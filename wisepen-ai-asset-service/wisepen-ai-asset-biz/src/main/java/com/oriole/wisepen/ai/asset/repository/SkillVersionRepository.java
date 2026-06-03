package com.oriole.wisepen.ai.asset.repository;

import com.oriole.wisepen.ai.asset.domain.entity.SkillVersionSnapshotEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillVersionRepository extends MongoRepository<SkillVersionSnapshotEntity, String> {
    Optional<SkillVersionSnapshotEntity> findByResourceIdAndVersion(String resourceId, Integer version);

    List<SkillVersionSnapshotEntity> findByResourceIdOrderByVersionDesc(String resourceId);

    void deleteByResourceIdIn(List<String> resourceIds);
}
