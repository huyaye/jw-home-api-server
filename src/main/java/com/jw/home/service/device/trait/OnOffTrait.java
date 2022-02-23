package com.jw.home.service.device.trait;

import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;

import java.util.Map;

public class OnOffTrait extends DeviceTrait {
    public OnOffTrait() {
        type = TraitType.OnOff;
    }

    @Override
    public boolean valid(DeviceType deviceType) {
        if (this.attr != null) {
            for (Map.Entry<String, Object> entry : attr.entrySet()) {
                String attrName = entry.getKey();
                Object value = entry.getValue();
                if (attrName.equals("commandOnlyOnOff") || attrName.equals("queryOnlyOnOff")) {
                    if (!(value instanceof Boolean)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
