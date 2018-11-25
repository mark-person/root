package com.ppx.cloud.common.jdbc.nosql;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.AddResult;
import com.mysql.cj.xdevapi.AddStatement;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.JsonString;
import com.mysql.cj.xdevapi.JsonValue;
import com.mysql.cj.xdevapi.Result;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SqlResult;
import com.mysql.cj.xdevapi.Table;

/**
 * c.modify("_id='100'").patch("{\"value\":if($.value2 is null, 1, 2)}").execute();
 * c.modify("_id='100'").patch("{\"value\":cast(11.0 as SIGNED INTEGER)}").execute();
 * 
 * insert into test(doc) values("{\"_id\": \"100\", \"times3\": 1}") 
 * on duplicate key update doc = JSON_SET(doc, '$.times3', ifnull(JSON_EXTRACT(doc,'$.times3'), 0) + 1)
 * @author mark
 *
 */
public class LogTemplate implements AutoCloseable {
	
	private Session session;
	
	private Schema schema;
	
	private boolean isException = false;
	
	public LogTemplate() {
		session = LogSessionPool.getSession();
		schema = session.getDefaultSchema();
	}

	@Override
	public void close() {
		if (session != null) {
			if (isException) {
				session.rollback();
			}
			LogSessionPool.closeSession(session);
		}
	}
	
	public Result addOrReplaceOne(String name, String id,  Map<String, Object> map) {
		try {
			Collection c = schema.createCollection(name, true);
			return c.addOrReplaceOne(id, new ObjectMapper().writeValueAsString(map));
		} catch (Throwable e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
	}
	
	public AddResult add(String name, Object obj) {
		try {
			Collection c = schema.createCollection(name, true);
			AddStatement as = c.add(new ObjectMapper().writeValueAsString(obj));
			
			return as.execute();
		} catch (Throwable e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
	}
	
	public AddResult batchAdd(String name, List<Object> list)  {
		try {
			Collection c = schema.createCollection(name, true);
			AddStatement as = null;
			for (Object obj : list) {
				String json = new ObjectMapper().writeValueAsString(obj);
				as = (as == null) ? c.add(json) : as.add(json);
			}
			if (as != null) {
				AddResult addResult = as.execute();
				return addResult;
			}
		} catch (Throwable e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public SqlResult sql(String sql) {
		try {
			SqlResult r = session.sql(sql).execute();
			return r;
		} catch (Exception e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
	}
	
	public boolean existsTable(String tableName) {
		List<Table> list = schema.getTables(tableName);
		return list.isEmpty() ? false : true;
	}

	
	// 测试用
	public Schema getSchema() {
		return schema;
	}
	public static void main(String[] args) {
		try (LogTemplate t = new LogTemplate()) {
			
			Collection c = t.getSchema().createCollection("test", true);
			
			String index = "{\"fields\": [{\"field\": \"$.value\", \"type\": \"numeric\" }]}";
			c.createIndex("idx_test_value", index);
			
		}

		System.out.println("------------end");
	}
}
