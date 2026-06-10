package com.oriole.wisepen.ai.asset.domain.dto.req;

import lombok.Data;

import java.util.Map;

/** 注册/覆盖一个子 Agent 的运行态配置 */
@Data
public class SubAgentConfigPutRequest {
    private String sessionId;
    private String agentId;
    private String displayName;
    private Map<String, Object> agentConfig;
}
