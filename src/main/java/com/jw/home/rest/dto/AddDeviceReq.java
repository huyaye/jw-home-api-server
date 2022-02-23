package com.jw.home.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddDeviceReq {
    @NotEmpty
    String homeId;
    @NotEmpty
    DeviceConnection connection;
    @NotEmpty
    DeviceType type;
    @NotNull
    List<AddDeviceTraitDto> traits;
    @NotEmpty
    String name;

    AddDeviceInfoDto deviceInfo;

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddDeviceTraitDto {
        @NotEmpty
        private TraitType type;
        private Map<String, Object> attr;
        private Map<String, Object> state;
    }

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddDeviceInfoDto {
        private String manufacturer;
        private String model;
        private String hwVersion;
        private String swVersion;
    }
}
