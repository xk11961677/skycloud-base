package com.skycloud.base.authorization.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sky.framework.model.dto.MessageRes;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author
 */
@Slf4j
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        MessageRes messageRes = value.getMessageRes();
        gen.writeObject(messageRes);
//        log.error("CustomOauthExceptionSerializer: {}" + value.toString());
    }
}