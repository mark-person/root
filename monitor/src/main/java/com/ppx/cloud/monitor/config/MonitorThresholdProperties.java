/**
 * 
 */
package com.ppx.cloud.monitor.config;

/**
 * # 由主程序使用数据库初始化
 * @author mark
 * @date 2018年12月29日
 */
public class MonitorThresholdProperties {
	
	// 需要排除导入导出等长时间的操作
    public static int DUMP_MAX_TIME = 5000;
    
    // 采集间格5分钟 5 * 60 * 1000
    public static int GATHER_INTERVAL = 5 * 60 * 1000;
    
    // cpu使用率超过就dump
    public static double MAX_CPU_DUMP = 0.9;
    
    // 堆内存占比超过就dump
    public static double MAX_MEMORY_DUMP = 0.9;
	
    public static void setMonitorProperties(int DUMP_MAX_TIME, int GATHER_INTERVAL,
    		double MAX_CPU_DUMP, double MAX_MEMORY_DUMP) {
    	MonitorThresholdProperties.DUMP_MAX_TIME = DUMP_MAX_TIME;
    	MonitorThresholdProperties.GATHER_INTERVAL = GATHER_INTERVAL;
    	MonitorThresholdProperties.MAX_CPU_DUMP = MAX_CPU_DUMP;
    	MonitorThresholdProperties.MAX_MEMORY_DUMP = MAX_MEMORY_DUMP;
    }
}
