package com.volunteerhub.community.service.like.model;

public class LikeCommandResult {
    private final boolean applied;
    private final long likeCount;

    public LikeCommandResult(boolean applied, long likeCount) {
        this.applied = applied;
        this.likeCount = likeCount;
    }

    public boolean isApplied() {
        return applied;
    }

    public long getLikeCount() {
        return likeCount;
    }
}
