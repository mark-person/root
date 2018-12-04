
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
    
    // 需要排除导入导出等长时间的操作
    public static int DUMP_MAX_TIME = 5000;
    
    // 采集间格5分钟 5 * 60 * 1000
    public static int GATHER_INTERVAL = 5 * 60 * 1000;
    
    // 同步配置数据180秒(暂不支持动态修改)
    public static final int SYNC_CONF_INTERVAL = 180 * 1000;
    
    // cpu使用率超过就dump(暂不支持动态修改)
    public static final double MAX_CPU_DUMP = 0.7;
    
    // 堆内存占比超过就dump(暂不支持动态修改)
    public static final double MAX_MEMORY_DUMP = 0.85;
    
    @Autowired
    private Environment env;
    
    @Bean
    public Object initMonitorConfig() {
        
        String active = env.getProperty("spring.profiles.active");
        if (StringUtils.isEmpty(active)) {
            throw new InitException("spring.profiles.active is empty");
        }
        IS_DEV = "dev".equals(active) ? true : false;
        // IS_DEBUG = IS_DEV ? true : false;
        
        if (IS_DEV) {
        	IS_DEBUG = true;
        	IS_WARNING = true;
        }
        return null;
    }
    
  
  

}
