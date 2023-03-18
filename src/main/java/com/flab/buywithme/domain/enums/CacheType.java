package com.flab.buywithme.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    POST_PAGE("postPage", 300, 1000);

    private final String cacheName;
    private final int expireAfterWrite;
    private final int maximumSize;
}
