package com.jw.home.service;

import com.jw.home.common.spec.HomeSecurityMode;
import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.exception.HomeDuplicatedException;
import com.jw.home.exception.HomeLimitException;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HomeServiceTest {
    HomeService homeService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private HomeRepository homeRepository;

    private Home homeToAdd;

    @BeforeEach
    void setUp() {
        homeService = new HomeService(memberRepository, homeRepository);

        homeToAdd = new Home();
        homeToAdd.setId("61a22c8895f77204b8f602ab");
        homeToAdd.setHomeName("testHome");
        homeToAdd.setTimezone("Asia/Seoul");
        homeToAdd.setSecurityMode(HomeSecurityMode.none);
        homeToAdd.setRooms(Collections.emptyList());
    }

    @Test
    // Home 추가 성공
    void addHomeSucceed() {
        Member member = new Member();
        member.setMemId("jw");

        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(memberRepository.save(any(Member.class))).thenReturn(Mono.just(member));
        when(homeRepository.save(any(Home.class))).thenReturn(Mono.just(homeToAdd));
        when(homeRepository.findAllById(Collections.emptyList())).thenReturn(Flux.empty());

        final Mono<Home> homeMono = homeService.addHome(Mono.just("jw"), homeToAdd);
        StepVerifier.create(homeMono)
                .expectNext(homeToAdd)
                .verifyComplete();

        Assertions.assertThat(member.getHomes().get(0).getHomeId()).isEqualTo(homeToAdd.getId());
    }

    @Test
    // Home 추가 실패 - 사용자보유 홈갯수 초과
    void addHomeFailedHomeLimit() {
        Member member = new Member();
        while (!member.hasMaxHome()) {
            member.addHome(new MemberHome());
        }
        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));

        final Mono<Home> homeMono = homeService.addHome(Mono.just("jw"), homeToAdd);
        StepVerifier.create(homeMono)
                .verifyError(HomeLimitException.class);
    }

    @Test
    // Home 추가 실패 - 사용자에게 같은이름의 Home 이 이미 있는 경우.
    void addHomeFailedHomeDuplicated() {
        Member member = new Member();
        Home existHome = new Home();
        existHome.setHomeName(homeToAdd.getHomeName());

        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(homeRepository.findAllById(Collections.emptyList())).thenReturn(Flux.just(existHome));

        final Mono<Home> homeMono = homeService.addHome(Mono.just("jw"), homeToAdd);
        StepVerifier.create(homeMono)
                .verifyError(HomeDuplicatedException.class);
    }

    @Test
    void getAllHomesOfMember() {
        Member member = new Member();

        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(homeRepository.findAllById(Collections.emptyList())).thenReturn(Flux.just(homeToAdd));

        final Flux<Home> homeFlux = homeService.getHomes(Mono.just("jw"));
        StepVerifier.create(homeFlux)
                .expectNext(homeToAdd)
                .verifyComplete();
    }
}