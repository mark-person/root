package com.ppx.cloud.common.jdbc.nosql;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.AddResult;
import com.mysql.cj.xdevapi.AddStatement;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.Result;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SqlResult;
import com.mysql.cj.xdevapi.Table;
import com.ppx.cloud.common.config.ObjectMappingCustomer;

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
			AddStatement as = c.add(new ObjectMappingCustomer().writeValueAsString(obj));
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
	
	public SqlResult sql(Object sql) {
		if (sql == null) return null;
		try {
			SqlResult r = session.sql(sql.toString()).execute();
			return r;
		} catch (Exception e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
	}
	
	public SqlResult sql(Object sql, List<Object> bindValue) {
		if (sql == null) return null;
		try {
			SqlResult r = session.sql(sql.toString()).bind(bindValue).execute();
			return r;
		} catch (Exception e) {
			this.isException = true;
			throw new RuntimeException(e);
		}
	}
	
	public SqlResult sql(Object sql, Object... bindValue) {
		if (sql == null) return null;
		try {
			SqlResult r = session.sql(sql.toString()).bind(bindValue).execute();
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
	
	
	
	public Collection createCollection(String collectionName) {
		return schema.createCollection(collectionName, true);
	}
	
	public Collection getCollection(String collectionName) {
		return schema.getCollection(collectionName);
	}
	
	public DbDoc find(String collectionName, String id) {
		Collection c = schema.createCollection(collectionName, true);
		return c.getOne(id);
	}

	
	public Schema getSchema() {
		return schema;
	}
	
	public static void main(String[] args) {
		try (LogTemplate t = new LogTemplate()) {
			
			Map<?, ?> doc = t.find("conf", "192.168.101.73:8081");
			
			
			System.out.println("xxxxxxxx:" + Integer.parseInt(doc.get("gatherInterval").toString()) );
		}

		System.out.println("------------end");
	}
}
