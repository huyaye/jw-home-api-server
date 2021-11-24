package com.jw.home.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jw.home.domain.Member;
import com.jw.home.service.MemberService;

import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/member")
public class MemberRestController {
	@Autowired
	private MemberService memberService;

	@GetMapping("/{memId}")
	public Mono<Member> getMember(@PathVariable("memId") String memId) {
		// TODO 공통 권한검사

		return memberService.getMember(memId);
	}
}
