package com.oriole.wisepen.ai.asset.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetCreateRequest;
import com.oriole.wisepen.common.log.annotation.Log;
import com.oriole.wisepen.common.security.annotation.CheckLogin;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillUpdateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionConfirmRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionInfoRequest;
import com.oriole.wisepen.ai.asset.exception.SkillError;
import com.oriole.wisepen.ai.asset.service.ISkillService;
import com.oriole.wisepen.ai.asset.service.ISkillVersionService;
import com.oriole.wisepen.resource.domain.dto.ResourceCheckPermissionReqDTO;
import com.oriole.wisepen.resource.domain.dto.ResourceCheckPermissionResDTO;
import com.oriole.wisepen.resource.enums.ResourceAccessRole;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Skill 管理", description = "Skill 主档的创建、更新与查询")
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@CheckLogin
public class SkillController {

    private final ISkillService skillService;
    private final ISkillVersionService skillVersionService;
    private final RemoteResourceService remoteResourceService;

    @Operation(summary = "创建 Skill", description = "创建一个归属于当前用户的 Skill 主档")
    @Log(title = "创建 Skill", businessType = BusinessType.INSERT)
    @PostMapping("/createSkill")
    public R<String> createSkill(@Validated @RequestBody SkillCreateRequest dto) {
        String userId = SecurityContextHolder.getUserId().toString();
        return R.ok(skillService.createSkill(dto, userId));
    }

    @Operation(summary = "更新 Skill", description = "更新 Skill 的名称与描述")
    @Log(title = "更新 Skill", businessType = BusinessType.UPDATE)
    @PostMapping("/changeSkill")
    public R<Void> updateSkill(@Validated @RequestBody SkillUpdateRequest dto) {
        assertSkillOwner(dto.getResourceId());
        skillService.updateSkill(dto);
        return R.ok();
    }

    @Operation(summary = "查询 Skill 详情", description = "查询 Skill 主档与核心目录信息")
    @PostMapping("/getSkillInfo")
    public R<SkillInfoRequest> getSkillInfo(@Validated @RequestBody SkillInfoGetRequest dto) {
        remoteResourceService.checkResPermission(ResourceCheckPermissionReqDTO.builder()
                .resourceId(dto.getResourceId())
                .userId(SecurityContextHolder.getUserId())
                .groupRoles(SecurityContextHolder.getGroupRoleMap())
                .build());
        return R.ok(skillService.getSkillInfo(dto.getResourceId()));
    }


    @Operation(summary = "创建 Skill 草稿版本", description = "创建或返回当前唯一草稿版本")
    @Log(title = "创建 Skill 草稿版本", businessType = BusinessType.INSERT)
    @PostMapping("/createSkillVersion")
    public R<SkillVersionInfoRequest> createSkillVersion(@Validated @RequestBody SkillVersionCreateRequest dto) {
        assertSkillOwner(dto.getResourceId());
        return R.ok(skillVersionService.createDraftVersion(dto));
    }

    @Operation(summary = "查询 Skill 版本", description = "查询指定版本或当前确认版本的文件快照")
    @PostMapping("/getSkillVersion")
    public R<SkillVersionInfoRequest> getSkillVersion(@Validated @RequestBody SkillVersionGetRequest dto) {
        remoteResourceService.checkResPermission(ResourceCheckPermissionReqDTO.builder()
                .resourceId(dto.getResourceId())
                .userId(SecurityContextHolder.getUserId())
                .groupRoles(SecurityContextHolder.getGroupRoleMap())
                .build());
        return R.ok(skillVersionService.getSkillVersion(dto));
    }



    @Operation(summary = "确认 Skill 版本", description = "确认当前草稿版本并推进主档版本号")
    @Log(title = "确认 Skill 版本", businessType = BusinessType.UPDATE)
    @PostMapping("/confirmSkillVersion")
    public R<Void> confirmSkillVersion(@Validated @RequestBody SkillVersionConfirmRequest dto) {
        assertSkillOwner(dto.getResourceId());
        skillVersionService.confirmSkillVersion(dto);
        return R.ok();
    }

    @Operation(summary = "创建 Skill 文件", description = "为草稿版本添加或替换文件并初始化上传")
    @Log(title = "创建 Skill 文件", businessType = BusinessType.INSERT)
    @PostMapping("/createSkillAsset")
    public R<UploadInitRespDTO> createSkillAsset(@Validated @RequestBody SkillAssetCreateRequest dto) {
        assertSkillOwner(dto.getResourceId());
        return R.ok(skillVersionService.createSkillAsset(dto));
    }

    private void assertSkillOwner(String resourceId) {
        ResourceCheckPermissionResDTO permission = remoteResourceService.checkResPermission(ResourceCheckPermissionReqDTO.builder()
                .resourceId(resourceId)
                .userId(SecurityContextHolder.getUserId())
                .groupRoles(SecurityContextHolder.getGroupRoleMap())
                .build()).getData();
        if (permission == null || permission.getResourceAccessRole() != ResourceAccessRole.OWNER) {
            throw new ServiceException(SkillError.SKILL_OWNER_MISMATCH);
        }
    }
}
