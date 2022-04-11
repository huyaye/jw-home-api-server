package com.jw.home.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetDevicesRes {
    @Getter
    @Setter
    @EqualsAndHashCode
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeviceDto {
        String homeId;
        DeviceConnection connection;
        String serial;
        DeviceType type;
        List<GetDevicesRes.DeviceDto.GetDeviceTraitDto> traits;
        String name;
        GetDevicesRes.DeviceDto.GetDeviceInfoDto deviceInfo;

        @Getter
        @Setter
        @ToString
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class GetDeviceTraitDto {
            private TraitType type;
            private Map<String, Object> attr;
            private Map<String, Object> state;
        }

        @Getter
        @Setter
        @ToString
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class GetDeviceInfoDto {
            private String manufacturer;
            private String model;
            private String hwVersion;
            private String swVersion;
        }
    }

    private List<DeviceDto> devices;
}