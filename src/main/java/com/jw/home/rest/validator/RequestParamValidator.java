package com.jw.home.rest.validator;

import org.springframework.util.MultiValueMap;

public interface RequestParamValidator {
    boolean validate(MultiValueMap<String, String> params);
}
