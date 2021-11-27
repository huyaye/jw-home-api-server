package com.jw.home.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jw.home.exception.HomeLimitException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "member")
public class Member {
	@Id
	private String id;

	private String memId;

	private List<MemberHome> homes;
//
//	private List<MemberDevice> devices;

	public boolean hasMaxHome() {
		return homes.size() >= 3;
	}

	public void addHome(MemberHome home) {
		if (hasMaxHome()) {
			throw HomeLimitException.INSTANCE;
		}
		homes.add(home);
	}
}
