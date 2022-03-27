package com.jw.home.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControlDeviceRes {
    @NotEmpty
    private ControlDeviceStatus status;

    private Map<String, Object> states;

    private String cause;
}
