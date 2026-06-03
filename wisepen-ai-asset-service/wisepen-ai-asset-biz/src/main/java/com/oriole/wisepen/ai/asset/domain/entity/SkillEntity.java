package com.oriole.wisepen.ai.asset.domain.entity;

import com.oriole.wisepen.ai.asset.domain.base.SkillInfoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "wisepen_skill_items")
public class SkillEntity extends SkillInfoBase {
    @Id
    private String resourceId;

    private String storageBizTag;

    /**
     * Legacy embedded version snapshot. New writes use wisepen_skill_versions.
     */
    @Deprecated
    private SkillVersionEntity currentVersionInfo;

    @Deprecated
    public String getSkillId() {
        return resourceId;
    }

    @Deprecated
    public void setSkillId(String skillId) {
        this.resourceId = skillId;
    }

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
