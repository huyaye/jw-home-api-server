package com.jw.home.rest.dto;

import com.jw.home.domain.Home;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetHomesDto {
	private List<Home> homes;
}
