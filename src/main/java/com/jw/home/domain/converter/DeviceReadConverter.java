package com.jw.home.domain.converter;

import com.jw.home.common.spec.TraitType;
import com.jw.home.service.device.TraitFactory;
import com.jw.home.service.device.trait.DeviceTrait;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.util.BsonUtils;

@ReadingConverter
public class DeviceReadConverter implements Converter<Document, DeviceTrait> {
    @Override
    public DeviceTrait convert(Document source) {
        TraitType type = TraitType.valueOf(source.getString("type"));
        DeviceTrait deviceTrait = TraitFactory.create(type);
        deviceTrait.setState(source.get("state", Document.class));
        deviceTrait.setAttr(source.get("attr", Document.class));
        return deviceTrait;
    }
}
