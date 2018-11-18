package com.ppx.cloud.common.jdbc.nosql;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.AddStatement;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;

public class NoSqlTemplate implements AutoCloseable {
	
	public final static String LOG_SCHEMA = "LOG";

	private Session session;
	
	private Schema schema;
	
	private boolean isException = false;
	
	public NoSqlTemplate(String schemaName) {
		
		// TODO Auto-generated constructor stub
		session = SessionPool.getSession();
		schema = session.getSchema(LOG_SCHEMA);
	}

	@Override
	public void close() {
		System.out.println("close-----------");
		
		if (isException && session != null) {
			System.out.println("999999999999:rollback");
			session.rollback();
		}
		
		
		if (session != null) {
			SessionPool.closeSession(session);
		}
	}

	
	public void batchAdd(String name, List<Map<Object, Object>> list)  {
		
		try {
			Collection c = schema.createCollection(name, true);

			AddStatement as = null;
			for (Map<Object, Object> map : list) {
				String json = "";
				
				//int i = 1 / 0;
				json = new ObjectMapper().writeValueAsString(map);
				
				
				as = (as == null) ? c.add(json) : as.add(json);
			}
			if (as != null) {
				as.execute();
			}
		} catch (Throwable e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
		
	}

	public static void main(String[] args) {
		try (NoSqlTemplate t = new NoSqlTemplate(NoSqlTemplate.LOG_SCHEMA)) {

			t.batchAdd("aaa", List.of(Map.of("abc", "value_abc")));
		}

		System.out.println("------------end");
	}
}
