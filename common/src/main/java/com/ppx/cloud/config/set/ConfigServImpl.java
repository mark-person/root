package com.ppx.cloud.config.set;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.config.ConfigExec;
import com.ppx.cloud.config.ConfigUtils;
import com.ppx.cloud.config.api.ConfigApiServ;
import com.ppx.cloud.config.pojo.Config;
import com.ppx.cloud.config.pojo.ConfigExecResult;

@Service
public class ConfigServImpl extends MyDaoSupport {
	
	
	@Autowired
	private ConfigApiServ configApiServ;
	
	public List<Config> list(Page page, Config pojo) {

		var cSql = new StringBuilder("select count(*) from config_value");
		var qSql = new StringBuilder("select * from config_value order by modified desc");
		
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
		
		String updateSql = "update config_value set config_value = ?, modified = now() where config_name = ?";
		getJdbcTemplate().update(updateSql, configValue, configName);
		
		// 清除执行结果
        String deleteSql = "delete from config_exec_result where config_name = ?";
        getJdbcTemplate().update(deleteSql, configName);
		String insertSql = "insert into config_exec_result(config_name, service_id, exec_result) values(?, ?, ?)";
		// 更新成功记录
		getJdbcTemplate().update(insertSql, configName, ApplicationUtils.getServiceId(), 1);
		
		Map<String, Object> syncMap = configApiServ.callSync(configName, configValue);
		int totalNum = (Integer)syncMap.get("totalNum");
		int successNum = (Integer)syncMap.get("successNum");
		if (totalNum == 0) {
			return ReturnMap.of("本服务刷新成功");
		} else if (totalNum != successNum) {
			int errcode = (Integer)syncMap.get("errcode");
			String errmsg = (String)syncMap.get("errmsg");
			return ReturnMap.of(errcode, "共" + (totalNum + 1) + "个服务, " + (totalNum - successNum) + "刷新失败, 失败原因:" + errmsg);
		}
		return ReturnMap.of((totalNum + 1) + "个服务全部刷新成功");
	}

	public List<Config> listConfigExecResult(Page page, ConfigExecResult pojo) {

		var cSql = new StringBuilder("select count(*) from config_value");
		var qSql = new StringBuilder("select * from config_value order by modified desc");
		
		List<Config> list = queryPage(Config.class, page, cSql, qSql);
		return list;
	}
    
    
}
