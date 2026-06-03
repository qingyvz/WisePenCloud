package com.oriole.wisepen.ai.asset.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillUpdateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionConfirmRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionInfoRequest;
import com.oriole.wisepen.ai.asset.feign.RemoteSkillService;
import com.oriole.wisepen.ai.asset.service.ISkillService;
import com.oriole.wisepen.ai.asset.service.ISkillVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/skill")
@RequiredArgsConstructor
public class InternalSkillController implements RemoteSkillService {

    private final ISkillService skillService;
    private final ISkillVersionService skillVersionService;

    @Override
    @PostMapping("/createSkill")
    public R<String> createSkill(@Validated @RequestBody SkillCreateRequest dto) {
        String userId = SecurityContextHolder.getUserId() == null ? null : SecurityContextHolder.getUserId().toString();
        return R.ok(skillService.createSkill(dto, userId));
    }

    @Override
    @PostMapping("/changeSkill")
    public R<Void> updateSkill(@Validated @RequestBody SkillUpdateRequest dto) {
        skillService.updateSkill(dto);
        return R.ok();
    }

    @Override
    @PostMapping("/getSkillInfo")
    public R<SkillInfoRequest> getSkillInfo(@Validated @RequestBody SkillInfoGetRequest dto) {
        return R.ok(skillService.getSkillInfo(dto.getResourceId()));
    }


    @Override
    @PostMapping("/createSkillVersion")
    public R<SkillVersionInfoRequest> createSkillVersion(@Validated @RequestBody SkillVersionCreateRequest dto) {
        return R.ok(skillVersionService.createDraftVersion(dto));
    }

    @Override
    @PostMapping("/getSkillVersion")
    public R<SkillVersionInfoRequest> getSkillVersion(@Validated @RequestBody SkillVersionGetRequest dto) {
        return R.ok(skillVersionService.getSkillVersion(dto));
    }



    @Override
    @PostMapping("/confirmSkillVersion")
    public R<Void> confirmSkillVersion(@Validated @RequestBody SkillVersionConfirmRequest dto) {
        skillVersionService.confirmSkillVersion(dto);
        return R.ok();
    }

    @Override
    @PostMapping("/createSkillAsset")
    public R<UploadInitRespDTO> createSkillAsset(@Validated @RequestBody SkillAssetCreateRequest dto) {
        return R.ok(skillVersionService.createSkillAsset(dto));
    }
}
