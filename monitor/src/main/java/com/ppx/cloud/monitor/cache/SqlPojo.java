/**
 * 
 */
package com.ppx.cloud.monitor.cache;

/**
 * @author mark
 * @date 2018年11月26日
 */
public class SqlPojo {
	private String sqlMd5;
	
	private int maxTime;
	
	public SqlPojo(String sqlMd5, int maxTime) {
		this.sqlMd5 = sqlMd5;
		this.maxTime = maxTime;
	}

	public String getSqlMd5() {
		return sqlMd5;
	}

	public void setSqlMd5(String sqlMd5) {
		this.sqlMd5 = sqlMd5;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}
	
	
}
