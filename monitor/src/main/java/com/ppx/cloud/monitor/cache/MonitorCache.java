/**
 * 
 */
package com.ppx.cloud.monitor.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用作sql和uri的缓存, 在StartMonitor时加载
 * @author mark
 * @date 2018年11月20日
 */
public class MonitorCache {
	
	// 存放数据库sql的mds5值，用作缓存
	private static Set<String> sqlMd5Set = new HashSet<String>();
	
	// 存放数据库uri的sequence值，用作缓存
	private static Map<String, Integer> uriSeqMap = new HashMap<String, Integer>();
	
	public static boolean containsSqlMd5(String sqlMd5) {
		return sqlMd5Set.contains(sqlMd5);
	}
	
	public static void addSqlMd5(String sqlMd5) {
		sqlMd5Set.add(sqlMd5);
	}
	
	public static Integer getSqlSeq(String uri) {
		return uriSeqMap.get(uri);
	}
	
	public static void addUriSeq(String uri, Integer seq) {
		uriSeqMap.put(uri, seq);
	}
	
	
	
	
	
}
