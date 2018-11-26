/**
 * 
 */
package com.ppx.cloud.monitor.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ppx.cloud.monitor.config.MonitorConfig;

/**
 * 用作sql和uri的缓存, 在StartMonitor时加载
 * @author mark
 * @date 2018年11月20日
 */
public class MonitorCache {
	
	// 存放数据库sql的md5值，用作缓存
	private static Set<String> sqlMd5Set = new HashSet<String>();
	
	// 存放数据库uri的sequence值，用作缓存
	private static Map<String, UriPojo> uriMap = new HashMap<String, UriPojo>();
	
	public static boolean containsSqlMd5(String sqlMd5) {
		return sqlMd5Set.contains(sqlMd5);
	}
	
	public static void addSqlMd5(String sqlMd5) {
		sqlMd5Set.add(sqlMd5);
	}
	
	public static UriPojo getUri(String uri) {
		return uriMap.get(uri);
	}
	
	public static void addUriSeq(String uri, UriPojo uriPojo) {
		uriMap.put(uri, uriPojo);
		
		if (MonitorConfig.IS_DEV) {
			addSeqUri(uriPojo.getUriSeq(), uri);
		}
	}
	
	
	
	// 开发环境用来把md5的sql或uri的seq转成对应的值 >>>>>
	
	private static Map<Integer, String> seqUriMap = new HashMap<Integer, String>();
	
	private static Map<String, String> md5SqlMap = new HashMap<String, String>();
	
	public static void addSeqUri(Integer seq, String uri) {
		seqUriMap.put(seq, uri);
	}
	
	public static String getSeqUri(Object seq) {
		return seqUriMap.get(seq);
	}
	
	public static void addMd5Sql(String sqlMd5, String sql) {
		md5SqlMap.put(sqlMd5, sql);
	}
	
	public static String getMd5Sql(String sqlMd5) {
		return md5SqlMap.get(sqlMd5);
	}
	
	
}
