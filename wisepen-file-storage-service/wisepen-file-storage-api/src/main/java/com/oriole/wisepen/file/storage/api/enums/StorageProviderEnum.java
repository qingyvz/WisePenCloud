package com.oriole.wisepen.file.storage.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageProviderEnum {
    ALIYUN_OSS(1, "ALIYUN_OSS"),
    MINIO(2, "MINIO"),
    TENCENT_COS(3, "TENCENT_COS");

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;
}
