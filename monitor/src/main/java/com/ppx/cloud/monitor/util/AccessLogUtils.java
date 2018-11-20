/**
 * 
 */
package com.ppx.cloud.monitor.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.ppx.cloud.common.exception.ErrorBean;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.monitor.cache.MonitorCache;
import com.ppx.cloud.monitor.pojo.AccessLog;

/**
 * @author mark
 *
 * @date 2018年11月8日
 */
public class AccessLogUtils {
	public static List<String> getInfoList(AccessLog a) {
		List<String> infoList = new ArrayList<String>();
		
		String beginTime = new SimpleDateFormat(DateUtils.TIME_PATTERN).format(a.getBeginTime());

		String uri = a.getUri();
		Pattern pattern = Pattern.compile("[0-9]*");
		if (pattern.matcher(uri).matches()) {
			uri = MonitorCache.getSeqUri(Integer.parseInt(uri));
			uri = uri == null ? a.getUri() : uri;
		}
		
		
		StringBuilder accessSb = new StringBuilder(a.getIp()).append("[").append(beginTime).append("]")
				.append(a.getSpendNanoTime() / 1000000).append(" ").append(a.getMethod()).append(" ")
				.append(uri);
		if (!StringUtils.isEmpty(a.getQueryString())) {
			accessSb.append("?").append(a.getQueryString());
		}
		if (a.getReferer() != null) {
			accessSb.append(" Referer:" + a.getReferer());
		}

		infoList.add(accessSb.toString());
		if (!StringUtils.isEmpty(a.getParams())) {
			infoList.add("params:" + a.getParams());
		}

		// cache
		if (!StringUtils.isEmpty(a.getCacheKey())) {
			infoList.add("cache:" + a.getCacheKey());
		}

		if (a.getSqlList().size() > 0) {
			for (int i = 0; i < a.getSqlList().size(); i++) {
				String v = a.getSqlList().get(i);
				v = MonitorCache.getMd5Sql(v);
				v = v == null ? a.getSqlList().get(i) : v;
				a.getSqlList().set(i, v);
			}

			String sql = StringUtils.collectionToDelimitedString(a.getSqlList(), "\r\n        ");
			infoList.add("sqlText:" + sql);
			infoList.add("sqlArgs:" + a.getSqlArgMap());
			infoList.add("sqlSpendTime:" + a.getSqlSpendTime());
			infoList.add("sqlCount:" + a.getSqlCount());
		}

		if (!StringUtils.isEmpty(a.getInJson())) {
			infoList.add("inJson:" + a.getInJson());
		}

		if (!StringUtils.isEmpty(a.getOutJson())) {
			infoList.add("outJson:" + a.getOutJson());
		}

		if (a.getThrowable() != null) {
			ErrorBean error = ErrorCode.getErroCode(a.getThrowable());
			infoList.add("Exception[" + error.getCode() + "]" + error.getInfo() + ":" + a.getThrowable().getMessage());
		}

		if (a.getLog() != null) {
			infoList.add("log:" + StringUtils.collectionToDelimitedString(a.getLog(), "\r\n    "));
		}

		return infoList;
	}
}
