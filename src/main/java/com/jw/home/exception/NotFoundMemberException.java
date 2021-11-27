package com.jw.home.exception;

public class NotFoundMemberException extends CustomBusinessException {
	public static NotFoundMemberException INSTANCE = new NotFoundMemberException();

	NotFoundMemberException() {
		super();
		this.resultCode = 201;
	}
}
