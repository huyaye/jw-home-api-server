package com.jw.home.service;

import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.exception.HomeDuplicatedException;
import com.jw.home.exception.HomeLimitException;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .flatMap(m -> homeRepository.save(home)
                        .flatMap(h -> {
                            MemberHome memberHome = new MemberHome();
                            memberHome.setHomeId(h.getId());
                            m.addHome(memberHome);
                            return memberRepository.save(m).thenReturn(h);
                        }));
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

}
