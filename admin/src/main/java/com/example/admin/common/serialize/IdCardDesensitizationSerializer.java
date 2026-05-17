package com.example.admin.common.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class IdCardDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String idCard, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (idCard == null) {
            jsonGenerator.writeNull();
            return;
        }
        jsonGenerator.writeString(DesensitizedUtil.idCardNum(idCard, 4, 4));
    }
}
