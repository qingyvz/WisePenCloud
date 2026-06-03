package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.dto.req.SkillCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillUpdateRequest;

import java.util.List;

public interface ISkillService {
    String createSkill(SkillCreateRequest req, String userId);

    void deleteSkills(List<String> resourceIds);

    void updateSkill(SkillUpdateRequest req);

    SkillInfoRequest getSkillInfo(String resourceId);
}
