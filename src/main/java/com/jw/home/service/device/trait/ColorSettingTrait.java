package com.jw.home.service.device.trait;

import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;

import java.util.Map;

public class ColorSettingTrait extends DeviceTrait {
    public ColorSettingTrait() {
        type = TraitType.ColorSetting;
    }

    @Override
    public boolean valid(DeviceType deviceType) {
        if (this.attr != null) {
            boolean includeColorAttr = false;
            for (Map.Entry<String, Object> entry : attr.entrySet()) {
                String attrName = entry.getKey();
                Object value = entry.getValue();
                if (attrName.equals("commandOnlyColorSetting")) {
                    if (!(value instanceof Boolean)) {
                        return false;
                    }
                } else if (attrName.equals("colorTemperatureRange")) {
                    if (!(value instanceof Map)) {
                        return false;
                    }
                    Object temperatureMinK = ((Map<?, ?>) value).get("temperatureMinK");
                    Object temperatureMaxK = ((Map<?, ?>) value).get("temperatureMaxK");
                    if (!(temperatureMinK instanceof Integer && temperatureMaxK instanceof Integer)) {
                        return false;
                    }
                    includeColorAttr = true;
                }
            }
            return includeColorAttr;
        }
        return true;
    }
}