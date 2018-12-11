/**
 * 
 */
package com.ppx.cloud.monitor.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ppx.cloud.common.exception.ErrorBean;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.common.util.MD5Utils;
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

		String uri = a.getUri() == null ? MonitorCache.getSeqUriDev(a.getUriSeq()) : a.getUri();
		
		
		StringBuilder accessSb = new StringBuilder(a.getIp()).append("[").append(beginTime).append("]")
				.append(a.getSpendTime()).append(" ").append(a.getMethod()).append(" ")
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
			// 开发环境SQL转换
			for (int i = 0; i < a.getSqlList().size(); i++) {
				String v = a.getSqlList().get(i);
				v = MonitorCache.getMd5SqlDev(v);
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
	
	public static Map<String, Object> getDebugMap(AccessLog a) {
		Map<String, Object> map = new HashMap<String, Object>();
        
		var sqlList = new ArrayList<String>(6);
		for (String sql : a.getSqlList()) {
			if (sql.length() != 32 || sql.indexOf(" ") > 0) {
				sqlList.add(MD5Utils.getMD5(sql));
			}
			else {
				sqlList.add(sql);
			}
		}
		
		// SQL部分
		map.put("sql", sqlList);
		
		
		map.put("sqla", a.getSqlArgMap());
		map.put("sqls", a.getSqlSpendTime());
		map.put("sqlc", a.getSqlCount());
		map.put("sqlb", a.getSqlBeginTime());

		map.put("params", a.getParams());
		map.put("inJson", a.getInJson());
		map.put("outJson", a.getOutJson());
   
        return map;
    }
}
