package com.jw.home.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.exception.HomeLimitException;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HomeService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HomeRepository homeRepository;

    // HomeLimitException, HomeDuplicatedException
    public Mono<Home> addHome(String memId, Home home) {
        /*
         * Member member = memberRepository.findByMemId(memId);
         * if (member.hasMaxHome()) {;
         * 		throw HomeLimitException.INSTANCE;
         * }
         *
         * home = homeRepository.save(home);
         *
         * MemberHome memberHome = new MemberHome();
         * memberHome.setHomeId(home.getId());
         * member.addHome(memberHome);
         * memberRepository.save(member);
         *
         * return home;
         */

        Mono<Member> memberMono = memberRepository.findByMemId(memId).log();
        return memberMono
                .filter(m -> m.hasMaxHome() == false).log()
                .switchIfEmpty(Mono.error(HomeLimitException.INSTANCE))
                .flatMap(m -> homeRepository.save(home)
                        .flatMap(h -> {
                            MemberHome memberHome = new MemberHome();
                            memberHome.setHomeId(h.getId());
                            try {
                                m.addHome(memberHome);
                            } catch (HomeLimitException e) {
                                e.printStackTrace();
                            }
                            return memberRepository.save(m).thenReturn(h);
                        }));
    }

}