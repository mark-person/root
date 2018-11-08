package com.ppx.cloud.monitor.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ppx.cloud.common.exception.security.PermissionUrlException;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.util.MonitorUtils;


/**
 * 日志记录
 * @author mark
 * @date 2018年11月7日
 */
public class AccessLog {
    // ------------
    private String ip;
    private long beginTime;
    private long beginNanoTime;
    private long spendNanoTime;
    private String method;
    private String uri;
    private String queryString;
    private Throwable throwable;
    private Integer accountId;
    
    private List<String> sqlList = new ArrayList<String>(5);
    private Map<Integer, List<Object>> sqlArgMap = new HashMap<Integer, List<Object>>(5); 
    private List<Long> sqlBeginTime = new ArrayList<Long>(5);
    private List<Integer> sqlSpendTime = new ArrayList<Integer>(5);
    private List<Integer> sqlCount = new ArrayList<Integer>(5);
    
    private int transactionTimes = 0;
    private int getConnTimes = 0;
    private int releaseConnTimes = 0;
    
    private String cacheKey;
    // org.slf4j.Marker 用来搜索
    private List<String> marker;
    // logger.debug和logger.info日志， logger.error存入throwable
    private List<String> log;
    
    // 输入参数(LogConfig.isDebug控制)
    private String params;  
    // 输入json(LogConfig.isDebug控制) 改成Object(json转的pojo对象),按需要转换
    private String inJson;
    // 输出json(LogConfig.isDebug控制)
    private String outJson;
    
    // http的Referer
    private String referer;
  
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getBeginNanoTime() {
        return beginNanoTime;
    }

    public void setBeginNanoTime(long beginNanoTime) {
        this.beginNanoTime = beginNanoTime;
    }
    
    
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getSpendNanoTime() {
        return spendNanoTime;
    }

    public void setSpendNanoTime(long spendNanoTime) {
        this.spendNanoTime = spendNanoTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        if (uri.length() > 64) {
            throw new PermissionUrlException();
        }
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public void addSql(String sql) {
        // MongoDB索引字段的长度不能大于1024字节
        if (sql != null && sql.length() > 1024) {
            sql = sql.substring(0, 1024);
        }
        sqlList.add(sql);
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getInJson() {
        return inJson;
    }

    public void setInJson(String inJson) {
        this.inJson = inJson;
    }

    public String getOutJson() {
        return outJson;
    }

    public void setOutJson(String outJson) {
        this.outJson = outJson;
    }

    public List<Integer> getSqlSpendTime() {
        return sqlSpendTime;
    }

    public void setSqlSpendTime(List<Integer> sqlSpendTime) {
        this.sqlSpendTime = sqlSpendTime;
    }
    
    public void addSqlSpendTime(long sqlSpendTime) {
        this.sqlSpendTime.add((int)sqlSpendTime);
    }
    
    public List<Integer> getSqlCount() {
        return sqlCount;
    }

    public void setSqlCount(List<Integer> sqlCount) {
        this.sqlCount = sqlCount;
    }
    
    public void addSqlCount(Integer sqlCount) {
        this.sqlCount.add(sqlCount);
    }

    public int getTransactionTimes() {
        return transactionTimes;
    }

    public void setTransactionTimes(int transactionTimes) {
        this.transactionTimes = transactionTimes;
    }

    public int getGetConnTimes() {
        return getConnTimes;
    }

    public void setGetConnTimes(int getConnTimes) {
        this.getConnTimes = getConnTimes;
    }

    public int getReleaseConnTimes() {
        return releaseConnTimes;
    }

    public void setReleaseConnTimes(int releaseConnTimes) {
        this.releaseConnTimes = releaseConnTimes;
    }
    
    public void addTransactionTimes() {
        this.transactionTimes++;
    }

    public void addReleaseConnTimes() {
        this.releaseConnTimes++;
    }
    
    public void addGetConnTimes() {
        this.getConnTimes++;
    }

    public Map<Integer, List<Object>> getSqlArgMap() {
        return sqlArgMap;
    }

    public void setSqlArgMap(Map<Integer, List<Object>> sqlArgMap) {
        this.sqlArgMap = sqlArgMap;
    }
    
    public void addSqlArg(Integer index, Object[] args) {
        sqlArgMap.put(index, Arrays.asList(args));
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public List<Long> getSqlBeginTime() {
        return sqlBeginTime;
    }

    public void setSqlBeginTime(List<Long> sqlBeginTime) {
        this.sqlBeginTime = sqlBeginTime;
    }
    
    public void addSqlBeginTime(long sqlBeginTime) {
        this.sqlBeginTime.add(sqlBeginTime);
    }
    
    public void accessLog(Long sqlBeginTime) {
        this.sqlBeginTime.add(sqlBeginTime);
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> logList) {
        this.log = logList;
    }
    
    public void addLog(String msg) {
        if (this.log == null) {
            this.log = new ArrayList<String>(5);
        }
        this.log.add(msg);
    }

    public List<String> getMarker() {
        return marker;
    }

    public void setMarker(List<String> marker) {
        this.marker = marker;
    }
    
    public void addMarker(String msg) {
        if (this.marker == null) {
            this.marker = new ArrayList<String>(2);
        }
        this.marker.add(msg);
    }
    
    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    // http请求的日志
    public static AccessLog getInstance(HttpServletRequest request) {
        AccessLog accessLog = new AccessLog();
        accessLog.setBeginTime(System.currentTimeMillis());
        accessLog.setBeginNanoTime(System.nanoTime());
        accessLog.setIp(ApplicationUtils.getIpAddress(request));
        accessLog.setMethod(request.getMethod());
        accessLog.setUri(request.getRequestURI());
        accessLog.setQueryString(request.getQueryString());
        accessLog.setReferer(MonitorUtils.getReferer(request));
        return accessLog;
    }
}
