package com.oriole.wisepen.ai.asset.feign;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "内部 Skill 服务", description = "提供给其他微服务的 Skill Feign 接口")
@FeignClient(contextId = "remoteSkillService", value = "wisepen-skill-service")
public interface RemoteSkillService {

    @Operation(summary = "创建 Skill", description = "注册一个 Skill 主档并返回 Skill ID")
    @PostMapping("/internal/skill/createSkill")
    R<String> createSkill(@RequestBody SkillCreateRequest dto);

    @Operation(summary = "更新 Skill", description = "更新 Skill 的名称与描述等基础元信息")
    @PostMapping("/internal/skill/changeSkill")
    R<Void> updateSkill(@RequestBody SkillUpdateRequest dto);

    @Operation(summary = "获取 Skill 详情", description = "查询 Skill 主档信息与主要路径")
    @PostMapping("/internal/skill/getSkillInfo")
    R<SkillInfoRequest> getSkillInfo(@RequestBody SkillInfoGetRequest dto);

    @Operation(summary = "创建 Skill 草稿版本", description = "创建或返回当前唯一草稿版本")
    @PostMapping("/internal/skill/createSkillVersion")
    R<SkillVersionInfoRequest> createSkillVersion(@RequestBody SkillVersionCreateRequest dto);

    @Operation(summary = "获取 Skill 版本", description = "查询指定版本或当前确认版本的文件快照")
    @PostMapping("/internal/skill/getSkillVersion")
    R<SkillVersionInfoRequest> getSkillVersion(@RequestBody SkillVersionGetRequest dto);

    @Operation(summary = "确认 Skill 版本", description = "确认当前草稿版本并推进主档版本号")
    @PostMapping("/internal/skill/confirmSkillVersion")
    R<Void> confirmSkillVersion(@RequestBody SkillVersionConfirmRequest dto);

    @Operation(summary = "创建 Skill 文件", description = "为草稿版本添加或替换文件并初始化上传")
    @PostMapping("/internal/skill/createSkillAsset")
    R<UploadInitRespDTO> createSkillAsset(@RequestBody SkillAssetCreateRequest dto);
}
