package com.jw.home.rest.handler;

import com.jw.home.common.spec.HomeSecurityMode;
import com.jw.home.config.CustomSecurityConfiguration;
import com.jw.home.domain.Home;
import com.jw.home.domain.mapper.HomeMapper;
import com.jw.home.rest.dto.AddHomeReq;
import com.jw.home.rest.dto.AddHomeRes;
import com.jw.home.rest.dto.GetHomesRes;
import com.jw.home.rest.dto.ResponseDto;
import com.jw.home.rest.router.HomeRouter;
import com.jw.home.service.HomeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;

@WebFluxTest
@Import(CustomSecurityConfiguration.class)
@ContextConfiguration(classes = {HomeRouter.class, HomeHandler.class})
class HomeHandlerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private HomeService homeService;

    private Home home;

    @BeforeEach
    void setUp() {
        home = new Home();
        home.setId("61a22c8895f77204b8f602ab");
        home.setHomeName("testHome");
        home.setTimezone("Asia/Seoul");
        home.setSecurityMode(HomeSecurityMode.none);
        home.setRooms(Collections.emptyList());
    }

    @Test
    void createHome() {
        when(homeService.addHome(any(), any(Home.class))).thenReturn(Mono.just(home));

        AddHomeReq addHomeDto = new AddHomeReq();
        addHomeDto.setHomeName("testHome");
        addHomeDto.setTimezone("Asia/Seoul");
        addHomeDto.setSecurityMode(HomeSecurityMode.none);
        addHomeDto.setRooms(Collections.emptyList());

        webClient.mutateWith(mockOpaqueToken()
                        .authorities(AuthorityUtils.createAuthorityList("SCOPE_ht.home")))
                .post().uri("/api/v1/homes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(addHomeDto), AddHomeReq.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseDto<AddHomeRes>>() {})
                .value(res -> {
                    Assertions.assertThat(res.getErrorCode()).isNull();
                    Assertions.assertThat(res.getResultData()).isEqualTo(HomeMapper.INSTANCE.toAddHomeRes(home));
                });
    }

    @Test
    void getHomes() {
        when(homeService.getHomes(any())).thenReturn(Flux.just(home));
        webClient.mutateWith(mockOpaqueToken()
                .authorities(AuthorityUtils.createAuthorityList("SCOPE_ht.home")))
                .get().uri("/api/v1/homes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseDto<GetHomesRes>>() {})
                .value(res -> {
                    final GetHomesRes resultData = res.getResultData();
                    Assertions.assertThat(resultData.getHomes().size()).isEqualTo(1);
                    Assertions.assertThat(resultData.getHomes().get(0)).isEqualTo(HomeMapper.INSTANCE.toGetHomesHomeDto(home));
                });

        when(homeService.getHomes(any())).thenReturn(Flux.empty());
        webClient.mutateWith(mockOpaqueToken()
                        .authorities(AuthorityUtils.createAuthorityList("SCOPE_ht.home")))
                .get().uri("/api/v1/homes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseDto<GetHomesRes>>() {})
                .value(res -> {
                    final GetHomesRes resultData = res.getResultData();
                    Assertions.assertThat(resultData.getHomes().size()).isEqualTo(0);
                });
    }
}