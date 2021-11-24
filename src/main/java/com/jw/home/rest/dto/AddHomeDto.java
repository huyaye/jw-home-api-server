package com.jw.home.rest.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.common.spec.HomeSecurityMode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddHomeDto {
	@Getter
	@Setter
	public static class AddRoomDto {
		private String roomName;
	}

	@NotEmpty
	private String homeName;

	@NotEmpty
	private String timezone;

	@NotEmpty
	private HomeSecurityMode securityMode;

	private List<AddRoomDto> rooms;
}
