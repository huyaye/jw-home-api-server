package com.jw.home.domain.mapper;

import com.jw.home.domain.Home;
import com.jw.home.rest.dto.AddHomeReq;
import com.jw.home.rest.dto.AddHomeRes;
import com.jw.home.rest.dto.GetHomesRes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HomeMapper {
    HomeMapper INSTANCE = Mappers.getMapper(HomeMapper.class);

    Home toHome(AddHomeReq dto);

    AddHomeRes toAddHomeRes(Home home);

    GetHomesRes.HomeDto toGetHomesHomeDto(Home home);
}
