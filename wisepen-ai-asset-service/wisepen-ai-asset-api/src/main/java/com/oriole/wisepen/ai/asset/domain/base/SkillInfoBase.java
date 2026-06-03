package com.oriole.wisepen.ai.asset.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oriole.wisepen.ai.asset.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.ai.asset.enums.SkillSourceTypeEnum;
import com.oriole.wisepen.ai.asset.enums.SkillStatusEnum;
import lombok.Data;

@Data
public class SkillInfoBase {
    private String name;
    private String ownerId;
    private String description;
    private Integer version;
    private SkillStatusEnum skillStatus;
    private SkillAuditStatusEnum auditStatus;
    private SkillSourceTypeEnum sourceType;

    @Deprecated
    @JsonIgnore
    public String getSkillName() {
        return name;
    }

    @Deprecated
    public void setSkillName(String skillName) {
        this.name = skillName;
    }
}
