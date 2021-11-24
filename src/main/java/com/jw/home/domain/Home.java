package com.jw.home.domain;

import java.util.List;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jw.home.common.spec.HomeSecurityMode;

import javax.management.ConstructorParameters;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "home")
public class Home {
	@Getter
	@Setter
	public static class Room {
		private String roomName;
	}

	@Id
	private String id;

	private String homeName;

	private String timezone;

	private HomeSecurityMode securityMode;

	private List<Room> rooms;
}