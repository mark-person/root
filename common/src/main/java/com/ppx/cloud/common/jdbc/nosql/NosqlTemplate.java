/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DocResult;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;

/**
 * @author mark
 * @date 2018年11月13日
 */
public class NosqlTemplate {
	private Session session;
	
	public NosqlTemplate(Session session) {
		this.session = session;
	}
	
	public DbDoc fetchOne(String collectionName, String searchCondition) {
		Schema myDb = session.getDefaultSchema();
		Collection c = myDb.getCollection(collectionName);
		
		
		DbDoc dbDoc = null;
		try {
			DocResult myDocs = c.find("name = ?").limit(1).bind("param", "abcSabc").execute();
			dbDoc = myDocs.fetchOne();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			MySessionUtils.closeSession(session);
		}
		
		return dbDoc;
	}
	
}
