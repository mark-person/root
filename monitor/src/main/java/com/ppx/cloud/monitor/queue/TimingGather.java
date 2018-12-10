/**
 * 
 */
package com.ppx.cloud.monitor.queue;

import java.util.BitSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.config.MonitorConfig;
import com.ppx.cloud.monitor.output.PersistenceImpl;
import com.ppx.cloud.monitor.util.MonitorUtils;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author mark
 * @date 2018年11月21日
 */
public class TimingGather {

		
	public static void gather() {
		Date gatherTime = new Date();
		
		// 数据库当前连接数
        int dsActive = 0;
        try {
            HikariDataSource ds = (HikariDataSource)ApplicationUtils.context.getBean(DataSource.class);
            dsActive = ds.getHikariPoolMXBean() == null ? dsActive : ds.getHikariPoolMXBean().getActiveConnections();
        } catch (Throwable e) {
            dsActive = -1;
        }
                
        Map<String, Object> gatherMap = new LinkedHashMap<String, Object>();
        long mMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        gatherMap.put("mMem", mMem);
        gatherMap.put("tMem", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        gatherMap.put("fMem", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        long uMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        gatherMap.put("uMem", uMem);
        
        long usableSpace = MonitorUtils.getUsableSpace();
        gatherMap.put("space", usableSpace);
        gatherMap.put("dsActive", dsActive);
        gatherMap.put("queueSize", AccessQueue.getQueueSize());
        
        // 内存配置信息 isDebug isWarning gatherInterval dumpThreadMaxTime
        StringBuilder configStr = new StringBuilder("debug:").append(MonitorConfig.IS_DEBUG)
                .append(",warning:").append(MonitorConfig.IS_WARNING)
                .append(",interval:").append(MonitorConfig.GATHER_INTERVAL)
                .append(",maxTime:").append(MonitorConfig.DUMP_MAX_TIME);
        gatherMap.put("config", configStr.toString());
        
        // CPU
        gatherMap.put("sCpuLoad", MonitorUtils.getSystemCpuLoad());
        double pCpuLoad = MonitorUtils.getProcessCpuLoad();
        gatherMap.put("pCpuLoad", pCpuLoad);
        
        // 请求,包括超时时dump
        boolean isOverCpu = pCpuLoad >= MonitorConfig.MAX_CPU_DUMP ? true : false;
        boolean isOverMem = Double.valueOf(uMem) / mMem >= MonitorConfig.MAX_MEMORY_DUMP ? true : false;
        if (isOverCpu) {
            gatherMap.put("cpuOverDump", MonitorUtils.getCpuTopDetail()); 
        }
        else if (isOverMem) {
            gatherMap.put("memOverDump", MonitorUtils.getCpuTopDetail());
        }
        Map<String, Object> requestInfo = MonitorUtils.getRequestInfo(isOverMem);
        gatherMap.put("requestInfo", requestInfo);
        boolean isOverTime = (Boolean)requestInfo.get("isOverTime");
        
        BitSet isOverBs = new BitSet();
        if (isOverCpu) {
        	BitSet b = new BitSet(1);
			b.set(1);
			isOverBs.xor(b);
        }
        if (isOverMem) {
        	BitSet b = new BitSet(1);
			b.set(2);
			isOverBs.xor(b);
        }
        if (isOverTime) {
        	BitSet b = new BitSet(1);
			b.set(3);
			isOverBs.xor(b);
        }
        int isOver = isOverBs.cardinality() > 0 ? (int)isOverBs.toLongArray()[0] : 0;
        Long maxProcessingTime = (Long)requestInfo.get("maxProcessingTime");
        int concurrentN = (int)requestInfo.get("concurrentN");
        
        
        // 更新服务，加上更新时间, 最新 堆使用内存  响应时间由响应时间里更新  数据库连接 硬盘 CPU
        Map<String, Object> lastUpdate = new LinkedHashMap<String, Object>();
        lastUpdate.put("lastUseMemory", uMem);
        lastUpdate.put("lastDsActive", dsActive);
        lastUpdate.put("lastUsableSpace", usableSpace); 
        lastUpdate.put("lastProcessCpuLoad", pCpuLoad);
        lastUpdate.put("modified", new Date());
        lastUpdate.put("lastConcurrentN", requestInfo.get("concurrentN"));
        try (LogTemplate t = new LogTemplate()) {
        	PersistenceImpl.getInstance(t).insertGather(gatherTime, isOver, maxProcessingTime, concurrentN, gatherMap, lastUpdate);
        }
	}
}
