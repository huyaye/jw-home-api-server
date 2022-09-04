package com.jw.home.service.device;

import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.HomeState;
import com.jw.home.domain.Device;
import com.jw.home.domain.Member;
import com.jw.home.exception.DeviceDuplicatedException;
import com.jw.home.exception.InvalidDeviceSpecException;
import com.jw.home.exception.InvalidHomeException;
import com.jw.home.exception.NotFoundDeviceException;
import com.jw.home.repository.DeviceRepository;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final MemberRepository memberRepository;
    private final HomeRepository homeRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceServerCaller deviceServerCaller;

    public DeviceService(MemberRepository memberRepository, HomeRepository homeRepository,
                         DeviceRepository deviceRepository, DeviceServerCaller deviceServerCaller) {
        this.memberRepository = memberRepository;
        this.homeRepository = homeRepository;
        this.deviceRepository = deviceRepository;
        this.deviceServerCaller = deviceServerCaller;
    }

    // TODO Transaction
    public Mono<String> addDevice(String memId, Device device) {
        Mono<Member> memberMono = memberRepository.findByMemId(memId);
        return memberMono
                .filter(m -> m.hasHome(device.getHomeId(), HomeState.shared))
                .switchIfEmpty(Mono.error(InvalidHomeException.INSTANCE))
                .filter(m -> device.valid())
                .switchIfEmpty(Mono.error(InvalidDeviceSpecException.INSTANCE))
                .flatMap(m -> deviceRepository.existsBySerialAndConnection(device.getSerial(), device.getConnection())
                        .map(exist -> {
                            if (exist) {
                                throw new DeviceDuplicatedException(device.getConnection(), device.getSerial());
                            }
                            return m;
                        }))
                .flatMap(m -> deviceRepository.save(device).map(Device::getId))
                .flatMap(deviceId -> homeRepository.findById(device.getHomeId())
                        .flatMap(home -> {
                            home.addNoRoomDeviceId(deviceId);
                            return homeRepository.save(home).thenReturn(deviceId);
                        }));
    }

    public Mono<ControlDeviceRes> controlDevice(Mono<String> memId, ControlDeviceReq req) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId).log();
        return memberMono
                // Validation
                .flatMap(m -> deviceRepository.findById(req.getDeviceId())
                        .map(d -> Tuples.of(m.getMemId(), d)))  // T1:memberId, T2:device
                .filter(t -> t.getT2().getConnection().equals(req.getConnection()))
                .flatMap(t -> homeRepository.findById(t.getT2().getHomeId())
                        .map(h -> Tuples.of(t.getT1(), t.getT2(), h)))  // T1:memberId, T2:device, T3:home
                .filter(t -> t.getT3().hasSharedMember(t.getT1()))
                .switchIfEmpty(Mono.error(NotFoundDeviceException.INSTANCE))
                // Call device server
                .flatMap(t -> deviceServerCaller.controlDevice(req, t.getT2().getSerial()));
    }

    public Mono<String> getDeviceId(DeviceConnection connection, String serial) {
        return deviceRepository.findByConnectionAndSerial(connection, serial)
                .switchIfEmpty(Mono.error(NotFoundDeviceException.INSTANCE))
                .map(Device::getId);
    }

    public Flux<Device> getDevices(Set<String> deviceIds) {
        return deviceRepository.findAllById(deviceIds);
    }

    public Flux<String> deleteDevices(Mono<String> memId, List<String> deviceIds) {
        Mono<Member> memberMono = memId.flatMap(memberRepository::findByMemId);
        return memberMono
                .flatMapMany(member -> homeRepository.findAllById(member.getHomeIds()))
                .flatMap(home -> {
                    List<String> targetDeviceIds = deviceIds.stream()
                            .filter(home::removeDevice).collect(Collectors.toList());
                    return homeRepository.save(home)
                            .thenMany(Flux.fromIterable(targetDeviceIds));
                })
                .flatMap(this::releaseDeviceResource);
    }

    public Mono<String> releaseDeviceResource(String deviceId) {
        // DB 에서 삭제하고 device server 로 리소스 해제 요청을 한다.
        return deviceRepository.deleteById(deviceId)
                .doOnNext(Void -> {
                    // TODO notify device server to disconnect
                }).thenReturn(deviceId);
    }

}
