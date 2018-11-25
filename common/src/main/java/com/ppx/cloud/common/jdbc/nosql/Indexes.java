/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.Result;
import com.mysql.cj.xdevapi.SqlResult;

/**
 * @author mark
 * @date 2018年11月25日
 */
public class Indexes {
	
	public List<Map<String, String>> indexList = new ArrayList<Map<String, String>>();
	
	private String collectionName;
	
	public Indexes() {
		
	}
	
	public static Indexes createIndex(String collectionName) {
		Indexes indexes = new Indexes();
		indexes.collectionName = collectionName;
		return indexes; 
	}
	
	
	public Indexes add(String field, String type) {
		Map<String, String> indexMap = new HashMap<String, String>();
		indexMap.put("field", field);
		indexMap.put("type", type);
		indexList.add(indexMap);
		return this;
	}
	
	
	public Result createIndex(LogTemplate t) {
		Set<String> indexNameSet = new HashSet<String>();
		indexList.forEach(m -> {
			indexNameSet.add("'" + m.get("field") + "'");
		});
		
		Collection c = t.getSchema().createCollection(collectionName, true);
		
		Set<String> existsIndexName = new HashSet<String>();
		String sql = "select index_name from sys.x$schema_index_statistics where table_schema = database() " + 
				"and table_name = '" + c.getName() + "' and index_name in (" + StringUtils.collectionToCommaDelimitedString(indexNameSet) + ")";
		SqlResult sr = t.sql(sql);
		sr.fetchAll().forEach(r -> {
			existsIndexName.add(r.getString("index_name"));
		});
		
		List<Map<String, String>> createIndexList = new ArrayList<Map<String, String>>();
		indexList.forEach(m -> {
			String f = m.get("field");
			if (!existsIndexName.contains(f)) {
				createIndexList.add(m);
			}
		});
		
		if (createIndexList.size() > 0) {
			String json = "";
			try {
				json = new ObjectMapper().writeValueAsString(createIndexList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			json = "{\"fields\":" + json + "}";
			
			return c.createIndex("idx_test_value2", json);
		}
		return null;
	}
	
	
	
	
}
