/**
 * 
 */
package com.ppx.cloud.auth.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.config.ConfigExec;

/**
 * @author mark
 * @date 2019年1月14日
 */
public class AuthGrantConfigExec implements ConfigExec {
	
	@Autowired
	private EhCacheService ehCacheService;
	
	@Override
	public String run(String configValue) {
		ehCacheService.clearGrantLocalCache();
		return "";
	}
}
