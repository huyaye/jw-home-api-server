package com.jw.home.rest.dto;

import com.jw.home.common.spec.HomeSecurityMode;
import com.jw.home.common.spec.HomeState;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetHomesRes {
	@Getter
	@Setter
	@EqualsAndHashCode
	public static class HomeDto {
		private String id;
		private String homeName;
		private String timezone;
		private HomeSecurityMode securityMode;
		private List<RoomDto> rooms;
		private HomeState state;
		private String invitor;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	public static class RoomDto {
		private String roomName;
	}

	private List<HomeDto> homes;
}
