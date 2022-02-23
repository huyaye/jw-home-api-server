package com.jw.home.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.HomeSecurityMode;
import com.jw.home.common.spec.HomeState;
import lombok.*;

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
		private List<RoomDto> rooms;
		private Set<String> noRoomDeviceIds;
		private HomeState state;
		private String invitor;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RoomDto {
		private String roomName;
		private Set<String> deviceIds;
	}

	private List<HomeDto> homes;
}
