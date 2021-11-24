package com.jw.home;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class SampleRestController {

	@GetMapping("/authentication")
	public Mono<TokenInfoDto> hello(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
		TokenInfoDto tokenInfo = new TokenInfoDto();

		tokenInfo.setName(principal.getName());

		return Mono.just(tokenInfo);
	}

//	public Mono<TokenInfoDto> hello(BearerTokenAuthentication authentication) {
//		TokenInfoDto tokenInfo = new TokenInfoDto();
//
//		tokenInfo.setName(authentication.getName());
//
//		return Mono.just(tokenInfo);
//	}

}
