package com.jw.home.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jw.home.domain.Home;

public interface HomeRepository extends ReactiveMongoRepository<Home, String> {
}
