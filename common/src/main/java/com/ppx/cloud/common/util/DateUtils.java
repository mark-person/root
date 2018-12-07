package com.ppx.cloud.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public final static String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public final static String DATE_PATTERN = "yyyy-MM-dd";

	
	public static String today() {
		return new SimpleDateFormat(DATE_PATTERN).format(new Date());
	}
	
}