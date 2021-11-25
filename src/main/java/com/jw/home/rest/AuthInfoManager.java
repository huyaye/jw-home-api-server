package com.jw.home.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class AuthInfoManager {

    /**
     * check_token 결과에 포함된 memberId 가져오기
     */
    public static Mono<String> getRequestMemId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName).log();
    }
}
