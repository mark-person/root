/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.SqlResult;
import com.ppx.cloud.common.config.ObjectMappingCustomer;


/**
 * 
 * @author mark
 * @date 2018年11月29日
 */
public class UpdateSql {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateSql.class);

	private boolean upsert;
	
	private String tableName;

	private Map<String, Object> valueMap = new LinkedHashMap<String, Object>();

	private List<String> setList = new ArrayList<String>();
	
	private List<Object> bindValueList = new ArrayList<Object>(); 
	
	private Object[] pkValue;
	
	// 组合主键pkName用逗号分开
	public UpdateSql(boolean upsert, String tableName, String pkName, Object... pkValue) {
		this.upsert = upsert;
		this.tableName = tableName;
		this.pkValue = pkValue.clone();
		if (upsert) {
			valueMap.put(pkName, "?");
			bindValueList.addAll(Arrays.asList(pkValue));
		}
		else {
			valueMap.put(pkName.replaceAll(",", "=? and ") + "=?", "?");
		}
	}
	
	public UpdateSql inc(String name, int n) {
		if (upsert) {
			valueMap.put(name, n);
		}
		setList.add(name + "=" + name + (n >= 0 ? "+" + n : n));
		return this;
	}

	public UpdateSql max(String name, int n) {
		if (upsert) {
			valueMap.put(name, n);
		}
		setList.add(name + "=if(" + name + ">" + n + "," + name + "," + n + ")");
		return this;
	}
	
	public UpdateSql max(String name, int n, String setName, Object setValue) {
		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(setValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 先加到value
		valueMap.put(setName, "?");
		String s = setName + "=if(" + name + ">" + n + "," + setName + ",?)";
		bindValueList.add(json);
		bindValueList.add(json);
		setList.add(s);
		return this;
	}

	public UpdateSql min(String name, int n) {
		if (upsert) {
			valueMap.put(name, n);
		}
		setList.add(name + "=if(" + name + "<" + n + "," + name + "," + n + ")");
		return this;
	}

	public UpdateSql setOnInsert(String name, Object val) {
		valueMap.put(name, val);
		bindValueList.add(val);
		return this;
	}
	
	public UpdateSql set(String name, Object val) {
		if (upsert) {
			valueMap.put(name, "?");
			bindValueList.add(val);
		}
		setList.add(name + "=?");
		bindValueList.add(val);
		return this;
	}

	public UpdateSql setSql(String name, String sql) {
		if (upsert) {
			valueMap.put(name, sql);
		}
		setList.add(name + "=" + sql);
		return this;
	}
	
	public UpdateSql setJson(String name, Object obj) {
		String json = "";
		try {
			json = new ObjectMappingCustomer().writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (upsert) {
			valueMap.put(name, "?");
			bindValueList.add(json);
		}
		setList.add(name + "=?");
		bindValueList.add(json);
		return this;
	}
	
	public UpdateSql set(String name, Object insertVal, Object setVal) {
		if (upsert) {
			valueMap.put(name, "?");
			bindValueList.add(insertVal);
		}
		setList.add(name + "=?");
		bindValueList.add(setVal);
		return this;
	}
	
	public UpdateSql setSql(String name, String insertSql, String setSql) {
		if (upsert) {
			valueMap.put(name, insertSql);
		}
		setList.add(name + "=" + setSql);
		return this;
	}

	public String toString() {
		String columnString = StringUtils.collectionToCommaDelimitedString(valueMap.keySet());
		String valueString = StringUtils.collectionToCommaDelimitedString(valueMap.values());
		String setString = StringUtils.collectionToCommaDelimitedString(setList);
		String s = "";
		if (upsert) {
			s = "insert into " + tableName + "(" + columnString + ") values(" + valueString
					+ ") on duplicate key update " + setString;
		}
		else {
			columnString = StringUtils.collectionToDelimitedString(valueMap.keySet(), "and");
			s = "update " + tableName + " set " + setString + " where " + columnString;
			bindValueList.addAll(Arrays.asList(pkValue));
		}
		logger.debug("sql:{}", s);
		return s;
	}
	
	public UpdateSql addToSet(String name, String value) {
		if (upsert) {
			valueMap.put(name, "'[\"" + value + "\"]'");
		}
		String s =  name + "=if(JSON_CONTAINS(" + name + ", '\"" + value + "\"', '$') != 1, JSON_EXTRACT(JSON_ARRAY_APPEND(" + name + ", '$', '" + value + "'), '$'), JSON_EXTRACT(" + name + ", '$'))";
		setList.add(s);
		return this;
	}
	
	public SqlResult execute(LogTemplate t) {
		logger.debug("bindValueList:{}", bindValueList);
		return t.sql(this.toString(), bindValueList);
	}
	
	public static void main(String[] args) {
		try (LogTemplate t = new LogTemplate()) {
			UpdateSql u = new UpdateSql(false, "stat_uri", "uri_seq", 600);
			u.inc("times", 1);
			u.max("maxTime", 10);
			u.inc("totalTime", 10);
			u.setSql("avgTime", "totalTime/times");
			u.set("lasted", new Date());
			u.execute(t);
		}
		System.out.println("-------------end");
	}
}
