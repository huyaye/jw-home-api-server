package com.jw.home.rest.dto;

import com.jw.home.common.spec.HomeSecurityMode;
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
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	public static class RoomDto {
		private String roomName;
	}

	private List<HomeDto> homes;
}
