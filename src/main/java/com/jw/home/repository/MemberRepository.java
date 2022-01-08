package com.jw.home.repository;

import com.jw.home.domain.Member;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveMongoRepository<Member, String> {
	Mono<Member> findByMemId(String memId);
}
