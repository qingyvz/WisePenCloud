package com.oriole.wisepen.ai.asset.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.ai.asset.domain.base.SkillFileBase;
import cn.hutool.core.util.IdUtil;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionConfirmRequest;
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
import com.oriole.wisepen.common.core.domain.IResult;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitReqDTO;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.file.storage.api.enums.StorageSceneEnum;
import com.oriole.wisepen.file.storage.api.feign.RemoteStorageService;
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
    private final RemoteStorageService remoteStorageService;

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


    @Override
    @Transactional
    public UploadInitRespDTO createSkillAsset(SkillAssetCreateRequest req) {
        SkillEntity skill = skillRepository.findByResourceId(req.getResourceId())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
        int currentVersion = skill.getVersion() == null ? 0 : skill.getVersion();
        if (req.getVersion() != currentVersion + 1) {
            throw new ServiceException(SkillError.SKILL_VERSION_INVALID);
        }
        validateDirectoryPath(req.getPath());
        validateFileName(req.getName());

        SkillVersionSnapshotEntity version = skillVersionRepository
                .findByResourceIdAndVersion(req.getResourceId(), req.getVersion())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_VERSION_INVALID));
        if (version.getStatus() != SkillVersionStatusEnum.DRAFT) {
            throw new ServiceException(SkillError.SKILL_VERSION_INVALID);
        }

        String relativePath = toRelativePath(req.getPath(), req.getName());
        UploadInitRespDTO upload = initStorageUpload(req, relativePath);
        SkillFileEntity file = findOrCreateFile(version, req.getPath(), req.getName());
        file.setObjectKey(upload.getObjectKey());
        skillVersionRepository.save(version);
        return upload;
    }


    @Override
    @Transactional
    public void confirmSkillVersion(SkillVersionConfirmRequest req) {
        SkillEntity skill = skillRepository.findByResourceId(req.getResourceId())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
        int currentVersion = skill.getVersion() == null ? 0 : skill.getVersion();
        if (req.getVersion() != currentVersion + 1) {
            throw new ServiceException(SkillError.SKILL_VERSION_INVALID);
        }
        SkillVersionSnapshotEntity version = skillVersionRepository
                .findByResourceIdAndVersion(req.getResourceId(), req.getVersion())
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_VERSION_INVALID));
        boolean hasSkillMd = version.getFiles().stream()
                .anyMatch(file -> "/".equals(file.getPath()) && "SKILL.md".equals(file.getName()));
        if (!hasSkillMd) {
            throw new ServiceException(SkillError.SKILL_VERSION_INVALID);
        }
        version.setStatus(SkillVersionStatusEnum.CONFIRMED);
        skill.setVersion(req.getVersion());
        skillVersionRepository.save(version);
        skillRepository.save(skill);
    }

    private SkillVersionSnapshotEntity createDraftFromCurrent(String resourceId, int currentVersion, int draftVersion) {
        SkillVersionSnapshotEntity entity = SkillVersionSnapshotEntity.builder()
                .resourceId(resourceId)
                .version(draftVersion)
                .status(SkillVersionStatusEnum.DRAFT)
                .build();
        if (currentVersion > 0) {
            skillVersionRepository.findByResourceIdAndVersion(resourceId, currentVersion)
                    .ifPresent(current -> entity.setFiles(copyFiles(current.getFiles())));
        }
        return skillVersionRepository.save(entity);
    }


    private SkillFileEntity findOrCreateFile(SkillVersionSnapshotEntity version, String path, String name) {
        for (SkillFileEntity file : version.getFiles()) {
            if (path.equals(file.getPath()) && name.equals(file.getName())) {
                return file;
            }
        }
        SkillFileEntity file = SkillFileEntity.builder()
                .id(IdUtil.fastSimpleUUID())
                .path(path)
                .name(name)
                .build();
        version.getFiles().add(file);
        return file;
    }

    private String toRelativePath(String path, String name) {
        if ("/".equals(path)) {
            return name;
        }
        return path.substring(1) + "/" + name;
    }

    private UploadInitRespDTO initStorageUpload(SkillAssetCreateRequest req, String relativePath) {
        String bizTag = req.getResourceId() + "/" + req.getVersion() + "/" + relativePath;
        return unwrap(remoteStorageService.initUpload(UploadInitReqDTO.builder()
                .md5(req.getMd5())
                .extension(extractExtension(req.getName()))
                .scene(StorageSceneEnum.PRIVATE_SKILL_ASSET)
                .bizTag(bizTag)
                .expectedSize(req.getExpectedSize())
                .build()), SkillError.SKILL_UPLOAD_INIT_FAILED);
    }

    private String extractExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == name.length() - 1) {
            return "bin";
        }
        return name.substring(dotIndex + 1);
    }

    private <T> T unwrap(R<T> response, IResult message) {
        if (response == null) {
            throw new ServiceException(message);
        }
        if (response.getCode() == null || response.getCode() != 200) {
            throw new ServiceException(message, response.getMsg());
        }
        if (response.getData() == null) {
            throw new ServiceException(message);
        }
        return response.getData();
    }

    private void validateDirectoryPath(String path) {
        if (path == null || path.isBlank() || !path.startsWith("/") || path.contains("\\")
                || path.contains("//") || path.contains("/../") || path.endsWith("/..")
                || (!"/".equals(path) && path.endsWith("/"))) {
            throw new ServiceException(SkillError.SKILL_RELATIVE_PATH_INVALID);
        }
    }

    private void validateFileName(String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.contains("\\")
                || ".".equals(name) || "..".equals(name)) {
            throw new ServiceException(SkillError.SKILL_RELATIVE_PATH_INVALID);
        }
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
        SkillVersionInfoRequest response = SkillVersionInfoRequest.builder()
                .resourceId(entity.getResourceId())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .build();
        if (entity.getFiles() != null) {
            for (SkillFileEntity file : entity.getFiles()) {
                response.getFiles().add(BeanUtil.copyProperties(file, SkillFileBase.class));
            }
        }
        return response;
    }
}
