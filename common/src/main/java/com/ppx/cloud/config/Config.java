/**
 * 
 */
package com.ppx.cloud.config;

/**
 * @author mark
 * @date 2019年1月9日
 */
public class Config {
	private String configName;
	private String configModule;
	private String configValue;
	private String configDesc;
	private Integer configStatus;
	
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getConfigModule() {
		return configModule;
	}
	public void setConfigModule(String configModule) {
		this.configModule = configModule;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getConfigDesc() {
		return configDesc;
	}
	public void setConfigDesc(String configDesc) {
		this.configDesc = configDesc;
	}
	public Integer getConfigStatus() {
		return configStatus;
	}
	public void setConfigStatus(Integer configStatus) {
		this.configStatus = configStatus;
	}

	

}
