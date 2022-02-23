package com.jw.home.rest.handler;

import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;
import com.jw.home.config.CustomSecurityConfiguration;
import com.jw.home.domain.Device;
import com.jw.home.rest.dto.AddDeviceReq;
import com.jw.home.rest.dto.ResponseDto;
import com.jw.home.rest.router.DeviceRouter;
import com.jw.home.service.device.DeviceService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;

@WebFluxTest
@Import(CustomSecurityConfiguration.class)
@ContextConfiguration(classes = {DeviceRouter.class, DeviceHandler.class})
class DeviceHandlerTest {
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private DeviceService deviceService;

    @Test
    void addDevice() {
        when(deviceService.addDevice(any(), any(Device.class))).thenReturn(Mono.just("deviceId"));

        AddDeviceReq req = new AddDeviceReq();
        req.setHomeId("homeId");
        req.setConnection(DeviceConnection.websocket);
        req.setType(DeviceType.LIGHT);
        req.setName("Smart Light");
        AddDeviceReq.AddDeviceTraitDto onOffTrait = new AddDeviceReq.AddDeviceTraitDto();
        onOffTrait.setType(TraitType.OnOff);
        onOffTrait.setState(Collections.singletonMap("on", true));
        onOffTrait.setAttr(Collections.singletonMap("commandOnlyOnOff", false));
        List<AddDeviceReq.AddDeviceTraitDto> traits = new ArrayList<>();
        traits.add(onOffTrait);
        req.setTraits(traits);

        webClient.mutateWith(mockOpaqueToken()
                        .authorities(AuthorityUtils.createAuthorityList("SCOPE_jw.home")))
                .post().uri("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), AddDeviceReq.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseDto<Map<String, String>>>() {
                })
                .value(res -> {
                    Assertions.assertThat(res.getErrorCode()).isNull();
                    Assertions.assertThat(res.getResultData().get("id")).isEqualTo("deviceId");
                });
    }
}