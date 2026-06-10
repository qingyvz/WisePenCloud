package com.oriole.wisepen.ai.asset.domain.entity;

import com.oriole.wisepen.ai.asset.domain.base.AgentTemplateInfoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/** 预制子 Agent 模板,templateId(继承自 base)即 _id;agentConfig 作为不透明子文档存储 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "wisepen_agent_templates")
public class AgentTemplateEntity extends AgentTemplateInfoBase {
    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
