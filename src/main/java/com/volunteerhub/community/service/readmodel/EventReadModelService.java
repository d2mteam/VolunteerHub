package com.volunteerhub.community.service.readmodel;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.readmodel.EventReadModel;
import com.volunteerhub.community.readmodel.UserProfileSummaryView;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.repository.readmodel.EventReadModelRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventReadModelService {

    private final EventRepository eventRepository;
    private final RoleInEventRepository roleInEventRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;
    private final EventReadModelRepository eventReadModelRepository;

    public EventReadModel getEvent(Long eventId) {
        if (eventId == null) {
            return null;
        }
        return eventReadModelRepository.findById(eventId)
                .orElseGet(() -> rebuildEvent(eventId));
    }

    public OffsetPage<EventReadModel> findEvents(Page<Event> eventPage) {
        List<EventReadModel> content = eventPage.getContent().stream()
                .map(this::getFromCacheOrBuild)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageUtils.from(eventPage);
        return OffsetPage.<EventReadModel>builder()
                .content(content)
                .pageInfo(pageInfo)
                .build();
    }

    public OffsetPage<EventReadModel> findByManager(Page<Event> eventPage) {
        return findEvents(eventPage);
    }

    public List<EventReadModel> mapToReadModels(List<Event> events) {
        return events.stream()
                .map(this::getFromCacheOrBuild)
                .filter(Objects::nonNull)
                .toList();
    }

    public EventReadModel rebuildEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .map(this::buildAndCache)
                .orElse(null);
    }

    private EventReadModel getFromCacheOrBuild(Event event) {
        return eventReadModelRepository.findById(event.getEventId())
                .orElseGet(() -> buildAndCache(event));
    }

    private EventReadModel buildAndCache(Event event) {
        EventReadModel model = buildModel(event);
        if (model != null) {
            eventReadModelRepository.save(model);
        }
        return model;
    }

    private EventReadModel buildModel(Event event) {
        if (event == null) {
            return null;
        }

        Map<String, Object> metadata = event.getMetadata();
        List<String> categories = Collections.emptyList();
        if (metadata != null) {
            Object categoriesRaw = metadata.getOrDefault("categories", Collections.emptyList());
            if (categoriesRaw instanceof List<?> list) {
                categories = list.stream().map(Object::toString).toList();
            }
        }

        UserProfileSummaryView createdBy = userProfileRepository.findSummaryByUserId(event.getCreatedBy().getUserId());

        return EventReadModel.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventLocation(event.getEventLocation())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .eventState(event.getEventState().name())
                .categories(categories)
                .memberCount(Math.toIntExact(roleInEventRepository.countByEvent(event.getEventId())))
                .postCount(Math.toIntExact(postRepository.countByEvent(event.getEventId())))
                .likeCount(likeRepository.countByTargetIdAndTableType(event.getEventId(), TableType.EVENT))
                .createdBy(createdBy)
                .build();
    }
}
