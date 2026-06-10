package com.oriole.wisepen.ai.asset.constant;

import com.oriole.wisepen.resource.enums.ResourceType;

import java.time.Duration;
import java.util.Set;

public interface AIAssetConstants {
    public static final Set<ResourceType> ALLOWED_TYPES = Set.of(
            ResourceType.SKILL,
            ResourceType.AGENT
    );

    // 子 Agent 运行态 Redis 存储策略:配置/历史的存活时长与 prior_turns 滑窗上限
    public static final Duration SUB_AGENT_STATE_TTL = Duration.ofHours(24);
    public static final int SUB_AGENT_MAX_PRIOR_TURNS = 20;
}
