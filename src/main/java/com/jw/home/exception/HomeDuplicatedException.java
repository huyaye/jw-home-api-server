package com.jw.home.exception;

public class HomeDuplicatedException extends CustomBusinessException {
	private static final long serialVersionUID = -558278576297803875L;

	public static HomeDuplicatedException INSTANCE = new HomeDuplicatedException();

	HomeDuplicatedException() {
		super();
		this.errorCode = 302;
		this.errorMessage = "home duplicated";
	}
}
