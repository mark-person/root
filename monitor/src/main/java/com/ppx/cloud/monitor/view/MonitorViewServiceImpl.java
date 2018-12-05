package com.ppx.cloud.monitor.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mysql.cj.xdevapi.JsonString;
import com.mysql.cj.xdevapi.Row;
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
				row.getDbDoc("serviceInfo").forEach((k, v) -> {
					if (v instanceof JsonString) {
						var s = v.toString().replaceAll("(^\"|\"$)", "");
						map.put(k, s);
					}
					else {
						map.put(k, v.toString());
					}
				});
				returnList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnList;
	}

	

}
