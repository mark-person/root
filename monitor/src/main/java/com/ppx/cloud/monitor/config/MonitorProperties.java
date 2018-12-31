/**
 * 
 */
package com.ppx.cloud.monitor.config;

/**
 * #由数据库初始化
 * @author mark
 * @date 2018年12月29日
 */
public class MonitorProperties {
	
	// 需要排除导入导出等长时间的操作
    public static int DUMP_MAX_TIME = 5000;
    
    // 采集间格5分钟 5 * 60 * 1000
    public static int GATHER_INTERVAL = 5 * 60 * 1000;
    
    // 同步配置数据180秒
    public static final int SYNC_CONF_INTERVAL = 180 * 1000;
    
    // cpu使用率超过就dump
    public static final double MAX_CPU_DUMP = 0.9;
    
    // 堆内存占比超过就dump
    public static final double MAX_MEMORY_DUMP = 0.9;
	
	
}
