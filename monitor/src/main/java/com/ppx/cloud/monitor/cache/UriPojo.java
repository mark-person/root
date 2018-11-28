/**
 * 
 */
package com.ppx.cloud.monitor.cache;

/**
 * @author mark
 * @date 2018年11月26日
 */
public class UriPojo {
	
	private int uriSeq;
	
	private int maxTime;
	
	public UriPojo(int uriSeq, int maxTime) {
		this.uriSeq = uriSeq;
		this.maxTime = maxTime;
	}

	public int getUriSeq() {
		return uriSeq;
	}

	public void setUriSeq(int uriSeq) {
		this.uriSeq = uriSeq;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}
	
	
	
}
