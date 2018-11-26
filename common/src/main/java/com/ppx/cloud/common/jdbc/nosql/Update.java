/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mark
 * @date 2018年11月22日
 */
public class Update {

	private String collectionName;

	private Map<String, Object> valueMap = new LinkedHashMap<String, Object>();

	private List<String> setList = new ArrayList<String>();

	public Update(String collectionName, String _id) {
		this.collectionName = collectionName;
		valueMap.put("_id", _id);
	}

	public Update inc(String name, int n) {
		// 先加到value
		String s = "";
		valueMap.put(name, n);
		if (n > 0) {
			s = "'$." + name + "', ifnull(convert(JSON_EXTRACT(doc,'$." + name + "'), SIGNED), 0)+" + n;
		} else {
			s = "'$." + name + "', ifnull(convert(JSON_EXTRACT(doc,'$." + name + "'), SIGNED), 0)" + n;
		}
		setList.add(s);
		return this;
	}

	public Update max(String name, int n) {
		// 先加到value
		valueMap.put(name, n);
		String s = "'$." + name + "', convert(if(JSON_EXTRACT(doc,'$." + name + "') > " + n + ", JSON_EXTRACT(doc,'$." + name
				+ "'), " + n + "), SIGNED)";
		setList.add(s);
		return this;
	}
	
	public Update max(String name, int n, String setName, Object setValue) {
		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(setValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 先加到value
		
		valueMap.put(setName, setValue);
		String s = "'$." + setName + "', if(JSON_EXTRACT(doc,'$." + name + "') > " + n + ", JSON_EXTRACT(doc,'$." + setName
				+ "'), '" + json + "')";
		setList.add(s);
		return this;
	}

	public Update min(String name, int n) {
		// 先加到value
		valueMap.put(name, n);
		String s = "'$." + name + "', convert(if(JSON_EXTRACT(doc,'$." + name + "') < " + n + ", JSON_EXTRACT(doc,'$." + name
				+ "'), " + n + "), SIGNED)";
		setList.add(s);
		return this;
	}

	public Update setOnInsert(String name, Object obj) {
		valueMap.put(name, obj);
		return this;
	}

	public Update set(String name, Object obj) {
		String s = "";
		if (obj instanceof String) {
			s = "'$." + name + "', '" + obj + "'";
		} else {
			s = "'$." + name + "', " + obj;
		}
		setList.add(s);
		return this;
	}

	public String toString() {
		String valueString = "";
		try {
			valueString = new ObjectMapper().writeValueAsString(valueMap);
			//valueString = valueString.replaceAll("\\\"", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String setString = StringUtils.collectionToCommaDelimitedString(setList);
		String s = "insert into " + collectionName + "(doc) values('" + valueString
				+ "') on duplicate key update doc = JSON_SET(doc, " + setString + ")";
		return s;
	}

	public static void main(String[] args) {
		Update u = new Update("conf", "1000");
		u.max("times", 1200, "max", Map.of("abc", "abcValue"));
		System.out.println("" + u);
	}
}
