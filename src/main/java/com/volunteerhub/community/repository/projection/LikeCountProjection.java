package com.volunteerhub.community.repository.projection;

public interface LikeCountProjection {
    Long getTargetId();

    Long getCount();
}
