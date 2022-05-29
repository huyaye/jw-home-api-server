package com.jw.home.kafka.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.TriggerType;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceStateValue {
    DeviceConnection connection;

    String serial;

    Boolean online;

    TriggerType trigger;

    Map<String, Object> states;
}
