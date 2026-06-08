package com.oriole.wisepen.ai.asset.domain.mq;

import com.oriole.wisepen.ai.asset.domain.dto.res.LatestPublishedSkillInfoResponse;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillPublishedMessage extends LatestPublishedSkillInfoResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
