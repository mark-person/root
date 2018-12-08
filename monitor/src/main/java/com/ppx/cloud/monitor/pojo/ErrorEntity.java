package com.ppx.cloud.monitor.pojo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.persistence.AccessEntity;

public class ErrorEntity {
 
    // 缩写:serverId beginTime errorCode paramsinJson
    @JsonIgnore
    private String _id;
    private String sid;
    
    private Date b;
    private String uri;
    private List<String> marker;
    private Integer c;
    private String msg;
    // ErrorCode.IGNORE_ERROR异常时，记录输入参数
    private String p;
    private String in;
            
    private AccessEntity a;
    
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    
    public String getHexId() {
        return _id;
    }

    public AccessEntity getA() {
        return a;
    }

    public void setA(AccessEntity a) {
        this.a = a;
    }
    
    public Date getB() {
        return b;
    }

    public void setB(Date b) {
        this.b = b;
    }
    
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }
    
    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }
    
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getMarker() {
        return marker;
    }

    public void setMarker(List<String> marker) {
        this.marker = marker;
    }

    public static ErrorEntity getInstance(AccessLog a, String _id) {
        ErrorEntity error = new ErrorEntity();
        error.set_id(_id);
        error.setB(a.getBeginTime());
        error.setUri(a.getUri());
        error.setMsg(a.getThrowable().getMessage());
        error.setSid(ApplicationUtils.getServiceId());
        return error;
    }
    
}