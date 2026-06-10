package com.oriole.wisepen.ai.asset.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oriole.wisepen.ai.asset.constant.AIAssetConstants;
import com.oriole.wisepen.ai.asset.domain.dto.res.PriorTurnResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.SubAgentConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/** 子 Agent 运行态的 Redis 访问层,键/TTL/滑窗复刻原 chat-service 的 RedisSubAgentState */
@Component
@RequiredArgsConstructor
public class SubAgentRedisStateManager {

    private static final String CONFIG_PREFIX = "wisepen:sub_agent_config:";
    private static final String CTX_PREFIX = "wisepen:sub_agent_ctx:";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void putConfig(String sessionId, String agentId, SubAgentConfigResponse config) {
        stringRedisTemplate.opsForValue().set(configKey(sessionId, agentId), toJson(config), AIAssetConstants.SUB_AGENT_STATE_TTL);
    }

    public SubAgentConfigResponse getConfig(String sessionId, String agentId) {
        String raw = stringRedisTemplate.opsForValue().get(configKey(sessionId, agentId));
        return raw == null ? null : fromJson(raw, SubAgentConfigResponse.class);
    }

    public void appendPriorTurn(String sessionId, String agentId, PriorTurnResponse turn) {
        String key = ctxKey(sessionId, agentId);
        stringRedisTemplate.opsForList().rightPush(key, toJson(turn));
        stringRedisTemplate.opsForList().trim(key, -AIAssetConstants.SUB_AGENT_MAX_PRIOR_TURNS, -1);
        stringRedisTemplate.expire(key, AIAssetConstants.SUB_AGENT_STATE_TTL);
    }

    public List<PriorTurnResponse> listPriorTurns(String sessionId, String agentId) {
        List<String> raw = stringRedisTemplate.opsForList().range(ctxKey(sessionId, agentId), 0, -1);
        return raw == null ? List.of() : raw.stream().map(s -> fromJson(s, PriorTurnResponse.class)).toList();
    }

    private String configKey(String sessionId, String agentId) {
        return CONFIG_PREFIX + sessionId + ":" + agentId;
    }

    private String ctxKey(String sessionId, String agentId) {
        return CTX_PREFIX + sessionId + ":" + agentId;
    }

    @SneakyThrows
    private String toJson(Object value) {
        return objectMapper.writeValueAsString(value);
    }

    @SneakyThrows
    private <T> T fromJson(String raw, Class<T> type) {
        return objectMapper.readValue(raw, type);
    }
}
