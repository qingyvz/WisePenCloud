package com.oriole.wisepen.ai.asset.domain.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

/** 预制子 Agent 模板的共享业务字段(实体与响应/请求共用) */
@Data
@SuperBuilder
@NoArgsConstructor
public class AgentTemplateInfoBase {
    @Id
    private String templateId;
    private String displayName;
    private String description;
    private List<String> triggers;              // 关键词,matcher 对 query 做大小写无关 substring,命中即召回该模板
    private Map<String, Object> agentConfig;
    private Boolean enabled;                    // 总开关:false=停用,matcher 不召回、create_sub_agent 显式调用也拒绝
}
