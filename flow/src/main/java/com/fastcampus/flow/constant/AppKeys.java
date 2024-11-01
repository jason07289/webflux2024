package com.fastcampus.flow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppKeys {
    WAITING("resources:queue:%s:wait"),
    PROCEED("resources:queue:%s:proceed"),
    WAITING_FOR_SCAN("resources:queue:*:wait"),
    TOKEN("resource-queue-%s-token"),
    ;

    public final String KEY;
}
