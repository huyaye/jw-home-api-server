package com.jw.home.common.spec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommandType {
    OnOff("action.devices.commands.OnOff"),
    BrightnessAbsolute("action.devices.commands.BrightnessAbsolute"),
    ColorAbsolute("action.devices.commands.ColorAbsolute");

    private final String code;

    CommandType(String code) {
        this.code = code;
    }

    // json -> enum
    @JsonCreator
    public static CommandType fromCode(String code) {
        for(CommandType type : CommandType.values()){
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
