package com.volunteerhub.community.service.like.gateway;

final class LikeLuaScripts {
    private LikeLuaScripts() {
    }

    static final String LIKE_SCRIPT = """
            local membership = KEYS[1]
            local counter = KEYS[2]
            local stream = KEYS[3]
            local tableType = ARGV[1]
            local targetId = ARGV[2]
            local userId = ARGV[3]

            if redis.call('GET', membership) then
              local current = redis.call('GET', counter) or '0'
              return {0, tonumber(current)}
            end

            redis.call('SET', membership, '1')
            local total = redis.call('INCR', counter)
            redis.call('XADD', stream, '*', 'action', 'LIKE', 'tableType', tableType, 'targetId', targetId, 'userId', userId)
            return {1, total}
            """;

    static final String UNLIKE_SCRIPT = """
            local membership = KEYS[1]
            local counter = KEYS[2]
            local stream = KEYS[3]
            local tableType = ARGV[1]
            local targetId = ARGV[2]
            local userId = ARGV[3]

            if redis.call('DEL', membership) == 0 then
              local current = redis.call('GET', counter) or '0'
              return {0, tonumber(current)}
            end

            local total = redis.call('DECR', counter)
            if total < 0 then
              redis.call('SET', counter, 0)
              total = 0
            end
            redis.call('XADD', stream, '*', 'action', 'UNLIKE', 'tableType', tableType, 'targetId', targetId, 'userId', userId)
            return {1, total}
            """;
}
