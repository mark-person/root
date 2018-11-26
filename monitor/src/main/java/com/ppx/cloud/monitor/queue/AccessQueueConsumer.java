package com.ppx.cloud.monitor.queue;

import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ppx.cloud.monitor.config.MonitorConfig;
import com.ppx.cloud.monitor.output.ConsoleImpl;
import com.ppx.cloud.monitor.output.PersistenceImpl;
import com.ppx.cloud.monitor.persistence.AccessEntity;
import com.ppx.cloud.monitor.pojo.AccessLog;


/**
 * 队列消费者,用于日志输出
 * @author mark
 * @date 2018年6月16日
 */
@Component
public class AccessQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AccessQueueConsumer.class);
  
    
    public void start() {
        // 启动后调用
        logger.info("AccessQueueConsumer.start()>>>>>>>>>>>>>>>>");
        
        new Thread(() ->  {
        	// 加上try内防止线程死掉
        	try {
        		while (true) {
        			consumeAccessLog();
        			Thread.sleep(20);
        		}
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void consumeAccessLog() {
        AccessLog a;
        while ((a = AccessQueue.getQueue().poll()) != null) {
            if (MonitorConfig.IS_DEV) {
                ConsoleImpl.print(a);
            }
            
            // 监控页面的查看不输出(异常时输出)
            if (a.getUri().indexOf("/monitorView/") < 0 || a.getThrowable() != null) {
                try {
                    logToDb(a);
                } catch (Throwable e) {
                    e.printStackTrace();
                    // 输出mongodb异常，则打印到控制台
                    System.err.println("Error(logToMongodb):" + e.getMessage());
                    if (!MonitorConfig.IS_DEV) {
                        ConsoleImpl.print(a);
                    }
                    
                }
            }
        }
    }
    

    private void logToDb(AccessLog a) {
        
        AccessEntity accessEntity = AccessEntity.getInstance(a);
        
        // 访问日志、 访问日志索引、uri统计、sql统计、响应统计
        PersistenceImpl.insertAccess(accessEntity);
        PersistenceImpl.createAccessIndex(accessEntity);
        PersistenceImpl.insertUriStat(a);
        PersistenceImpl.insertSqlStat(a);
//        
//        DebugEntity debugEntity = null;
//        if (MonitorConfig.IS_DEBUG) {
//            // debug日志
//            debugEntity = DebugEntity.getInstance(a, accessEntity.get_id());
//            mongodbService.insertDebug(debugEntity);  
//        }
//        if (a.getThrowable() == null) {
//            // 响应时间统计(有异常的不统计响应时间)
//            mongodbService.insertResponse(a, accessEntity.get_id());
//        }
//        else {
//            // 异常处理
//            debugEntity = debugEntity == null ? DebugEntity.getInstance(a, accessEntity.get_id()) : debugEntity;
//            ErrorEntity e = ErrorEntity.getInstance(a, accessEntity.get_id());
//            mongodbService.insertError(e, a.getThrowable(), debugEntity);
//        }
        
//        // warning访问日志 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//        if (MonitorConfig.IS_WARNING) {
//            // >>>>>>>>>>>>>> 警告信息 begin >>>>>>>>>>>>>>
//            BitSet bs = new BitSet();
//
//            // 检查是否有未关闭的数据库连接
//            String warn = AccessAnalysis.checkConnection(a.getGetConnTimes(), a.getReleaseConnTimes());
//            if (!StringUtils.isEmpty(warn)) {
//                BitSet b = new BitSet();
//                b.set(1);
//                bs.xor(b);
//            }
//
//            // 检查for update有没有加上事务
//            warn = AccessAnalysis.checkForUpdate(a.getSqlList(), a.getTransactionTimes());
//            if (!StringUtils.isEmpty(warn)) {
//                BitSet b = new BitSet();
//                b.set(2);
//                bs.xor(b);
//            }
//
//            // 检查非安全SQL，没有加上where条件
//            warn = AccessAnalysis.checkUnSafeSql(a.getSqlList());
//            if (!StringUtils.isEmpty(warn)) {
//                BitSet b = new BitSet();
//                b.set(3);
//                bs.xor(b);
//            }
//
//            // 检查事务个数是否大于1
//            warn = AccessAnalysis.checkTransactionTimes(a.getTransactionTimes());
//            if (!StringUtils.isEmpty(warn)) {
//                BitSet b = new BitSet();
//                b.set(4);
//                bs.xor(b);
//            }
//
//            // 检查多个操作SQL是否没有使用事务
//            warn = AccessAnalysis.checkNoTransaction(a.getSqlList(), a.getTransactionTimes());
//            if (!StringUtils.isEmpty(warn)) {
//                BitSet b = new BitSet();
//                b.set(5);
//                bs.xor(b);
//            }
//            
//            // 检查注入SQL
//            warn = AccessAnalysis.checkAntiSql(a.getSqlList());
//            if (!StringUtils.isEmpty(warn)) {
//                BitSet b = new BitSet();
//                b.set(6);
//                bs.xor(b);
//            }
//            
//            if (!bs.isEmpty()) {
//                mongodbService.insertWarning(a, bs);
//            }           
//        }
    }
    
   
    
}
