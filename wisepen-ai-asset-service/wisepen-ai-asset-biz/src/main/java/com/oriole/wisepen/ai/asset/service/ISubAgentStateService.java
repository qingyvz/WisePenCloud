package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.dto.req.SubAgentConfigPutRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SubAgentPriorTurnAppendRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.PriorTurnResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.SubAgentConfigResponse;

import java.util.List;

public interface ISubAgentStateService {

    void putConfig(SubAgentConfigPutRequest req);

    SubAgentConfigResponse getConfig(String sessionId, String agentId);

    void appendPriorTurn(SubAgentPriorTurnAppendRequest req);

    List<PriorTurnResponse> listPriorTurns(String sessionId, String agentId);
}
