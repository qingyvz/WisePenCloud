package com.oriole.wisepen.ai.asset.domain.base;

import com.oriole.wisepen.ai.asset.enums.SkillVersionStatusEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkillVersionSnapshotBase {
    private String resourceId;
    private Integer version;
    private SkillVersionStatusEnum status;
    private List<SkillFileBase> files = new ArrayList<>();
}
