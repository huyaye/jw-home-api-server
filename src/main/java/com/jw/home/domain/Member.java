package com.jw.home.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jw.home.common.spec.HomeState;
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

	private List<MemberHome> homes = new ArrayList<>();
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

	public List<String> getHomeIds() {
		return homes.stream().map(MemberHome::getHomeId).collect(Collectors.toList());
	}

    public boolean hasHome(String homeId, HomeState state) {
		return homes.stream().anyMatch(home -> {
			boolean existed = home.getHomeId().equals(homeId);
			if (existed && state != null) {
				existed = home.getState().equals(state);
			}
			return existed;
		});
    }
}
