package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.oriole.wisepen.ai.asset.constant.SkillValidationMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillAssetCreateRequest {
    @NotBlank(message = SkillValidationMsg.SKILL_ID_NOT_BLANK)
    private String resourceId;

    @NotNull(message = SkillValidationMsg.SKILL_VERSION_NOT_BLANK)
    private Integer version;

    @NotBlank(message = SkillValidationMsg.SKILL_RELATIVE_PATH_NOT_BLANK)
    private String name;

    @NotBlank(message = SkillValidationMsg.SKILL_RELATIVE_PATH_NOT_BLANK)
    private String path;

    private String md5;

    private Long expectedSize;
}
