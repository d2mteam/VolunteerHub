package com.volunteerhub.community.service.readmodel;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.readmodel.PostReadModel;
import com.volunteerhub.community.readmodel.UserProfileSummaryView;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.repository.readmodel.PostReadModelRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReadModelService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;
    private final PostReadModelRepository postReadModelRepository;

    public PostReadModel getPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return postReadModelRepository.findById(postId)
                .orElseGet(() -> rebuildPost(postId));
    }

    public OffsetPage<PostReadModel> listByEvent(Long eventId, Pageable pageable) {
        Page<Post> postPage = postRepository.findByEvent_EventId(eventId, pageable);
        List<PostReadModel> content = postPage.getContent().stream()
                .map(this::getFromCacheOrBuild)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<PostReadModel>builder()
                .content(content)
                .pageInfo(pageInfo)
                .build();
    }

    public Page<Post> getPostPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public OffsetPage<PostReadModel> findPosts(Page<Post> postPage) {
        List<PostReadModel> content = postPage.getContent().stream()
                .map(this::getFromCacheOrBuild)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<PostReadModel>builder()
                .content(content)
                .pageInfo(pageInfo)
                .build();
    }

    public PostReadModel rebuildPost(Long postId) {
        return postRepository.findById(postId)
                .map(this::buildAndCache)
                .orElse(null);
    }

    private PostReadModel getFromCacheOrBuild(Post post) {
        return postReadModelRepository.findById(post.getPostId())
                .orElseGet(() -> buildAndCache(post));
    }

    private PostReadModel buildAndCache(Post post) {
        PostReadModel model = buildModel(post);
        if (model != null) {
            postReadModelRepository.save(model);
        }
        return model;
    }

    private PostReadModel buildModel(Post post) {
        if (post == null) {
            return null;
        }
        UserProfileSummaryView createdBy = userProfileRepository.findSummaryByUserId(post.getCreatedBy().getUserId());
        return PostReadModel.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .eventId(post.getEvent().getEventId())
                .likeCount(likeRepository.countByTargetIdAndTableType(post.getPostId(), TableType.POST))
                .createdBy(createdBy)
                .build();
    }
}
