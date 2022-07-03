package com.jw.home.service;

import com.jw.home.common.spec.HomeState;
import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.domain.mapper.HomeMapper;
import com.jw.home.exception.*;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import com.jw.home.rest.dto.GetHomesRes;
import com.jw.home.rest.dto.InviteHomeReq;
import com.jw.home.service.device.DeviceService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HomeService {
    private final MemberRepository memberRepository;
    private final HomeRepository homeRepository;
    private final DeviceService deviceService;

    public HomeService(MemberRepository memberRepository, HomeRepository homeRepository, DeviceService deviceService) {
        this.memberRepository = memberRepository;
        this.homeRepository = homeRepository;
        this.deviceService = deviceService;
    }

    // HomeLimitException, HomeDuplicatedException
    public Mono<Home> addHome(Mono<String> memId, Home home) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId).log();
        return memberMono
                .filter(m -> !m.hasMaxHome())
                .switchIfEmpty(Mono.error(HomeLimitException.INSTANCE))
                .flatMap(m -> checkHomeNameDuplicated(m, home.getHomeName()))
                .doOnNext(m -> home.addSharedMemberId(m.getMemId()))
                .flatMap(m -> homeRepository.save(home)
                        .flatMap(h -> {
                            MemberHome memberHome = MemberHome.builder()
                                    .homeId(h.getId())
                                    .state(HomeState.shared).build();
                            m.addHome(memberHome);
                            return memberRepository.save(m).thenReturn(h);
                        }));
    }

    public Flux<GetHomesRes.HomeDto> getHomes(Mono<String> memId) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId);
        return memberMono
                .flatMap(member -> homeRepository.findAllById(member.getHomeIds())
                        .collectList()
                        .map(homes -> Tuples.of(member, homes)))
                .map(tuple -> {
                    Member member = tuple.getT1();
                    List<Home> homes = tuple.getT2();
                    return member.getHomes().stream()
                            .flatMap(memberHome -> homes.stream()
                                    .filter(h -> memberHome.getHomeId().equals(h.getId()))
                                    .map(h -> {
                                        GetHomesRes.HomeDto homeDto = HomeMapper.INSTANCE.toGetHomesHomeDto(h);
                                        homeDto.setState(memberHome.getState());
                                        homeDto.setInvitor(memberHome.getInvitor());
                                        return homeDto;
                                    }))
                            .collect(Collectors.toList());
                })
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<Member> checkHomeNameDuplicated(Member member, String homeName) {
        return homeRepository.findAllById(member.getHomes().stream().map(MemberHome::getHomeId).collect(Collectors.toList()))
                .any(h -> homeName.equalsIgnoreCase(h.getHomeName()))
                .flatMap(hasDuplicatedHomeName -> {
                    if (hasDuplicatedHomeName)
                        return Mono.error(HomeDuplicatedException.INSTANCE);
                    return Mono.just(member);
                });
    }

    // TODO Transaction?
    public Flux<String> withdrawHomes(Mono<String> memId, List<String> homeIds) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId);
        return memberMono
                .flatMap(member -> {
                    List<MemberHome> targetHomes = new ArrayList<>();
                    member.getHomes().stream()
                            .filter(home -> homeIds.contains(home.getHomeId()))
                            .forEach(targetHomes::add);
                    if (targetHomes.size() > 0) {
                        member.getHomes().removeAll(targetHomes);
                        return memberRepository.save(member)
                                .thenReturn(new AbstractMap.SimpleEntry<>(member.getMemId(), targetHomes.stream().map(MemberHome::getHomeId)));
                    } else {
                        return Mono.empty();
                    }
                })
                .flatMapMany(m -> {
                    String memberId = m.getKey();
                    List<String> targetHomeIds = m.getValue().collect(Collectors.toList());
                    return homeRepository.findAllById(targetHomeIds)
                            .doOnNext(home -> home.evictMember(memberId))
                            .flatMap(home -> {
                                if (home.hasNoRelatedMembers()) {
                                    return homeRepository.delete(home)
                                            .thenMany(Flux.fromStream(
                                                    home.getDeviceIds().stream()
                                                    .map(deviceService::releaseDeviceResource)))
                                                    .flatMap(Function.identity())
                                            .then(Mono.just(home.getId()));
                                } else {
                                    return homeRepository.save(home).thenReturn(home.getId());
                                }
                            });
                });
    }

    // InvalidMemberException, InvalidHomeException, HomeLimitException
    public Mono<Home> inviteHome(Mono<String> hostMemberId, InviteHomeReq inviteInfo) {
        String homeId = inviteInfo.getHomeId();
        String guestMemberId = inviteInfo.getMemberId();

        return hostMemberId
                .flatMap(memberRepository::findByMemId)
                // hostMemberId 가 초대할 수 있는 Home 인지 검사
                .filter(member -> member.getHomes().stream()
                        .anyMatch(home -> home.getHomeId().equals(homeId) && home.getState().equals(HomeState.shared)))
                .switchIfEmpty(Mono.error(InvalidHomeException.INSTANCE))
                .flatMap(host -> memberRepository.findByMemId(guestMemberId)
                        // guestMemberId 가 초대할 수 있는 사용자인지 검사
                        .switchIfEmpty(Mono.error(InvalidMemberException.INSTANCE))
                        .map(guest -> Tuples.of(host.getMemId(), guest)))
                .flatMap(tuple -> homeRepository.findById(homeId)
                        .map(home -> Tuples.of(tuple.getT1(), tuple.getT2(), home)))
                .flatMap(tuple -> {
                    String hostId = tuple.getT1();
                    Member guest = tuple.getT2();
                    Home home = tuple.getT3();
                    // guestMember 가 이미 home 에 연관(공유, 초대)된 사용자인지 검사
                    if (home.hasMember(guest.getMemId())) {
                        return Mono.error(InvalidMemberException.INSTANCE);
                    }
                    // member 컬렉션 업데이트
                    MemberHome memberHome = MemberHome.builder()
                            .homeId(home.getId())
                            .state(HomeState.invited)
                            .invitor(hostId).build();
                    guest.addHome(memberHome);
                    // home 컬렉션 업데이트
                    home.addInvitedMemberId(guest.getMemId());

                    return memberRepository.save(guest)
                            .then(homeRepository.save(home));
                });
    }

    // InvalidHomeException
    public Mono<Home> approveHomeInvitation(Mono<String> memberId, String homeId) {
        return memberId
                .flatMap(memberRepository::findByMemId)
                // 사용자에게 초대된 home 인지 검사
                .flatMap(member -> {
                    Optional<MemberHome> memberHome = member.getHomes().stream()
                            .filter(home -> home.getHomeId().equals(homeId) && home.getState().equals(HomeState.invited))
                            .findFirst();
                    if (memberHome.isEmpty()) {
                        return Mono.error(InvalidHomeException.INSTANCE);
                    }
                    memberHome.get().setState(HomeState.shared);
                    return memberRepository.save(member).thenReturn(member.getMemId());
                })
                .flatMap(memId -> homeRepository.findById(homeId)
                        .flatMap(home -> {
                            home.approveMember(memId);
                            return homeRepository.save(home);
                        }));
    }

    public Mono<String> checkHomeAndGetTimezone(String userId, String homeId, String[] deviceIds) {
        return homeRepository.findById(homeId)
            .switchIfEmpty(Mono.error(InvalidHomeException.INSTANCE))
            .filter(home -> {
                if (deviceIds != null && deviceIds.length > 0) {
                    for (String deviceId : deviceIds) {
                        if (!home.getDeviceIds().contains(deviceId)) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .switchIfEmpty(Mono.error(NotFoundDeviceException.INSTANCE))
            .map(Home::getTimezone);
    }
}