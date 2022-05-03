package com.jw.home.service.device.mapper;

import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import com.jw.home.service.device.dto.ControlReqMsg;
import com.jw.home.service.device.dto.ControlResMsg;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceServerDtoMapper {
    DeviceServerDtoMapper INSTANCE = Mappers.getMapper(DeviceServerDtoMapper.class);

    ControlReqMsg toControlReqMsg(ControlDeviceReq controlDeviceReq, String transactionId, String serial);

    ControlDeviceRes toControlDeviceRes(ControlResMsg controlresMsg);
}
