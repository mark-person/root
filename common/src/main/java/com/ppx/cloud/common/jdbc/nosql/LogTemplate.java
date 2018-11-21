package com.ppx.cloud.common.jdbc.nosql;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.AddResult;
import com.mysql.cj.xdevapi.AddStatement;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.Result;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SqlResult;

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
	
	public AddResult add(String name, Map<Object, Object> map) {
		try {
			Collection c = schema.createCollection(name, true);
			AddStatement as = c.add(new ObjectMapper().writeValueAsString(map));
			return as.execute();
		} catch (Throwable e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
	}
	
	public AddResult batchAdd(String name, List<Map<Object, Object>> list)  {
		try {
			Collection c = schema.createCollection(name, true);
			AddStatement as = null;
			for (Map<Object, Object> map : list) {
				String json = new ObjectMapper().writeValueAsString(map);
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

	public static void main(String[] args) {
		try (LogTemplate t = new LogTemplate()) {
			t.batchAdd("aaa", List.of(Map.of("abc", "value_abc")));
		}

		System.out.println("------------end");
	}
}
