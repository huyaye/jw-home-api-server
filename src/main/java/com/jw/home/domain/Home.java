package com.jw.home.domain;

import com.jw.home.common.spec.HomeSecurityMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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

	private List<String> userIds;

	public boolean withoutMember() {
		return userIds == null || userIds.isEmpty();
	}
}