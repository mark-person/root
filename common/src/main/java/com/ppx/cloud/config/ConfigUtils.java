/**
 * 
 */
package com.ppx.cloud.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mark
 * @date 2019年1月9日
 */
public class ConfigUtils {
	private static Map<String, ConfigExec> bindMap = new HashMap<String, ConfigExec>();
	
	public static ConfigExec getConfigExec(String configName) {
		return bindMap.get(configName);
	}
	
	public static void bindConfigExec(String configName, ConfigExec configExec) {
		bindMap.put(configName, configExec);
	}
}
