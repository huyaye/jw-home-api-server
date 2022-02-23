package com.jw.home.repository;

import com.jw.home.domain.Device;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DeviceRepository extends ReactiveMongoRepository<Device, String> {
}
