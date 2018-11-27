/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mark
 * @date 2018年11月22日
 */
public class UpdateSql {

	private String tableName;

	private Map<String, Object> valueMap = new LinkedHashMap<String, Object>();

	private List<String> setList = new ArrayList<String>();

	public UpdateSql(String tableName, String pkName, Integer pkValue) {
		this.tableName = tableName;
		valueMap.put(pkName, pkValue);
	}

	public UpdateSql inc(String name, int n) {
		// 先加到value
		String s = "";
		if (n > 0) {
			s = name + "=" + name + "+" + n;
		} else {
			s = name + "=" + name + n;
		}
		setList.add(s);
		return this;
	}

	public UpdateSql max(String name, int n) {
		// 先加到value
		valueMap.put(name, n);
		String s = name + "=if(" + name + ">" + n + "," + name + "," + n + ")";
		setList.add(s);
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
		valueMap.put(setName, "'" + json + "'");
		String s = setName + "=if(" + name + ">" + n + "," + setName + ",'" + json + "')";
		setList.add(s);
		return this;
	}

	public UpdateSql min(String name, int n) {
		// 先加到value
		valueMap.put(name, n);
		String s = name + "=if(" + name + "<" + n + "," + name + "," + n + ")";
		setList.add(s);
		return this;
	}

	public UpdateSql setOnInsert(String name, Object obj) {
		valueMap.put(name, obj);
		return this;
	}

	public UpdateSql set(String name, Object obj) {
		String s = name + "=" + obj;
		setList.add(s);
		return this;
	}

	public String toString() {
		String columnString = StringUtils.collectionToCommaDelimitedString(valueMap.keySet());
		String valueString = StringUtils.collectionToCommaDelimitedString(valueMap.values());
		String setString = StringUtils.collectionToCommaDelimitedString(setList);
		
		String s = "insert into " + tableName + "(" + columnString + ") values(" + valueString
				+ ") on duplicate key update " + setString;
		return s;
	}
	
	
	public UpdateSql addToSet(String name, String value) {
		valueMap.put(name, "'[" + value + "]'");
		String s =  name + "=if(JSON_CONTAINS(doc, '\"" + value + "\"', '$." + name + "') != 1, JSON_EXTRACT(JSON_ARRAY_APPEND(" + name + ", '$." + name
				+ "', '\"" + value + "\"'), '$." + name + "'), JSON_EXTRACT(doc, '$." + name + "'))";
		setList.add(s);
		return this;
	}

	public static void main(String[] args) {
		UpdateSql u = new UpdateSql("stat_uri", "uri_seq", 1);
		//u.setOnInsert("lasted", "now()");
		u.set("lasted", "now()");
		u.inc("times", 1);
		
		u.addToSet("set", "abc");
		//u.inc("totalTime", 5);
		//u.max("maxTime", 100);
		//u.max("maxTime", 100, "maxDetail", Map.of("mark", 1233));
		
		System.out.println("" + u);
	}
}
