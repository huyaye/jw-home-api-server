package com.jw.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class CustomSecurityConfiguration {

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		// @formatter:off
		http.csrf().disable()
			.authorizeExchange()
			.pathMatchers("/authentication").hasAuthority("SCOPE_jw.home")
			.pathMatchers("/api/v1/homes").hasAuthority("SCOPE_jw.home")
			.pathMatchers("/api/v1/devices").permitAll()	// TODO access control
			.pathMatchers("/api/v1/devices/control").hasAuthority("SCOPE_jw.home")
			.pathMatchers("/api/v1/admin/devices/id").permitAll()	// TODO access control
			.pathMatchers("/api/v1/admin/check/home").permitAll()	// TODO access control
			.anyExchange().authenticated()
			.and()
			/*
			 * Select using JWT or OpaqueToken
			 */
//			.oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::opaqueToken);
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
			);
		// @formatter:on
		return http.build();
	}

	@Bean
	Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
		JwtAuthenticationConverter jwtAuthenticationConverter =
				new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
		return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
	}
}
