package com.jw.home.repository;

import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.domain.Device;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceRepository extends ReactiveMongoRepository<Device, String> {
    Mono<Boolean> existsBySerialAndConnection(String serial, DeviceConnection connection);

    Flux<Device> findByConnectionAndSerial(DeviceConnection connection, String serial);
}
