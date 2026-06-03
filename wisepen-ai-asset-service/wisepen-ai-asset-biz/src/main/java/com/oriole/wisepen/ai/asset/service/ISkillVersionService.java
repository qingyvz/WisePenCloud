package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionInfoRequest;

public interface ISkillVersionService {
    SkillVersionInfoRequest createDraftVersion(SkillVersionCreateRequest req);

    SkillVersionInfoRequest getSkillVersion(SkillVersionGetRequest req);
}
