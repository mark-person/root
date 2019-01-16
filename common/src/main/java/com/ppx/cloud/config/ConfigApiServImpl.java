package com.ppx.cloud.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.util.ApplicationUtils;

@Service
public class ConfigApiServImpl extends MyDaoSupport implements ConfigApiServ {

	public List<Config> listConfig(String artifactId) {
		String sql = "select * from config_value where artifact_id = ?";
		List<Config> list = getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(Config.class), artifactId);
		return list;
	}
	
    // 请求
    public Map<String, Object> callSync(String configName, String configValue) {
    	Map<String, Object> returnMap = new HashMap<String, Object>();
    	String errmsg = "";
    	
    	// 请求其它服务
    	String uri = "/auto/configApi/sync?configParam=" + configName + "&configValue=" + configValue;
    	
    	String currentServiceId = ApplicationUtils.getServiceId();
    	// 更新其它服务
		String otherSql = "select service_id from config_service where service_status = 1 and service_id != ? and " + 
				" artifact_id = (select artifact_id from config_value where config_name = ?)";
		List<String> ohterServiceIdList = getJdbcTemplate().queryForList(otherSql, String.class,  currentServiceId ,configName);
		
		RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("", new HttpHeaders());
        
        int successNum = 0;
        
        // 清除执行结果
        String deleteSql = "delete from config_exec_result where config_name = ?";
        getJdbcTemplate().update(deleteSql, configName);
        
        String insertSql = "insert into config_exec_result(config_name, servcie_id, exec_result, exec_desc) values(?, ?, ?, ?)";
		for (String serviceId : ohterServiceIdList) {
			try {
				String url = "http://" + serviceId + "/" + uri;
		        ResponseEntity<Map> resultMap = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
				int errcode = (Integer)resultMap.getBody().get("errcode");
				if (errcode == 0) {
					successNum++;
					// 更新成功记录
					 getJdbcTemplate().update(insertSql, configName, serviceId, 1, null);
				}
				else {
					errmsg += "serviceId:" + serviceId + "api异常:" +  resultMap.getBody().get("errmsg") + ";";
					getJdbcTemplate().update(insertSql, configName, serviceId, 0, resultMap.getBody().get("errmsg"));
				}
			} catch (Exception e) {
				errmsg += "serviceId:" + serviceId + "异常:" +  e.getMessage() + ";";
				getJdbcTemplate().update(insertSql, configName, serviceId, 0, e.getMessage());
			}
		}
		
		returnMap.put("successNum", successNum);
		returnMap.put("totalNum", ohterServiceIdList.size());
		if (Strings.isNotEmpty(errmsg)) {
			returnMap.put("errcode", 4001);
			returnMap.put("errmsg", errmsg);
		}
		else {
			returnMap.put("errcode", 0);
			returnMap.put("errmsg", errmsg);
		}
    	
    	return returnMap;
    }
    
    
//    public void updateConfigValue(String configParam) {
//    	String sql = "update config_value set config_status = 1 where config_param = ? and service_id = ?";
//    	getJdbcTemplate().update(sql, configParam, ApplicationUtils.getServiceId());
//    }
    
    
}
