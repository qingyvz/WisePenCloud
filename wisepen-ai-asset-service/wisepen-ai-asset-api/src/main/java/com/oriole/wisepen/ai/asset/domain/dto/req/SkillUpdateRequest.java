package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oriole.wisepen.ai.asset.constant.SkillValidationMsg;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillUpdateRequest {
    private String resourceId;

    @Deprecated
    private String skillId;

    private String name;

    @Deprecated
    private String skillName;

    private String description;

    @AssertTrue(message = SkillValidationMsg.SKILL_ID_NOT_BLANK)
    @JsonIgnore
    public boolean isResourceIdPresent() {
        return StringUtils.hasText(resourceId) || StringUtils.hasText(skillId);
    }

    @JsonIgnore
    public String getEffectiveResourceId() {
        return StringUtils.hasText(resourceId) ? resourceId : skillId;
    }

    @JsonIgnore
    public String getEffectiveName() {
        return StringUtils.hasText(name) ? name : skillName;
    }
}
