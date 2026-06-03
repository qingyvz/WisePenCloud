package com.oriole.wisepen.ai.asset.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillUpdateRequest;
import com.oriole.wisepen.ai.asset.domain.entity.SkillEntity;
import com.oriole.wisepen.ai.asset.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.ai.asset.enums.SkillSourceTypeEnum;
import com.oriole.wisepen.ai.asset.enums.SkillStatusEnum;
import com.oriole.wisepen.ai.asset.exception.SkillError;
import com.oriole.wisepen.ai.asset.repository.SkillRepository;
import com.oriole.wisepen.ai.asset.repository.SkillVersionRepository;
import com.oriole.wisepen.ai.asset.service.ISkillService;
import com.oriole.wisepen.resource.domain.dto.ResourceCreateReqDTO;
import com.oriole.wisepen.resource.enums.ResourceType;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

    private final SkillRepository skillRepository;
    private final RemoteResourceService remoteResourceService;
    private final SkillVersionRepository skillVersionRepository;

    @Override
    public String createSkill(SkillCreateRequest dto, String userId) {
        ResourceCreateReqDTO resourceReq = ResourceCreateReqDTO.builder()
                .resourceName(dto.getName())
                .resourceType(ResourceType.SKILL)
                .ownerId(userId)
                .build();
        String resourceId = remoteResourceService.createResource(resourceReq).getData();
        if (!StringUtils.hasText(resourceId)) {
            throw new ServiceException(SkillError.SKILL_RESOURCE_REGISTER_FAILED);
        }

        SkillEntity entity = SkillEntity.builder()
                .resourceId(resourceId)
                .name(dto.getName())
                .ownerId(userId)
                .description(dto.getDescription() == null ? "" : dto.getDescription())
                .version(0)
                .sourceType(dto.getSourceType() == null ? SkillSourceTypeEnum.MANUAL : dto.getSourceType())
                .skillStatus(SkillStatusEnum.DRAFT)
                .auditStatus(SkillAuditStatusEnum.NOT_SUBMITTED)
                .build();
        skillRepository.save(entity);
        return resourceId;
    }

    @Override
    @Transactional
    public void deleteSkills(List<String> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }
        skillRepository.deleteByResourceIdIn(resourceIds);
        skillVersionRepository.deleteByResourceIdIn(resourceIds);
    }

    @Override
    public void updateSkill(SkillUpdateRequest dto) {
        SkillEntity entity = skillRepository.findByResourceId(dto.getResourceId())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        skillRepository.save(entity);
    }

    @Override
    public SkillInfoRequest getSkillInfo(String resourceId) {
        SkillEntity entity = skillRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
        return BeanUtil.copyProperties(entity, SkillInfoRequest.class);
    }
}
