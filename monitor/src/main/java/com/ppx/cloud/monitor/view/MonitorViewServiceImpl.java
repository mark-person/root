package com.ppx.cloud.monitor.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyCriteria;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.monitor.output.PersistenceSupport;

@Service
public class MonitorViewServiceImpl extends PersistenceSupport {
    
	
	public List<Map> listDisplayService() {
		
		return null;
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
	
	public List<Map<String, Object>> listStart(Page page, String sid) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			MyCriteria c = new MyCriteria("where");
			c.addAnd("json_extract(doc, '$.sid') = ?", sid);
			
			var cSql = new StringBuilder("select count(*) from col_start").append(c);
			var qSql = new StringBuilder("select doc from col_start").append(c).append(" order by json_extract(doc, '$.startTime') desc");
			returnList = queryCollectionPage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}

	
	public List<Map<String, Object>> listAccess(Page page, String date, String serviceId) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("accessDate = ?", date);
			c.addAnd("serviceId = ?", serviceId);
			
			var cSql = new StringBuilder("select count(*) from access").append(c);
			var qSql = new StringBuilder("select * from access").append(c).append(" order by accessTime desc");
			returnList = queryTablePage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	public List<Map<String, Object>> listError(Page page, String sid) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("json_extract(doc, '$.sid') = ?", sid);
			
			var cSql = new StringBuilder("select count(*) from col_error").append(c);
			var qSql = new StringBuilder("select doc from col_error").append(c).append(" order by json_extract(doc, '$.b') desc");
			returnList = queryCollectionPage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	public List<Map<String, Object>> listGather(Page page, String sid) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("service_id = ?", sid);
			
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
	
	
	
	public List<Map<String, Object>> listDebug(Page page, String sid) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("json_extract(doc, '$.sid') = ?", sid);
			
			var cSql = new StringBuilder("select count(*) from col_debug").append(c);
			var qSql = new StringBuilder("select doc from col_debug").append(c).append(" order by json_extract(doc, '$.b') desc");
			returnList = queryCollectionPage(t, page, cSql, qSql, c.getParaList());
		}
		return returnList;
	}
	
	
	
	
	
	
	
	

}
