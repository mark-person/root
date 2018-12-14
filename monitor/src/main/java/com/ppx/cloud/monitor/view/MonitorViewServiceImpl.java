package com.ppx.cloud.monitor.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.Row;
import com.ppx.cloud.common.jdbc.MyCriteria;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.monitor.output.PersistenceSupport;

@Service
public class MonitorViewServiceImpl extends PersistenceSupport {
    
	
	public List<Map<String, Object>> listDisplayService() {
		var returnList  = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			String sql = "select serviceId from service where serviceDisplay = 1";
			List<Row> list = t.sql(sql).fetchAll();
			for (Row row : list) {
				var map = new HashMap<String, Object>();
				map.put("serviceId", row.getString("serviceId"));
				returnList.add(map);
			}
		}
		return returnList;
	}

	public List<Map<String, Object>> listAllService(Page page) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		
		try (LogTemplate t = new LogTemplate()) {
			var cSql = new StringBuilder("select count(*) from service");
			var qSql = new StringBuilder("select * from service order by servicePrio desc");
			returnList = queryTablePage(t, page, cSql, qSql, null);
		}
		
		return returnList;
	}
	
	public List<Map<String, Object>> listStartup(Page page, String serviceId) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			MyCriteria c = new MyCriteria("where");
			c.addAnd("serviceId = ?", serviceId);
			
			var cSql = new StringBuilder("select count(*) from startup").append(c);
			var qSql = new StringBuilder("select * from startup").append(c).append(" order by startupTime desc");
			returnList = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}

	
	public List<Map<String, Object>> listAccess(Page page, String date, String beginTime, String endTime,
			String serviceId, String uriText) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("and").addAnd("a.accessTime >= ?", beginTime)
				.addAnd("a.accessTime <= ?", endTime)
				.addAnd("a.serviceId = ?", serviceId)
				.addAnd("s.uriText = ?", uriText);
			c.addPrePara(date);
			
			var cSql = new StringBuilder("select count(*) from access a left join map_uri_seq s on s.uriSeq = a.uriSeq"
					+ " where a.accessDate = ?").append(c);
			var qSql = new StringBuilder("select a.*, s.uriText from access a left join map_uri_seq s on s.uriSeq = a.uriSeq"
					+ " where a.accessDate = ?").append(c).append(" order by a.accessTime desc");
			returnList = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	public Map<String, Object> getAccess(String accessId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		var sql = "select a.*, s.uriText, l.marker, l.log from access a"
				+ " left join map_uri_seq s on s.uriSeq = a.uriSeq "
				+ " left join access_log l on l.accessId = a.accessId where a.accessId = ?";
		try (LogTemplate t = new LogTemplate()) {
			returnMap = fetchOne(t, sql, accessId);
		}
		return returnMap;
	}
	
	public List<Map<String, Object>> listError(Page page, String serviceId) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("serviceId = ?", serviceId);
			
			var cSql = new StringBuilder("select count(*) from error").append(c);
			var qSql = new StringBuilder("select e.*, s.uriText, a.accessInfo from error e" + 
					" left join access a on a.accessId = a.accessId" + 
					" left join map_uri_seq s on s.uriSeq = a.uriSeq").append(c).append(" order by errorTime desc");
			returnList = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	public Map<String, Object> getError(String accessId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		var sql = "select from error where accessId = ?";
		try (LogTemplate t = new LogTemplate()) {
			returnMap = fetchOne(t, sql, accessId);
		}
		return returnMap;
	}
	
	public List<Map<String, Object>> listGather(Page page, String serviceId) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("service_id = ?", serviceId);
			
			var cSql = new StringBuilder("select count(*) from gather").append(c);
			var qSql = new StringBuilder("select * from gather").append(c).append(" order by gatherTime desc");
			returnList = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	
	public List<Map<String, Object>> listStatUri(Page page, String uri) {
		
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		MyCriteria c = new MyCriteria("where");
		c.addAnd("uri = ?", uri);
		
		try (LogTemplate t = new LogTemplate()) {
			var cSql = new StringBuilder("select count(*) from stat_uri u").append(c);
			var qSql = new StringBuilder("select u.*, m.uriText from stat_uri u "
					+ "left join map_uri_seq m on m.uriSeq = u.uriSeq").append(c).append(" order by u.lasted desc");
			r = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		
		return r;
	}
	
	public List<Map<String, Object>> listStatSql(Page page, String sql) {
		
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		MyCriteria c = new MyCriteria("where");
		c.addAnd("sqlMd5 = ?", sql);
		
		try (LogTemplate t = new LogTemplate()) {
			var cSql = new StringBuilder("select count(*) from stat_sql s").append(c);
			var qSql = new StringBuilder("select s.*, m.sqlText from stat_sql s "
					+ "left join map_sql_md5 m on m.sqlMd5 = s.sqlMd5").append(c).append(" order by s.lasted desc");
			r = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		
		return r;
	}
	
	public List<Map<String, Object>> listStatResponse(Page page, String serviceId) {
		
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		MyCriteria c = new MyCriteria("where");
		c.addAnd("serviceId = ?", serviceId);
		
		try (LogTemplate t = new LogTemplate()) {
			var cSql = new StringBuilder("select count(*) from stat_response r").append(c);
			var qSql = new StringBuilder("select r.* from stat_response r").append(c).append(" order by r.hh desc");
			r = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		
		return r;
	}
	
	public List<Map<String, Object>> listStatWarning(Page page, String serviceId) {
		
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		MyCriteria c = new MyCriteria("where");
		c.addAnd("serviceId = ?", serviceId);
		
		try (LogTemplate t = new LogTemplate()) {
			var cSql = new StringBuilder("select count(*) from stat_warning w").append(c);
			var qSql = new StringBuilder("select w.* from stat_warning w").append(c).append(" order by w.lasted desc");
			r = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		
		return r;
	}
	
	
	
	public List<Map<String, Object>> listDebug(Page page, String serviceId, String date, String beginTime, String endTime,
			String uri, String marker) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("d.serviceId = ?", serviceId);
			c.addAnd("d.debugTime >= ?", Strings.isEmpty(beginTime) ? null : date + " " + beginTime);
			c.addAnd("d.debugTime <= ?", Strings.isEmpty(endTime) ? null : date + " " + endTime);
			c.addAnd("s.uriText = ?", uri);
			c.addAnd("exists(select 1 from access_log where accessId = d.accessId and marker = ?)", marker);
			
			var cSql = new StringBuilder("select count(*) from debug d "
					+ " left join access a on d.accessId = a.accessId "
					+ " left join map_uri_seq s on a.uriSeq = s.uriSeq").append(c);
			var qSql = new StringBuilder("select d.*, s.uriText, a.accessInfo, a.spendTime, (select group_concat(l.marker) from access_log l where l.accessId = d.accessId) marker"
					+ " from debug d"
					+ " left join access a on d.accessId = a.accessId"
					+ " left join map_uri_seq s on a.uriSeq = s.uriSeq")
					.append(c).append(" order by d.debugTime desc");
			returnList = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	
	public Map<String, Object> getDebug(String accessId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		var sql = "select d.*, s.uriText, a.accessInfo, a.spendTime" + 
				" from debug d" + 
				" left join access a on d.accessId = a.accessId" + 
				" left join map_uri_seq s on a.uriSeq = s.uriSeq where d.accessId = ?";
		try (LogTemplate t = new LogTemplate()) {
			returnMap = fetchOne(t, sql, accessId);
		}
		return returnMap;
	}
	
	
	
	
	

}
