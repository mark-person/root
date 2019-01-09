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
	
	public static ConfigExec getConfigExec(String configParam) {
		return bindMap.get(configParam);
	}
	
	public static void bindConfigExec(String configParam, ConfigExec configExec) {
		bindMap.put(configParam, configExec);
	}
}
