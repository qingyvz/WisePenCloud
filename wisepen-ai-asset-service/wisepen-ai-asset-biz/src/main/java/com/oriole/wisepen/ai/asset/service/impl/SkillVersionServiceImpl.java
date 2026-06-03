package com.oriole.wisepen.ai.asset.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.ai.asset.domain.base.SkillFileBase;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionInfoRequest;
import com.oriole.wisepen.ai.asset.domain.entity.SkillEntity;
import com.oriole.wisepen.ai.asset.domain.entity.SkillFileEntity;
import com.oriole.wisepen.ai.asset.domain.entity.SkillVersionSnapshotEntity;
import com.oriole.wisepen.ai.asset.enums.SkillVersionStatusEnum;
import com.oriole.wisepen.ai.asset.exception.SkillError;
import com.oriole.wisepen.ai.asset.repository.SkillRepository;
import com.oriole.wisepen.ai.asset.repository.SkillVersionRepository;
import com.oriole.wisepen.ai.asset.service.ISkillVersionService;
import com.oriole.wisepen.common.core.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillVersionServiceImpl implements ISkillVersionService {

    private final SkillRepository skillRepository;
    private final SkillVersionRepository skillVersionRepository;

    @Override
    @Transactional
    public SkillVersionInfoRequest createDraftVersion(SkillVersionCreateRequest req) {
        SkillEntity skill = skillRepository.findByResourceId(req.getResourceId())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
        int currentVersion = skill.getVersion() == null ? 0 : skill.getVersion();
        int draftVersion = currentVersion + 1;

        SkillVersionSnapshotEntity draft = skillVersionRepository
                .findByResourceIdAndVersion(req.getResourceId(), draftVersion)
                .orElseGet(() -> createDraftFromCurrent(req.getResourceId(), currentVersion, draftVersion));
        return toResponse(draft);
    }

    @Override
    public SkillVersionInfoRequest getSkillVersion(SkillVersionGetRequest req) {
        SkillEntity skill = skillRepository.findByResourceId(req.getResourceId())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
        int targetVersion = req.getVersion() == null ? (skill.getVersion() == null ? 0 : skill.getVersion()) : req.getVersion();
        SkillVersionSnapshotEntity version = skillVersionRepository
                .findByResourceIdAndVersion(req.getResourceId(), targetVersion)
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_VERSION_INVALID));
        return toResponse(version);
    }

    private SkillVersionSnapshotEntity createDraftFromCurrent(String resourceId, int currentVersion, int draftVersion) {
        SkillVersionSnapshotEntity entity = new SkillVersionSnapshotEntity();
        entity.setResourceId(resourceId);
        entity.setVersion(draftVersion);
        entity.setStatus(SkillVersionStatusEnum.DRAFT);
        if (currentVersion > 0) {
            skillVersionRepository.findByResourceIdAndVersion(resourceId, currentVersion)
                    .ifPresent(current -> entity.setFiles(copyFiles(current.getFiles())));
        }
        return skillVersionRepository.save(entity);
    }

    private List<SkillFileEntity> copyFiles(List<SkillFileEntity> files) {
        List<SkillFileEntity> copied = new ArrayList<>();
        if (files == null) {
            return copied;
        }
        for (SkillFileEntity file : files) {
            copied.add(BeanUtil.copyProperties(file, SkillFileEntity.class));
        }
        return copied;
    }

    private SkillVersionInfoRequest toResponse(SkillVersionSnapshotEntity entity) {
        SkillVersionInfoRequest response = new SkillVersionInfoRequest();
        response.setResourceId(entity.getResourceId());
        response.setVersion(entity.getVersion());
        response.setStatus(entity.getStatus());
        if (entity.getFiles() != null) {
            for (SkillFileEntity file : entity.getFiles()) {
                response.getFiles().add(BeanUtil.copyProperties(file, SkillFileBase.class));
            }
        }
        return response;
    }
}
