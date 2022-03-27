package com.jw.home.exception;

import com.jw.home.rest.dto.ControlDeviceRes;

public class DeviceControlException extends CustomBusinessException {
	public DeviceControlException(ControlDeviceRes cause) {
		super();
		this.errorCode = 404;
		this.errorMessage = "failed to control device";
		this.resultData = cause;
	}
}
