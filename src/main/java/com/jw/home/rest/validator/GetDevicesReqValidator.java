package com.jw.home.rest.validator;

import com.jw.home.common.spec.DeviceConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Component
@Slf4j
public class GetDevicesReqValidator implements RequestParamValidator {
    @Override
    public boolean validate(MultiValueMap<String, String> params) {
        List<String> connections = params.get("connection");
        if (connections == null || connections.isEmpty() || !DeviceConnection.isConnection(connections.get(0))) {
            return false;
        }
        List<String> serials = params.get("serial");
        return !(serials == null || serials.isEmpty());
    }
}
