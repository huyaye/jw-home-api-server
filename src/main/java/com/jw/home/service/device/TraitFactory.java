package com.jw.home.service.device;

import com.jw.home.common.spec.TraitType;
import com.jw.home.exception.InvalidDeviceSpecException;
import com.jw.home.service.device.trait.*;

import javax.validation.constraints.Null;

public class TraitFactory {
    public static DeviceTrait create(TraitType traitType) {
        switch (traitType) {
            case OnOff:
                return new OnOffTrait();
            case Brightness:
                return new BrightnessTrait();
            case ColorSetting:
                return new ColorSettingTrait();
            default:
                throw InvalidDeviceSpecException.INSTANCE;
        }
    }
}
