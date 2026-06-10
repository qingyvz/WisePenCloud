package com.oriole.wisepen.ai.asset.domain.dto.req;

import lombok.Data;

/** 追加一次 (query, result) 到子 Agent 的滑动窗口 */
@Data
public class SubAgentPriorTurnAppendRequest {
    private String sessionId;
    private String agentId;
    private String query;
    private String result;
}
