package com.oriole.wisepen.ai.asset.service.impl;

import com.oriole.wisepen.ai.asset.cache.SubAgentRedisStateManager;
import com.oriole.wisepen.ai.asset.domain.dto.req.SubAgentConfigPutRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SubAgentPriorTurnAppendRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.PriorTurnResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.SubAgentConfigResponse;
import com.oriole.wisepen.ai.asset.service.ISubAgentStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubAgentStateServiceImpl implements ISubAgentStateService {

    private final SubAgentRedisStateManager redisStateManager;

    @Override
    public void putConfig(SubAgentConfigPutRequest req) {
        SubAgentConfigResponse config = new SubAgentConfigResponse();
        config.setDisplayName(req.getDisplayName());
        config.setAgentConfig(req.getAgentConfig());
        redisStateManager.putConfig(req.getSessionId(), req.getAgentId(), config);
    }

    @Override
    public SubAgentConfigResponse getConfig(String sessionId, String agentId) {
        return redisStateManager.getConfig(sessionId, agentId);
    }

    @Override
    public void appendPriorTurn(SubAgentPriorTurnAppendRequest req) {
        PriorTurnResponse turn = new PriorTurnResponse();
        turn.setQuery(req.getQuery());
        turn.setResult(req.getResult());
        redisStateManager.appendPriorTurn(req.getSessionId(), req.getAgentId(), turn);
    }

    @Override
    public List<PriorTurnResponse> listPriorTurns(String sessionId, String agentId) {
        return redisStateManager.listPriorTurns(sessionId, agentId);
    }
}
