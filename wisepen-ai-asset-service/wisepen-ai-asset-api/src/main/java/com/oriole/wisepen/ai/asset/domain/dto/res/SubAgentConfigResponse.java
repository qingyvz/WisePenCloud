package com.oriole.wisepen.ai.asset.domain.dto.res;

import lombok.Data;

import java.util.Map;

/** 子 Agent 运行态配置:displayName + 不透明的 agentConfig */
@Data
public class SubAgentConfigResponse {
    private String displayName;
    private Map<String, Object> agentConfig;
}
