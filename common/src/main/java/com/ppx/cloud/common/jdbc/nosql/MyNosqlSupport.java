/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.AddStatement;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DocResult;
import com.mysql.cj.xdevapi.JsonNumber;
import com.mysql.cj.xdevapi.JsonString;
import com.mysql.cj.xdevapi.JsonValue;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.ppx.cloud.common.exception.custom.NosqlException;

/**
 * @author mark
 * @date 2018年11月13日
 */
public abstract class MyNosqlSupport {
	
	public void batchAdd(String name, List<Map<Object, Object>> list) {
		Session session = MySessionPool.getSession();
		try {
			Schema myDb = session.getDefaultSchema();
			Collection c = myDb.getCollection(name);
			
			AddStatement as = null;
			for (Map<Object, Object> map : list) {
				String json = new ObjectMapper().writeValueAsString(map);
				as = (as == null) ? c.add(json) : as.add(json);
			}
			if (as != null) {
				as.execute();
			}
		}  catch (Exception e) {
			throw new NosqlException(e);
		} finally {
			MySessionPool.closeSession(session);
		}
	}
	
	public DbDoc get(String collectionName, String id) {
		Session session = MySessionPool.getSession();
		DbDoc dbDoc = null;
		try {
			Schema myDb = session.getDefaultSchema();
			dbDoc =  myDb.getCollection(collectionName).getOne(id);
		} catch (Exception e) {
			throw new NosqlException(e);
		} finally {
			MySessionPool.closeSession(session);
		}
		return dbDoc;
	}
	
	
	public DbDoc fetchOne(String collectionName, String searchCondition, Object... values) {
		Session session = MySessionPool.getSession();
		
		DbDoc dbDoc = null;
		try {
			Schema myDb = session.getDefaultSchema();
			Collection c = myDb.getCollection(collectionName);
			
			DocResult myDocs = c.find(searchCondition).limit(1).bind(values).bind(values).execute();
			dbDoc = myDocs.fetchOne();
		} catch (Exception e) {
			throw new NosqlException(e);
		} finally {
			MySessionPool.closeSession(session);
		}
		
		return dbDoc;
	}
	
	public void add(String collectionName, Map<String, Object> map) {
		Session session = MySessionPool.getSession();
		
		try {
			Schema myDb = session.getDefaultSchema();
			Collection c = myDb.getCollection(collectionName);
			String json = new ObjectMapper().writeValueAsString(map);
			c.add(json).execute();
		}  catch (Exception e) {
			throw new NosqlException(e);
		} finally {
			MySessionPool.closeSession(session);
		}
	}
	
	public void testSql(String collectionName, String id, Map<?, ?> map) {
		Session session = MySessionPool.getSession();
		
		try {
			Schema myDb = session.getDefaultSchema();
			Collection c = myDb.getCollection(collectionName);
			
//			DbDoc doc = c.newDoc();
//			doc.put("_id", new JsonString().setValue("James"));
			
			
		
			
			DbDoc doc = c.newDoc();
			
			
			
			//doc.add("value",  new JsonNumber().setValue("66"));

			
			
			
//			doc.merge("value", new JsonNumber().setValue("11"), (a, b) -> {
//				System.out.println("---------------001");
//				return new JsonNumber().setValue("601");
//			});
//			
//			//String json = new ObjectMapper().writeValueAsString(map);
//			System.out.println("---------------002");
			
			
			
			
			// c.replaceOne(id, doc);
			
			//c.modify("_id = '100'")
	
			
			
			
		}  catch (Exception e) {
			throw new NosqlException(e);
		} finally {
			MySessionPool.closeSession(session);
		}
	}
	
}	
