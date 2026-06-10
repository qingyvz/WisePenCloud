package com.oriole.wisepen.ai.asset.domain.dto.res;

import lombok.Data;

import java.util.List;

/** matcher warmup 用的轻量元信息,不含 agentConfig */
@Data
public class AgentTemplateMetaResponse {
    private String templateId;
    private String displayName;
    private String description;
    private List<String> triggers;
}
