package com.jw.home.service;

import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.exception.HomeDuplicatedException;
import com.jw.home.exception.HomeLimitException;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {

    private final MemberRepository memberRepository;

    private final HomeRepository homeRepository;

    public HomeService(MemberRepository memberRepository, HomeRepository homeRepository) {
        this.memberRepository = memberRepository;
        this.homeRepository = homeRepository;
    }

    // HomeLimitException, HomeDuplicatedException
    public Mono<Home> addHome(Mono<String> memId, Home home) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId).log();
        return memberMono
                .filter(m -> !m.hasMaxHome())
                .switchIfEmpty(Mono.error(HomeLimitException.INSTANCE))
                .flatMap(m -> checkHomeNameDuplicated(m, home.getHomeName()))
                .doOnNext(m -> home.setUserIds(Collections.singletonList(m.getMemId())))
                .flatMap(m -> homeRepository.save(home)
                        .flatMap(h -> {
                            MemberHome memberHome = new MemberHome();
                            memberHome.setHomeId(h.getId());
                            m.addHome(memberHome);
                            return memberRepository.save(m).thenReturn(h);
                        }));
    }

    public Flux<Home> getHomes(Mono<String> memId) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId);
        return memberMono
                .flatMapMany(member -> homeRepository.findAllById(member.getHomes().stream().map(MemberHome::getHomeId).collect(Collectors.toList())));
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
    public Flux<String> deleteHomes(Mono<String> memId, List<String> homeIds) {
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
                                .thenReturn(new AbstractMap.SimpleEntry<>(member.getMemId(), targetHomes.stream().map(MemberHome::getHomeId) ));
                    } else {
                        return Mono.empty();
                    }
                })
                .flatMapMany(m -> {
                    String memberId = m.getKey();
                    List<String> targetHomeIds = m.getValue().collect(Collectors.toList());
                    return homeRepository.findAllById(targetHomeIds)
                            .doOnNext(home -> home.getUserIds().remove(memberId))
                            .flatMap(home -> {
                                if (home.withoutMember()) {
                                    return homeRepository.delete(home).thenReturn(home.getId());
                                } else {
                                    return homeRepository.save(home).thenReturn(home.getId());
                                }
                            });
                });
    }
}
