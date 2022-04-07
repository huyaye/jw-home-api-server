package com.jw.home.service.device.mapper;

import com.jw.home.rest.dto.ControlDeviceReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceServerDtoMapper {
    DeviceServerDtoMapper INSTANCE = Mappers.getMapper(DeviceServerDtoMapper.class);

    com.jw.home.service.device.dto.ControlDeviceReq toControlDeviceReq(com.jw.home.rest.dto.ControlDeviceReq controlDeviceReq, String serial);
}
