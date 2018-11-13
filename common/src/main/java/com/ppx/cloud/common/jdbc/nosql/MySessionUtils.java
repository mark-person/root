/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;

/**
 * @author mark
 * @date 2018年11月13日
 */
public class MySessionUtils {

	private static Deque<Session> queue = new ArrayDeque<Session>(3);
	
	private static int useNum = 0;
	
	public synchronized static Session getSession() {
		if (useNum >= 3) {
			// 加上延时判断
			throw new RuntimeException("The no sql connection pool is full.");
			
		}
		useNum++;
		
		if (queue.isEmpty()) {
			Session mySession = new SessionFactory().getSession("mysqlx://localhost:33060?user=root&password=@Dengppx123456");
			queue.add(mySession);
			return mySession;
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
