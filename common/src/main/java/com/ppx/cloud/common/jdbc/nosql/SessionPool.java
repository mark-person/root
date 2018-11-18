/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayDeque;
import java.util.Deque;

import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;

/**
 * @author mark
 * @date 2018年11月13日
 */
public class SessionPool {
	
	// 最大连接数
	private final static int MAX_SIZE = 3;
	
	private final static String LOG = "log";

	private static Deque<Session> queue = new ArrayDeque<Session>(MAX_SIZE);
	
	private static int useNum = 0;
	
	public synchronized static Session getSession() {
		if (useNum >= MAX_SIZE) {
			// 加上延时判断
			throw new RuntimeException("The no sql connection pool is full. MAX_SIZE:" + MAX_SIZE);
			
		}
		useNum++;
		
		if (queue.isEmpty()) {
			Session session = new SessionFactory().getSession("mysqlx://localhost:33060?user=root&password=@Dengppx123456");
			session.createSchema(LOG, true);
			
			
			
			queue.add(session);
			return session;
		}
		else {
			return queue.pollFirst();
		}
	}
	
	public static void closeSession(Session session) {
		useNum--;
		session.commit();
		queue.addFirst(session);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
}
