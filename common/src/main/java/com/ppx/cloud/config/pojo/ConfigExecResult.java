/**
 * 
 */
package com.ppx.cloud.config.pojo;

import java.util.Date;

/**
 * @author mark
 * @date 2019年1月17日
 */
public class ConfigExecResult {
	private String serviceId;
	private Integer execResult;
	private String execDesc;
	private Date created;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getExecResult() {
		return execResult;
	}

	public void setExecResult(Integer execResult) {
		this.execResult = execResult;
	}

	public String getExecDesc() {
		return execDesc;
	}

	public void setExecDesc(String execDesc) {
		this.execDesc = execDesc;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
