package com.jw.home.common.spec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TraitType {
    OnOff("action.devices.traits.OnOff"),
    Brightness("action.devices.traits.Brightness"),
    ColorSetting("action.devices.traits.ColorSetting");

    private final String code;

    TraitType(String code) {
        this.code = code;
    }

    // json -> enum
    @JsonCreator
    public static TraitType fromCode(String code) {
        for(TraitType type : TraitType.values()){
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
