package com.jw.home.service;

import com.jw.home.common.spec.HomeSecurityMode;
import com.jw.home.common.spec.HomeState;
import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.exception.HomeDuplicatedException;
import com.jw.home.exception.HomeLimitException;
import com.jw.home.exception.InvalidHomeException;
import com.jw.home.exception.InvalidMemberException;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import com.jw.home.rest.dto.GetHomesRes;
import com.jw.home.rest.dto.InviteHomeReq;
import com.jw.home.service.device.DeviceService;
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
import java.util.HashSet;
import java.util.List;

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
    @MockBean
    private DeviceService deviceService;

    private Home homeToAdd;

    @BeforeEach
    void setUp() {
        homeService = new HomeService(memberRepository, homeRepository, deviceService);

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
            member.addHome(MemberHome.builder().build());
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
        member.setMemId("jw");
        member.addHome(MemberHome.builder().homeId("61a22c8895f77204b8f602ab").state(HomeState.shared).build());

        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(homeRepository.findAllById(List.of("61a22c8895f77204b8f602ab"))).thenReturn(Flux.just(homeToAdd));

        Flux<GetHomesRes.HomeDto> homeFlux = homeService.getHomes(Mono.just("jw"));
        StepVerifier.create(homeFlux)
                .consumeNextWith(homeDto -> {
                    Assertions.assertThat(homeDto.getHomeName()).isEqualTo("testHome");
                    Assertions.assertThat(homeDto.getState()).isEqualTo(HomeState.shared);
                })
                .verifyComplete();
    }

    @Test
    void deleteHomesOfMember() {
        Member member = new Member();
        member.setMemId("jw");
        member.addHome(MemberHome.builder().homeId("5678").state(HomeState.shared).build());

        Home home = new Home();
        home.setId("5678");
        home.setHomeName("testHome");
        home.setTimezone("Asia/Seoul");
        home.setSharedMemberIds(new HashSet<>(List.of("jw", "my")));
        home.setSecurityMode(HomeSecurityMode.none);
        home.setRooms(Collections.emptyList());

        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(memberRepository.save(any())).thenReturn(Mono.just(member));
        when(homeRepository.findAllById(List.of("5678"))).thenReturn(Flux.just(home));
        when(homeRepository.save(any())).thenReturn(Mono.just(home));
        when(homeRepository.delete(any())).thenReturn(Mono.empty());

        final Flux<String> deletedIds = homeService.withdrawHomes(Mono.just("jw"), List.of("1234", "5678"));
        StepVerifier.create(deletedIds)
                .expectNext("5678")
                .verifyComplete();

        Assertions.assertThat(home.getSharedMemberIds().size()).isEqualTo(1);
        Assertions.assertThat(home.getSharedMemberIds().stream().findAny().get()).isEqualTo("my");
        Assertions.assertThat(member.getHomes()).isEmpty();
    }

    @Test
    void inviteHomeSucceed() {
        Member host = new Member();
        host.setMemId("host");
        host.addHome(MemberHome.builder().homeId("1234").state(HomeState.shared).build());

        Member guest = new Member();
        guest.setMemId("guest");

        Home home = new Home();
        home.setId("1234");
        home.setSharedMemberIds(new HashSet<>(List.of("host")));

        when(memberRepository.findByMemId("host")).thenReturn(Mono.just(host));
        when(memberRepository.findByMemId("guest")).thenReturn(Mono.just(guest));
        when(homeRepository.findById("1234")).thenReturn(Mono.just(home));
        when(memberRepository.save(guest)).thenReturn(Mono.just(guest));
        when(homeRepository.save(home)).thenReturn(Mono.just(home));

        InviteHomeReq req = new InviteHomeReq("1234", "guest");
        Mono<Home> homeMono = homeService.inviteHome(Mono.just("host"), req);
        StepVerifier.create(homeMono)
                .expectNext(home)
                .verifyComplete();

        Assertions.assertThat(home.getInvitedMemberIds()).contains("guest");
    }

    @Test
    // Home 초대 실패 - 초대하는 사용자가 권한이 없는 Home
    void inviteHomeFailedInvalidHome() {
        Member host = new Member();
        host.setMemId("host");

        when(memberRepository.findByMemId("host")).thenReturn(Mono.just(host));

        InviteHomeReq req = new InviteHomeReq("1234", "guest");
        Mono<Home> homeMono = homeService.inviteHome(Mono.just("host"), req);
        StepVerifier.create(homeMono)
                .verifyError(InvalidHomeException.class);
    }

    @Test
    // Home 초대 실패 - 초대받는 사용자가 Home 에 이미 관계(공유 or 초대) 가 있는 경우
    void inviteHomeFailedInvalidMember() {
        Member host = new Member();
        host.setMemId("host");
        host.addHome(MemberHome.builder().homeId("1234").state(HomeState.shared).build());

        Member guest = new Member();
        guest.setMemId("guest");

        Home home = new Home();
        home.setId("1234");
        home.setSharedMemberIds(new HashSet<>(List.of("host", "guest")));

        when(memberRepository.findByMemId("host")).thenReturn(Mono.just(host));
        when(memberRepository.findByMemId("guest")).thenReturn(Mono.just(guest));
        when(homeRepository.findById("1234")).thenReturn(Mono.just(home));

        InviteHomeReq req = new InviteHomeReq("1234", "guest");
        Mono<Home> homeMono = homeService.inviteHome(Mono.just("host"), req);
        StepVerifier.create(homeMono)
                .verifyError(InvalidMemberException.class);
    }

    @Test
    // Home 초대 수락 성공
    void approveHomeInvitationSucceed() {
        Member member = new Member();
        member.setMemId("jw");
        member.addHome(MemberHome.builder().homeId("1234").state(HomeState.invited).build());

        Home home = new Home();
        home.setId("1234");
        home.setInvitedMemberIds(new HashSet<>(List.of("jw")));

        when(memberRepository.findByMemId("jw")).thenReturn(Mono.just(member));
        when(homeRepository.findById("1234")).thenReturn(Mono.just(home));
        when(memberRepository.save(member)).thenReturn(Mono.just(member));
        when(homeRepository.save(home)).thenReturn(Mono.just(home));

        Mono<Home> homeMono = homeService.approveHomeInvitation(Mono.just("jw"), "1234");
        StepVerifier.create(homeMono)
                .expectNext(home)
                .verifyComplete();

        Assertions.assertThat(member.getHomes()).anyMatch(h -> h.getHomeId().equals("1234")
                && h.getState().equals(HomeState.shared));
        Assertions.assertThat(home.getInvitedMemberIds()).isEmpty();
        Assertions.assertThat(home.getSharedMemberIds()).contains("jw");
    }

    @Test
    // Home 초대 수락 실패
    void approveHomeInvitationFailed() {
        Member member = new Member();
        member.setMemId("jw");

        when(memberRepository.findByMemId("jw")).thenReturn(Mono.just(member));

        Mono<Home> homeMono = homeService.approveHomeInvitation(Mono.just("jw"), "1234");
        StepVerifier.create(homeMono)
            .verifyError(InvalidHomeException.class);
    }
}