package com.ppx.cloud.monitor.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mysql.cj.xdevapi.DbDocImpl;
import com.mysql.cj.xdevapi.JsonString;
import com.mysql.cj.xdevapi.Row;
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
			var qSql = new StringBuilder("select * from service order by service_prio desc");
			List<Row> list = queryPage(t, page, cSql, qSql, null);
			for (Row row : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("serviceId", row.getString("service_id"));
				map.put("serviceDisplay", row.getString("service_display"));
				row.getDbDoc("service_info").forEach((k, v) -> {
					if (v instanceof JsonString) {
						map.put(k, ((JsonString)v).getString());
					}
					else {
						map.put(k, v.toString());
					}
				});
				returnList.add(map);
			}
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
			List<Row> list = queryPage(t, page, cSql, qSql, c.getParaList());
			for (Row row : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				row.getDbDoc("doc").forEach((k, v) -> {
					if (v instanceof JsonString) {
						map.put(k, ((JsonString)v).getString());
					}
					else {
						map.put(k, v.toString());
					}
				});
				returnList.add(map);
			}
		}
		return returnList;
	}

	
	public List<Map<String, Object>> listAccess(Page page, String date, String sid) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("json_extract(doc, '$.sid') = ?", sid);
			
			var cSql = new StringBuilder("select count(*) from col_access" + date).append(c);
			var qSql = new StringBuilder("select doc from col_access" + date).append(c).append(" order by json_extract(doc, '$.b') desc");
			List<Row> list = queryPage(t, page, cSql, qSql, c.getParaList());
			for (Row row : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				row.getDbDoc("doc").forEach((k, v) -> {
					if (v instanceof JsonString) {
						map.put(k, ((JsonString)v).getString());
					}
					else {
						map.put(k, v.toString());
					}
				});
				returnList.add(map);
			}
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
			List<Row> list = queryPage(t, page, cSql, qSql, c.getParaList());
			for (Row row : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				row.getDbDoc("doc").forEach((k, v) -> {
					if (v instanceof JsonString) {
						map.put(k, ((JsonString)v).getString());
					}
					else {
						map.put(k, v.toString());
					}
				});
				returnList.add(map);
			}
		}
		return returnList;
	}
	
	public List<Map<String, Object>> listGather(Page page, String sid) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			
			MyCriteria c = new MyCriteria("where");
			c.addAnd("json_extract(doc, '$.sid') = ?", sid);
			
			var cSql = new StringBuilder("select count(*) from col_gather").append(c);
			var qSql = new StringBuilder("select doc from col_gather").append(c).append(" order by json_extract(doc, '$.created') desc");
			List<Row> list = queryPage(t, page, cSql, qSql, c.getParaList());
			for (Row row : list) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				row.getDbDoc("doc").forEach((k, v) -> {
					if (v instanceof DbDocImpl) {
						Map<String, Object> mapmap = new LinkedHashMap<String, Object>();
						((DbDocImpl)v).forEach((kk, vv) -> {
							if (v instanceof JsonString) {
								mapmap.put(kk, ((JsonString)vv).getString());
							}
							else {
								mapmap.put(kk, vv.toString());
							}
						});
						map.put(k, mapmap);
					}
					else if (v instanceof JsonString) {
						map.put(k, ((JsonString)v).getString());
					}
					else {
						map.put(k, v.toString());
					}
				});
				returnList.add(map);
			}
		}
		return returnList;
	}
	

}
