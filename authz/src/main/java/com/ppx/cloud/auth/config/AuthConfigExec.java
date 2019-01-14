/**
 * 
 */
package com.ppx.cloud.auth.config;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.common.exception.custom.ConfigException;
import com.ppx.cloud.config.ConfigExec;

/**
 * @author mark
 * @date 2019年1月14日
 */
public class AuthConfigExec implements ConfigExec {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean run(String configValue) {
		
		try {
			if (Strings.isEmpty(configValue)) {
				throw new ConfigException("configValue couldn't be empty");
			}
			
			Map<String, Object> valMap = new ObjectMapper().readValue(configValue, Map.class);
			String ADMIN_PASSWORD = (String)valMap.get("ADMIN_PASSWORD");
			if (ADMIN_PASSWORD == null) {
				throw new ConfigException("ADMIN_PASSWORD couldn't be null");
			}
			String JWT_PASSWORD = (String)valMap.get("JWT_PASSWORD");
			if (JWT_PASSWORD == null) {
				throw new ConfigException("JWT_PASSWORD couldn't be null");
			}
			Integer JWT_VALIDATE_SECOND = (Integer)valMap.get("JWT_VALIDATE_SECOND");
			if (JWT_VALIDATE_SECOND == null) {
				throw new ConfigException("JWT_VALIDATE_SECOND couldn't be null");
			}
			
			AuthProperties.setAuthPoperties(ADMIN_PASSWORD, JWT_PASSWORD, JWT_VALIDATE_SECOND);
		} catch (IOException e) {
			throw new ConfigException(e.getMessage());
		}
		
		
		return true;
	}
}
