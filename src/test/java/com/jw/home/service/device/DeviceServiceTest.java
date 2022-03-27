package com.jw.home.service.device;

import com.jw.home.common.spec.CommandType;
import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.HomeState;
import com.jw.home.domain.Device;
import com.jw.home.domain.Home;
import com.jw.home.domain.Member;
import com.jw.home.domain.MemberHome;
import com.jw.home.repository.DeviceRepository;
import com.jw.home.repository.HomeRepository;
import com.jw.home.repository.MemberRepository;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import com.jw.home.rest.dto.ControlDeviceStatus;
import com.jw.home.service.device.trait.BrightnessTrait;
import com.jw.home.service.device.trait.DeviceTrait;
import com.jw.home.service.device.trait.OnOffTrait;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class DeviceServiceTest {
    private DeviceService deviceService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private HomeRepository homeRepository;
    @MockBean
    private DeviceRepository deviceRepository;
    @MockBean
    private DeviceServerCaller deviceServerCaller;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService(memberRepository, homeRepository, deviceRepository, deviceServerCaller);
    }

    @Test
    void addDevice() {
        Member member = new Member();
        member.setMemId("jw");
        member.addHome(MemberHome.builder().homeId("homeId").state(HomeState.shared).build());

        Home home = new Home();
        home.setId("homeId");

        Device device = makeLightDevice("homeId");
        device.setId("deviceId");

        when(homeRepository.save(home)).thenReturn(Mono.just(home));
        when(homeRepository.findById("homeId")).thenReturn(Mono.just(home));
        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(deviceRepository.save(device)).thenReturn(Mono.just(device));
        when(deviceRepository.existsBySerialAndConnection(any(), any())).thenReturn(Mono.just(false));

        Mono<String> deviceId = deviceService.addDevice("jw", device);
        StepVerifier.create(deviceId)
                .expectNext("deviceId")
                .verifyComplete();

        Assertions.assertThat(home.getNoRoomDeviceIds()).contains("deviceId");
    }

    @Test
    void controlDevice() {
        Member member = new Member();
        member.setMemId("jw");

        Device device = makeLightDevice("homeId");
        device.setId("deviceId");

        Home home = new Home();
        home.setId("homeId");
        home.addSharedMemberId("jw");

        ControlDeviceReq controlDeviceReq = new ControlDeviceReq();
        controlDeviceReq.setDeviceId("deviceId");
        controlDeviceReq.setConnection(DeviceConnection.websocket);
        controlDeviceReq.setCommand(CommandType.OnOff);
        controlDeviceReq.setParams(Collections.singletonMap("on", false));

        ControlDeviceRes controlDeviceRes = new ControlDeviceRes();
        controlDeviceRes.setStatus(ControlDeviceStatus.SUCCESS);
        controlDeviceRes.setStates(Collections.singletonMap("on", false));

        when(memberRepository.findByMemId(anyString())).thenReturn(Mono.just(member));
        when(deviceRepository.findById(controlDeviceReq.getDeviceId())).thenReturn(Mono.just(device));
        when(homeRepository.findById("homeId")).thenReturn(Mono.just(home));
        when(deviceServerCaller.controlDevice(controlDeviceReq)).thenReturn(Mono.just(controlDeviceRes));

        Mono<ControlDeviceRes> response = deviceService.controlDevice(Mono.just("jw"), controlDeviceReq);
        StepVerifier.create(response)
                .consumeNextWith(res -> {
                    Assertions.assertThat(res.getStatus()).isEqualTo(ControlDeviceStatus.SUCCESS);
                    Assertions.assertThat(res.getStates().get("on")).isEqualTo(false);
                })
                .verifyComplete();
    }

    private Device makeLightDevice(String homeId) {
        Device device = new Device();
        device.setHomeId(homeId);
        device.setConnection(DeviceConnection.websocket);
        device.setType(DeviceType.LIGHT);
        device.setName("Smart Light");

        DeviceTrait onOffTrait = new OnOffTrait();
        onOffTrait.setState(Collections.singletonMap("on", true));
        onOffTrait.setAttr(Collections.singletonMap("commandOnlyOnOff", false));
        DeviceTrait brightnessTrait = new BrightnessTrait();
        onOffTrait.setState(Collections.singletonMap("brightness", 65));
        List<DeviceTrait> traits = new ArrayList<>();
        traits.add(onOffTrait);
        traits.add(brightnessTrait);
        device.setTraits(traits);

        return device;
    }
}