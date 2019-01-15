package com.ppx.cloud.config.set;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.config.Config;
import com.ppx.cloud.config.ConfigExec;
import com.ppx.cloud.config.ConfigUtils;

@Service
public class ConfigServImpl extends MyDaoSupport {
	
	public List<Config> list(Page page, Config config) {

		var cSql = new StringBuilder("select count(*) from config_value");
		var qSql = new StringBuilder("select * from config_value");
		
		List<Config> list = queryPage(Config.class, page, cSql, qSql);
		return list;
	}
	
	@Transactional
	public Map<String, Object> update(String configName, String configValue) {
		try {
			new ObjectMapper().readValue(configValue, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			return  ReturnMap.of(4001, "configValue不是json格式:" + e.getMessage());
		}
		
		
		// 更新本地
		ConfigExec configExec = ConfigUtils.getConfigExec(configName);
		String r = configExec.run(configValue);
		if (Strings.isNotEmpty(r)) {
			return ReturnMap.of(4001, r);
		}
		
		String updateSql = "update config_value set config_value = ? where config_name = ?";
		getJdbcTemplate().update(updateSql, configValue, configName);
		
		
		
		
		return ReturnMap.of("3个服务全部刷新成功");
	}
	
   
    
    
}
