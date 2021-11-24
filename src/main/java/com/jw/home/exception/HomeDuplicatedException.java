package com.jw.home.exception;

public class HomeDuplicatedException extends Exception {
	private static final long serialVersionUID = -558278576297803875L;

	public static HomeDuplicatedException INSTANCE = new HomeDuplicatedException();
}
