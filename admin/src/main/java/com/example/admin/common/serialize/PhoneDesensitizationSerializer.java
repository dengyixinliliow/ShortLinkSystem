package com.example.admin.common.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PhoneDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String phone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (phone == null) {
            jsonGenerator.writeNull();
            return;
        }
        jsonGenerator.writeString(DesensitizedUtil.mobilePhone(phone));
    }
}
