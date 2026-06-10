package com.oriole.wisepen.ai.asset.domain.dto.res;

import lombok.Data;

/** 子 Agent 同 agent_id 跨调用的一次 (query, result) 摘要 */
@Data
public class PriorTurnResponse {
    private String query;
    private String result;
}
