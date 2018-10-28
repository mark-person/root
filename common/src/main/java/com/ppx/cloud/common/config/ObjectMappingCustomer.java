package com.ppx.cloud.common.config;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.common.util.DecimalUtils;

@SuppressWarnings("serial")
public class ObjectMappingCustomer extends ObjectMapper {

	public ObjectMappingCustomer() {
		super.setDateFormat(new SimpleDateFormat(DateUtils.TIME_PATTERN));

		super.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		SimpleModule module = new SimpleModule();

		module.addSerializer(Float.class, new JsonSerializer<Float>() {
			@Override
			public void serialize(Float value, JsonGenerator jsonGenerator, SerializerProvider provider)
					throws IOException, JsonProcessingException {
				DecimalFormat df = new DecimalFormat(DecimalUtils.MONEY_PATTERN);
				jsonGenerator.writeString(df.format(value));
			}
		});

		module.addSerializer(String.class, new JsonSerializer<String>() {
			@Override
			public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jsonGenerator.writeString(value);
			}
		});

		registerModule(module);

	}
}
