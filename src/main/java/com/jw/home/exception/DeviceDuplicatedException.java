package com.jw.home.exception;

import com.jw.home.common.spec.DeviceConnection;

public class DeviceDuplicatedException extends CustomBusinessException {
	public static DeviceDuplicatedException INSTANCE = new DeviceDuplicatedException();

	DeviceDuplicatedException() {
		super();
		this.errorCode = 403;
		this.errorMessage = "device duplicated";
	}

	public DeviceDuplicatedException(DeviceConnection connection, String serial) {
		this();
		this.errorMessage += " - " + connection + ", " + serial;
	}
}
