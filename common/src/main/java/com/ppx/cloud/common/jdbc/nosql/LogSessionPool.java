/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;

/**
 * @author mark
 * @date 2018年11月13日
 */
public class LogSessionPool {
	
	// 最大连接数
	private final static int MAX_SIZE = 3;
	
	private final static String SCHEMA_LOG = "log";
	
	private static boolean isFirstRun = true;

	private static Deque<Session> queue = new ArrayDeque<Session>(MAX_SIZE);
	
	private static int activeNum = 0;
	
	public synchronized static Session getSession() {
		if (activeNum >= MAX_SIZE) {
			// 加上延时判断
			long t = System.nanoTime();
			do {
				try {
					// 每隔n秒看看有没有连接
					TimeUnit.SECONDS.sleep(2);
					if (!queue.isEmpty()) {
						Session returnSession = queue.pollFirst();
						activeNum++;
						return returnSession;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (System.nanoTime() - t < 10 * 1e9);
			
			throw new RuntimeException("The no sql connection, pool is full. MAX_SIZE:" + MAX_SIZE);
		}
		
		
		if (queue.isEmpty()) {
			// 改成启动判断是不是要创建schema,然后取的是带schema的连接
			
			if (isFirstRun) {
				Session session = new SessionFactory().getSession("mysqlx://localhost:33060?user=root&password=@Dengppx123456");
				session.createSchema(SCHEMA_LOG, true);
				isFirstRun = false;
			}
			
			Session returnSession =  new SessionFactory().getSession("mysqlx://localhost:33060/" + SCHEMA_LOG + "?user=root&password=@Dengppx123456");
			activeNum++;
			return returnSession;
		}
		else {
			Session returnSession = queue.pollFirst();
			activeNum++;
			return returnSession;
		}
	}
	
	public static void closeSession(Session session) {
		session.commit();
		queue.addFirst(session);
		activeNum--;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
}
