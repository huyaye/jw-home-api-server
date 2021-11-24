package com.jw.home.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jw.home.domain.Member;
import com.jw.home.repository.MemberRepository;

import reactor.core.publisher.Mono;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	public Mono<Member> getMember(String memId) {
		return memberRepository.findByMemId(memId)
				.doOnNext(System.out::println)
				.doOnError(e -> {
					System.out.println(e);
				});
	}
}
