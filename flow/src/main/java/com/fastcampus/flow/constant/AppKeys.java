package com.fastcampus.flow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppKeys {
    WAITING("users:queue:%s:wait"),
    PROCEED("users:queue:%s:proceed"),
    WAITING_FOR_SCAN("users:queue:*:wait"),
    TOKEN("user-queue-%s-token"),
    ;

    public final String KEY;
}
