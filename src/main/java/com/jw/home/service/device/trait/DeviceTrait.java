package com.jw.home.service.device.trait;

import com.jw.home.common.spec.DeviceType;
import com.jw.home.common.spec.TraitType;
import com.jw.home.service.device.annotation.TraitState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.aspectj.util.Reflection;
import org.springframework.security.core.parameters.P;

import java.util.Iterator;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class DeviceTrait {
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
    public abstract boolean valid(DeviceType deviceType);

    public void updateState(Map<String, Object> state) {
        Iterator<Map.Entry<String, Object>> iter = state.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entity = iter.next();
            if (isValidStateName(entity.getKey())) {
                this.state.put(entity.getKey(), entity.getValue());
            }
        }
    }

    private boolean isValidStateName(String stateName) {
        TraitState traitState = this.getClass().getAnnotation(TraitState.class);
        for (String name : traitState.names()) {
            if (name.equals(stateName)) {
                return true;
            }
        }
        return false;
    }

    // TODO
    void control() {}
}
