package com.jw.home.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;

import reactor.core.publisher.Mono;

@Configuration
public class CustomAuthoritiesOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {
	@Value(value = "${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
	private String introspectionUri;

	@Value(value = "${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
	private String clientId;

	@Value(value = "${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
	private String clientSecret;

	private ReactiveOpaqueTokenIntrospector delegate;

	@Override
	public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
		delegate = new NimbusReactiveOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
		return delegate.introspect(token)
				.map(principal -> new DefaultOAuth2AuthenticatedPrincipal(extractName(principal),
						principal.getAttributes(), extractAuthorities(principal)));
	}

	private String extractName(OAuth2AuthenticatedPrincipal principal) {
		return principal.getAttribute("user_name");
	}

	private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
		List<String> scopes = principal.getAttribute(OAuth2IntrospectionClaimNames.SCOPE);
		Collection<GrantedAuthority> collect = scopes.stream()
				.map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope)).collect(Collectors.toList());
		return collect;
	}

}
