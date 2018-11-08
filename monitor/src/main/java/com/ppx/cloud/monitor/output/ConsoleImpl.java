package com.ppx.cloud.monitor.output;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.ppx.cloud.monitor.pojo.AccessLog;
import com.ppx.cloud.monitor.util.AccessAnalysisUtils;
import com.ppx.cloud.monitor.util.AccessLogUtils;


/**
 * 控制台输出，在dev环境才打印，或者在日志队列超时才打印
 * @author mark
 * @date 2018年7月2日
 */
public class ConsoleImpl {

   
    
    public static void print(AccessLog a) {
        List<String> infoList = AccessLogUtils.getInfoList(a);

        // >>>>>>>>>>>>>> 警告信息 begin >>>>>>>>>>>>>>
        List<String> warnList = new ArrayList<String>();

        // 检查是否有未关闭的数据库连接
        String warn = AccessAnalysisUtils.checkConnection(a.getGetConnTimes(), a.getReleaseConnTimes());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }

        // 检查for update有没有加上事务
        warn = AccessAnalysisUtils.checkForUpdate(a.getSqlList(), a.getTransactionTimes());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }

        // 检查非安全SQL，没有加上where条件
        warn = AccessAnalysisUtils.checkUnSafeSql(a.getSqlList());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }

        // 检查事务个数是否大于1
        warn = AccessAnalysisUtils.checkTransactionTimes(a.getTransactionTimes());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }

        // 检查多个操作SQL是否没有使用事务
        warn = AccessAnalysisUtils.checkNoTransaction(a.getSqlList(), a.getTransactionTimes());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }

        // 检查重复SQL
        warn = AccessAnalysisUtils.checkDuplicateSql(a.getSqlList());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }

        // 检查返回Json是否规范
        warn = AccessAnalysisUtils.checkOutJson(a.getOutJson());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }
        
        // 检查非安全SQL，没有加上where条件
        warn = AccessAnalysisUtils.checkAntiSql(a.getSqlList());
        if (!StringUtils.isEmpty(warn)) {
            warnList.add(warn);
        }
    
        // <<<<<<<<<<<<<< 警告信息 end <<<<<<<<<<<<<<
        debugToConsole(infoList, warnList);
    }
    
    private static void debugToConsole(List<String> infoList, List<String> warnList) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.collectionToDelimitedString(infoList, "\r\n"));
        System.out.println(sb);
        if (warnList.size() > 0) {
            StringBuilder warn = new StringBuilder();
            warn.append("警告信息>>>>>>>>>>>\r\n");
            warn.append(StringUtils.collectionToDelimitedString(warnList, "\r\n"));
            System.out.println(warn.append("\r\n"));
        }
    }
}