package com.jw.home.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.HomeSecurityMode;
import com.jw.home.common.spec.HomeState;
import com.jw.home.service.device.trait.DeviceTrait;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetHomesRes {
    @Getter
    @Setter
    @EqualsAndHashCode
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HomeDto {
        private String id;
        private String homeName;
        private String timezone;
        private HomeSecurityMode securityMode;
        private List<RoomDto> rooms = new ArrayList<>();
        private Set<DeviceDto> noRoomDevices = new HashSet<>();
        private HomeState state;
        private String invitor;

        public void addNoRoomDeviceDto(DeviceDto deviceDto) {
            noRoomDevices.add(deviceDto);
        }

        public void addDeviceDtoToRoom(DeviceDto deviceDto, String roomName) {
            for (RoomDto roomDto : rooms) {
                if (roomDto.getRoomName().equals(roomName)) {
                    Set<DeviceDto> devices = roomDto.getDevices();
                    devices.add(deviceDto);
                }
            }
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomDto {
        private String roomName;
        private Set<DeviceDto> devices = new HashSet<>();
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeviceDto {
        private String id;
        private DeviceConnection connection;
        private DeviceType type;
        private String name;
        private String serial;
        private Boolean online;
        private List<DeviceTrait> traits;
    }

    private List<HomeDto> homes;
}
