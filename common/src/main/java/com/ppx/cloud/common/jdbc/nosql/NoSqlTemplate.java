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

public class NoSqlTemplate implements AutoCloseable {
	
	private Session session;
	
	private Schema schema;
	
	private boolean isException = false;
	
	public NoSqlTemplate(String schemaName) {
		if (!SessionPool.SCHEMA_SET.contains(schemaName)) {
			throw new RuntimeException("no found schema:" + schemaName);
		}
		session = SessionPool.getSession();
		schema = session.getSchema(schemaName);
	}

	@Override
	public void close() {
		if (session != null) {
			if (isException) {
				session.rollback();
			}
			SessionPool.closeSession(session);
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
		try (NoSqlTemplate t = new NoSqlTemplate(SessionPool.SCHEMA_LOG)) {
			
			
			t.batchAdd("aaa", List.of(Map.of("abc", "value_abc")));
		}

		System.out.println("------------end");
	}
}
