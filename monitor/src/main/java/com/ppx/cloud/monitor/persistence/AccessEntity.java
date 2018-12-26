package com.ppx.cloud.monitor.persistence;


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppx.cloud.common.exception.ErrorPojo;
import com.ppx.cloud.common.exception.ErrorUtils;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.pojo.AccessLog;

public class AccessEntity {
    
    // 缩写:serviceId referer queryString method beginTime spendTime useMemory cacheKey accountId
	@JsonIgnore
    private String _id;
    private String sid;
    private String uri;
    private String r;
    private String str;
    private String m;
    private Date b;
    private int s;
    private String ip;
    private ErrorPojo e;
    private int mem;
    private String key;
    private Integer aid;
    private List<String> marker;
    private List<String> log;
    
    public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Date getB() {
        return b;
    }

    public void setB(Date b) {
        this.b = b;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public ErrorPojo getE() {
        return e;
    }

    public void setE(ErrorPojo e) {
        this.e = e;
    }
    
    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
    
    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }
    
    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }
    
    public List<String> getMarker() {
        return marker;
    }

    public void setMarker(List<String> marker) {
        this.marker = marker;
    }
    
    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public static AccessEntity getInstance(AccessLog a) {
        // 统一使用该objectId,包括日期，access_yyyy-MM-dd、error、debug、warning
        // 用a.getBeginTime()创建ObjectId使数据一致
        
        AccessEntity entity = new AccessEntity();
        entity.setSid(ApplicationUtils.getServiceId());
        entity.setB(a.getBeginTime());
        entity.setS(a.getSpendTime());
        entity.setIp(a.getIp());
        entity.setUri(a.getUri());
        entity.setM(a.getMethod());
        entity.setStr(a.getQueryString());
        entity.setAid(a.getAccountId());
        entity.setKey(a.getCacheKey());
        entity.setR(a.getReferer());
        
        long useMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        entity.setMem((int)(useMemory / 1024 / 1024));
        
        // 访问日志里异常处理
        if (a.getThrowable() != null) {
            ErrorPojo error = ErrorUtils.getErroCode(a.getThrowable());
            error.setErrmsg(a.getThrowable().getClass().getSimpleName());
            entity.setE(error);
        }
        
        if (a.getLog() != null) {
            entity.setLog(a.getLog());
        }
        
        return entity;
    }
    
}
