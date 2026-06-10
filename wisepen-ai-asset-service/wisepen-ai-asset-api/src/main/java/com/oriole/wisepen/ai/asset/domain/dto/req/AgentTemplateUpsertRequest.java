package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.oriole.wisepen.ai.asset.domain.base.AgentTemplateInfoBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 按 templateId 写入/覆盖一条模板(种子与管理用) */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AgentTemplateUpsertRequest extends AgentTemplateInfoBase {
}
