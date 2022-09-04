package com.jw.home.domain.mapper;

import com.jw.home.domain.Device;
import com.jw.home.rest.dto.AddDeviceReq;
import com.jw.home.rest.dto.GetHomesRes;
import com.jw.home.service.device.TraitFactory;
import com.jw.home.service.device.trait.DeviceTrait;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceMapper {
    DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);

    @Mapping(source = "traits", target = "traits",
            qualifiedByName = "AddDeviceTraitDtoToDeviceTrait")
    Device toDevice(AddDeviceReq dto);

    @Named("AddDeviceTraitDtoToDeviceTrait")
    static List<DeviceTrait> AddDeviceTraitDtoToDeviceTrait(List<AddDeviceReq.AddDeviceTraitDto> source) {
        return source.stream().map(s -> {
            DeviceTrait deviceTrait = TraitFactory.create(s.getType());
            deviceTrait.setAttr(s.getAttr());
            deviceTrait.setState(s.getState());
            return deviceTrait;
        }).collect(Collectors.toList());
    }

    GetHomesRes.DeviceDto toDeviceDto(Device device);
}
