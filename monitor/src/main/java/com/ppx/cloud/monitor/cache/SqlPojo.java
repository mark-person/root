/**
 * 
 */
package com.ppx.cloud.monitor.cache;

/**
 * @author mark
 * @date 2018年11月26日
 */
public class SqlPojo {
	private Integer sqlMd5;
	
	private Long maxTime;

	public Integer getSqlMd5() {
		return sqlMd5;
	}

	public void setSqlMd5(Integer sqlMd5) {
		this.sqlMd5 = sqlMd5;
	}

	public Long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(Long maxTime) {
		this.maxTime = maxTime;
	}
	
	
}
