package com.jw.home.service.device;

import com.jw.home.common.spec.HomeState;
import com.jw.home.domain.Device;
import com.jw.home.domain.Member;
import com.jw.home.exception.DeviceDuplicatedException;
import com.jw.home.exception.InvalidDeviceSpecException;
import com.jw.home.exception.InvalidHomeException;
import com.jw.home.repository.DeviceRepository;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeviceService {
    private final MemberRepository memberRepository;
    private final HomeRepository homeRepository;
    private final DeviceRepository deviceRepository;

    public DeviceService(MemberRepository memberRepository,
                         HomeRepository homeRepository,
                         DeviceRepository deviceRepository) {
        this.memberRepository = memberRepository;
        this.homeRepository = homeRepository;
        this.deviceRepository = deviceRepository;
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
}
