package com.ppx.cloud.common.config;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.common.util.DecimalUtils;

@SuppressWarnings("serial")
public class ObjectMappingCustomer extends ObjectMapper {

	public ObjectMappingCustomer() {
		//super.setDateFormat(new SimpleDateFormat(DateUtils.TIME_PATTERN));
		super.setTimeZone(TimeZone.getTimeZone("GMT+8"));

		super.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		SimpleModule module = new SimpleModule();
		
		module.addSerializer(Float.class, new JsonSerializer<Float>() {
			@Override
			public void serialize(Float value, JsonGenerator jsonGenerator, SerializerProvider provider)
					throws IOException {
				DecimalFormat df = new DecimalFormat(DecimalUtils.MONEY_PATTERN);
				jsonGenerator.writeString(df.format(value));
			}
		});
		
		module.addSerializer(Date.class, new JsonSerializer<Date>() {
			@Override
			public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider provider)
					throws IOException {
				SimpleDateFormat df = new SimpleDateFormat(DateUtils.TIME_PATTERN);
				jsonGenerator.writeString(df.format(value));
			}
		});
		
		registerModule(module);

	}
}
