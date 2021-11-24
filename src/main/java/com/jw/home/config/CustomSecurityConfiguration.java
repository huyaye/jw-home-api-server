package com.jw.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class CustomSecurityConfiguration {

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		// @formatter:off
		http.authorizeExchange()
				.pathMatchers("/authentication").hasAuthority("SCOPE_ht.home")
				.pathMatchers("/api/v1/homes").hasAuthority("SCOPE_ht.home")
				.anyExchange().authenticated()
				.and()
				.oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::opaqueToken);
		// @formatter:on
		return http.build();
	}

//	@Bean
//	public ReactiveOpaqueTokenIntrospector introspector() {
//		return new CustomAuthoritiesOpaqueTokenIntrospector();
//	}
}
