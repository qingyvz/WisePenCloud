package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionConfirmRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionInfoRequest;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;

public interface ISkillVersionService {
    SkillVersionInfoRequest createDraftVersion(SkillVersionCreateRequest req);

    SkillVersionInfoRequest getSkillVersion(SkillVersionGetRequest req);

    UploadInitRespDTO createSkillAsset(SkillAssetCreateRequest req);

    void confirmSkillVersion(SkillVersionConfirmRequest req);
}
