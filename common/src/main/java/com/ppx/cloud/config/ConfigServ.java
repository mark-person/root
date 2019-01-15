/**
 * 
 */
package com.ppx.cloud.config;

import java.util.List;

/**
 * @author mark
 * @date 2019年1月14日
 */
public interface ConfigServ {
	List<Config> listConfig(String artifactId);
}
