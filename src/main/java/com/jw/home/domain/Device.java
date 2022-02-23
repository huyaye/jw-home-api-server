package com.jw.home.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.DeviceType;
import com.jw.home.service.device.trait.DeviceTrait;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "device")
public class Device {
    @Id
    private String id;

    String homeId;

    DeviceConnection connection;

    DeviceType type;

    List<DeviceTrait> traits;

    String name;

    DeviceInfo deviceInfo;

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeviceInfo {
        private String manufacturer;
        private String model;
        private String hwVersion;
        private String swVersion;
    }

    public boolean valid() {
        for (DeviceTrait trait : traits) {
            if (!trait.valid(this.type)) {
                return false;
            }
        }
        return true;
    }
}
