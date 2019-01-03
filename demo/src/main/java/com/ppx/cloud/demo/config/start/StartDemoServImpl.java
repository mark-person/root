package com.ppx.cloud.demo.config.start;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

@Service
public class StartDemoServImpl extends MyDaoSupport implements StartDemoServ {
	
	

	public Map<String, Object> getConfig() {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		String sql = "select prop_name, prop_value, prop_value_type from base_properties";
		List<Map<String, Object>> propList = getJdbcTemplate().queryForList(sql);
		for (Map<String, Object> map : propList) {
			String propName = (String)map.get("prop_name");
			String propValue = (String)map.get("prop_value");
			int prop_value_type = (Integer)map.get("prop_value_type");
			if (prop_value_type == 0) {
				returnMap.put(propName, propValue);
			}
			else if (prop_value_type == 1) {
				returnMap.put(propName, Integer.parseInt(propValue));
			}
			else if (prop_value_type == 2) {
				returnMap.put(propName, Double.parseDouble(propValue));
			}
		}
		
		
		return returnMap;
	}

}
