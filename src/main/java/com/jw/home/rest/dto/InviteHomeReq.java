package com.jw.home.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InviteHomeReq {
    @NotEmpty
    String homeId;
    @NotEmpty
    String memberId;
}
