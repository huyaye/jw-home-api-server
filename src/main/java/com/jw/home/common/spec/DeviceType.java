package com.jw.home.common.spec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceType {
    LIGHT("action.devices.types.LIGHT");

    private final String code;

    DeviceType(String code) {
        this.code = code;
    }

    // json -> enum
    @JsonCreator
    public static DeviceType fromCode(String code) {
        for(DeviceType type : DeviceType.values()){
            if(type.code.equals(code)){
                return type;
            }
        }
        return null;
    }

    // enum -> json
    @JsonValue
    public String getCode() {
        return code;
    }
}
