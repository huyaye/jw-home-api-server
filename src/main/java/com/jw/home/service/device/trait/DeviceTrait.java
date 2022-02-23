package com.jw.home.service.device.trait;

import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DeviceTrait {
    protected TraitType type;

    protected Map<String, Object> attr;

    protected Map<String, Object> state;

    /**
     * attr과 state가 스펙을 만족하는지 검사
     * subClass 에서 구현
     *
     * @param deviceType 디바이스 종류가 검사에 영향을 줄 수 있는 경우 활용.
     * @return true if valid, or false
     */
    public boolean valid(DeviceType deviceType) {
        return false;
    }

    // TODO
    void control() {}
}
