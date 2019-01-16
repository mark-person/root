/**
 * 
 */
package com.ppx.cloud.config;

import java.util.List;
import java.util.Map;

/**
 * @author mark
 * @date 2019年1月14日
 */
public interface ConfigApiServ {
	List<Config> listConfig(String artifactId);
	
	Map<String, Object> callSync(String configName, String configValue);
}
