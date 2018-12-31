
package com.ppx.cloud.monitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.ppx.cloud.common.exception.custom.InitException;

/**
 * 监控配置
 * @author mark
 * @date 2018年7月2日
 */
@Configuration
public class MonitorConfig {
    

    public static boolean IS_DEBUG = false;
    
    public static boolean IS_WARNING = false;
    
    public static boolean IS_DEV = false;
    
    
    @Autowired
    private Environment env;
    
    @Bean
    public Object initMonitorConfig() {
        
        String active = env.getProperty("spring.profiles.active");
        if (StringUtils.isEmpty(active)) {
            throw new InitException("spring.profiles.active is empty");
        }
        IS_DEV = "dev".equals(active) ? true : false;
        if (IS_DEV) {
        	IS_DEBUG = true;
        	IS_WARNING = true;
        }
        return null;
    }
    
  
  

}
