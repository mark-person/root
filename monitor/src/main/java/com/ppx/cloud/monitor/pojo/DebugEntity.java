package com.ppx.cloud.monitor.pojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppx.cloud.common.config.ObjectMappingCustomer;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.persistence.AccessEntity;

public class DebugEntity {
    // 缩写:serverId beginTime
    @JsonIgnore
    private int accessId;
    private String sid;
    private Date b;
    private String uri;
    private List<String> marker;
    
    // 缩写:sqlList sqlArgs sqlSpendTime sqlBeginTime sqlCount
    private List<String> sql = new ArrayList<String>();
    private String sqla;
    private String sqls;
    private String sqlb;
    private String sqlc;
    // 缩写对应:params inJson outJson
    private String p;
    private String in;
    private String out;
    
    private AccessEntity a;
   
    
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
    public int getAccessId() {
		return accessId;
	}

	public void setAccessId(int accessId) {
		this.accessId = accessId;
	}

	public AccessEntity getA() {
        return a;
    }

    public void setA(AccessEntity a) {
        this.a = a;
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

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public List<String> getSql() {
       
        return sql;
    }

    public void setSql(List<String> sql) {
        this.sql = sql;
    }

    public String getSqls() {
        return sqls;
    }

    public void setSqls(String sqls) {
        this.sqls = sqls;
    }    

    public String getSqlc() {
        return sqlc;
    }

    public void setSqlc(String sqlc) {
        this.sqlc = sqlc;
    }

    public String getSqla() {
        return sqla;
    }

    public void setSqla(String sqla) {
        this.sqla = sqla;
    }

    public String getSqlb() {
//        if (sqlb != null) {
//           String[] b = sqlb.split(",");
//           List<String> list = new ArrayList<String>();
//           for (String time : b) {
//               String t = new SimpleDateFormat("HH:mm:ss SSS").format(new Date(Long.parseLong(time)));
//               list.add(t);
//           }
//           return StringUtils.collectionToCommaDelimitedString(list);
//        }
        return sqlb;
    }

    public void setSqlb(String sqlb) {
        this.sqlb = sqlb;
    }
    
    public Date getB() {
        return b;
    }

    public void setB(Date b) {
        this.b = b;
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

    public static DebugEntity getInstance(AccessLog a, int accessId) {
        DebugEntity debug = new DebugEntity();
        
        debug.setAccessId(accessId);
        debug.setB(a.getBeginTime());
        debug.setSid(ApplicationUtils.getServiceId());
        debug.setUri(a.getUri());
        debug.setMarker(a.getMarker());

        // SQL部分
        debug.setSql(a.getSqlList().isEmpty() ? null : a.getSqlList());
        StringBuilder sqla = new StringBuilder();
        if (!a.getSqlArgMap().isEmpty()) {
            a.getSqlArgMap().forEach((k, v) -> {
                sqla.append(v.toString());
            });
        }
        debug.setSqla(sqla.length() == 0 ? null : sqla.toString());
        debug.setSqls(a.getSqlSpendTime().isEmpty() ? null : StringUtils.collectionToCommaDelimitedString(a.getSqlSpendTime()));
        debug.setSqlc(a.getSqlCount().isEmpty() ? null : StringUtils.collectionToCommaDelimitedString(a.getSqlCount()));
        debug.setSqlb(a.getSqlBeginTime().isEmpty() ? null : StringUtils.collectionToCommaDelimitedString(a.getSqlBeginTime()));
        
        // 输入输出参数部分
        debug.setP(StringUtils.isEmpty(a.getParams()) ? null : a.getParams());
        debug.setIn(StringUtils.isEmpty(a.getInJson()) ? null : a.getInJson());
        debug.setOut(StringUtils.isEmpty(a.getOutJson()) ? null : a.getOutJson());
        
        return debug;
    }
    
    public String toJsonObject() {
    	String debugJson = "";
        try {
            debugJson = new ObjectMappingCustomer().writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return debugJson;
    }
}