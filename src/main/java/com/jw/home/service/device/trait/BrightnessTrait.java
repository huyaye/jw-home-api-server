package com.jw.home.service.device.trait;

import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;

import java.util.Map;

public class BrightnessTrait extends DeviceTrait {
    public BrightnessTrait() {
        type = TraitType.Brightness;
    }

    @Override
    public boolean valid(DeviceType deviceType) {
        if (this.attr != null) {
            for (Map.Entry<String, Object> entry : attr.entrySet()) {
                String attrName = entry.getKey();
                Object value = entry.getValue();
                if (attrName.equals("commandOnlyBrightness")) {
                    if (!(value instanceof Boolean)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
