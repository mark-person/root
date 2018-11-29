/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.SqlResult;

/**
 * @author mark
 * @date 2018年11月22日
 */
@Deprecated
public class UpdateSqlBak {

	private String tableName;

	private Map<String, Object> valueMap = new LinkedHashMap<String, Object>();

	private List<String> setList = new ArrayList<String>();
	
	private List<Object> bindValueList = new ArrayList<Object>(); 
	
	
	public List<Object> getBindValueList() {
		return bindValueList;
	}

	public UpdateSqlBak(String tableName, String pkName, String pkValue) {
		this.tableName = tableName;
		valueMap.put(pkName, pkValue);
	}
	
	public UpdateSqlBak inc(String name, int n) {
		// 先加到value
		String s = "";
		if (n >= 0) {
			s = name + "=" + name + "+" + n;
		} else {
			s = name + "=" + name + n;
		}
		setList.add(s);
		return this;
	}

	public UpdateSqlBak max(String name, int n) {
		// 先加到value
		valueMap.put(name, n);
		String s = name + "=if(" + name + ">" + n + "," + name + "," + n + ")";
		setList.add(s);
		return this;
	}
	
	public UpdateSqlBak max(String name, int n, String setName, Object setValue) {
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

	public UpdateSqlBak min(String name, int n) {
		// 先加到value
		valueMap.put(name, n);
		String s = name + "=if(" + name + "<" + n + "," + name + "," + n + ")";
		setList.add(s);
		return this;
	}

	public UpdateSqlBak setOnInsert(String name, Object obj) {
		valueMap.put(name, obj);
		return this;
	}

	public UpdateSqlBak set(String name, Object obj) {
		// 先加到value
		valueMap.put(name, obj);
		String s = name + "=" + obj;
		setList.add(s);
		return this;
	}
	
	public UpdateSqlBak setJson(String name, Object obj) {
		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 先加到value
		valueMap.put(name, "?");
		String s = name + "=?";
		setList.add(s);
		bindValueList.add(json);
		bindValueList.add(json);
		return this;
	}
	
	public UpdateSqlBak setSql(String name, String insertValue, String setValue) {
		valueMap.put(name, insertValue);
		String s = name + "=" + setValue;
		setList.add(s);
		return this;
	}

	public String toString() {
		String columnString = StringUtils.collectionToCommaDelimitedString(valueMap.keySet());
		String valueString = StringUtils.collectionToCommaDelimitedString(valueMap.values());
		String setString = StringUtils.collectionToCommaDelimitedString(setList);
		
		String s = "insert into " + tableName + "(" + columnString + ") values(" + valueString
				+ ") on duplicate key update " + setString;
		//System.out.println("xxxxxxs:" + s);
		return s;
	}
	
	
	public UpdateSqlBak addToSet(String name, String value) {
		valueMap.put(name, "'[\"" + value + "\"]'");
		String s =  name + "=if(JSON_CONTAINS(" + name + ", '\"" + value + "\"', '$') != 1, JSON_EXTRACT(JSON_ARRAY_APPEND(" + name + ", '$', '" + value + "'), '$'), JSON_EXTRACT(" + name + ", '$'))";
		setList.add(s);
		return this;
	}
	
	public SqlResult execute(LogTemplate t) {
		if (bindValueList.isEmpty()) {
			return t.sql(this.toString());
		}
		else {
			return t.sql(this.toString(), bindValueList);
		}
	}
	
	
	
	public static void main(String[] args) {
		// insert into map_uri_seq(uri_text) values('/test/test') on duplicate key update uri_times=uri_times+1
		// 或使用insert into map_uri_seq(uri_tex) select '/test/test' from dual where not exists(select 1 from map_uri_seq where uri_text = '/test/test')
//		
//		UpdateSql u = new UpdateSql("stat_uri", "uri_seq", "(select uri_seq from map_uri_seq where uri_text = '/test/test')");
//		u.set("lasted", "now()");
//		u.inc("totalTime",100);
//		u.max("maxTime", 100);
//		u.inc("times", 1);
//		u.set("avgTime", "totalTime/times");
//		
//		
//		u.set("distribute", "'[1,0,0,0,0]'",  "JSON_SET(distribute, '$[0]', JSON_EXTRACT(distribute, '$[0]') + 1)");
//		
//		//u.addToSet("sql_set", "xxxxx");
//		
//		
//		//u.max("maxTime", 100, "maxDetail", Map.of("mark", 1233));
//		
//		
//		System.out.println("" + u);
		
		long t = System.nanoTime();
		try {
			// 每隔n秒看看有没有连接
			TimeUnit.SECONDS.sleep(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.nanoTime() - t < 10 * 1e9
		System.out.println("xxx:" + (10 * 1e2));
		System.out.println("xxx:" + (System.nanoTime() - t > 3 * 1e9));
		
	}
}
