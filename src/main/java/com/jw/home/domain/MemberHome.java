package com.jw.home.domain;

import com.jw.home.common.spec.HomeState;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class MemberHome {
	String homeId;
	HomeState state;
	String invitor;
}
