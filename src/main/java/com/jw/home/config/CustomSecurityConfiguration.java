package com.jw.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

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
			.oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
		// @formatter:on
		return http.build();
	}

	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		byte[] key = "jwt_test_sign_key".getBytes();
		byte[] paddedKey = key.length < 32 ? Arrays.copyOf(key, 32) : key;

		SecretKey signKey = new SecretKeySpec(paddedKey, "HS256");
		return NimbusReactiveJwtDecoder.withSecretKey(signKey).build();
	}
}
