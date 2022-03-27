package com.jw.home.rest.dto;

import com.jw.home.common.spec.CommandType;
import com.jw.home.common.spec.DeviceConnection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ControlDeviceReq {
    @NotEmpty
    DeviceConnection connection;
    @NotEmpty
    String deviceId;
    @NotEmpty
    CommandType command;
    @NotNull
    Map<String, Object> params;
}
