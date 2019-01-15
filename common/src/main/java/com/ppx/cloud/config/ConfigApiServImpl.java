package com.ppx.cloud.config;

import java.util.List;
import java.util.Map;

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
    	
    	// 请求其它服务
    	String uri = "/auto/configApi/sync?configParam=" + configName + "&configValue=" + configValue;
    	
    	String currentServiceId = ApplicationUtils.getServiceId();
    	// 更新其它服务
		String otherSql = "select service_id from config_service where service_status = 1 and service_id != ? and " + 
				" artifact_id = (select artifact_id from config_value where config_name = ?)";
		List<String> ohterServiceIdList = getJdbcTemplate().queryForList(otherSql, String.class,  currentServiceId ,configName);
		for (String serviceId : ohterServiceIdList) {
			
		}
    	String sql = "";
    	List<String> serviceIdList = getJdbcTemplate().queryForList(sql, String.class, configName);
    	
    	RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("", new HttpHeaders());
        String url = "";
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("99999999:" + result.getBody());
        
    	for (String serviceId : serviceIdList) {
			// String url = "http://" + serviceId + uri;
			
			
		}
    	
    	
    	
    	
    	return null;
    }
    
    
    public void updateConfigValue(String configParam) {
    	String sql = "update config_value set config_status = 1 where config_param = ? and service_id = ?";
    	getJdbcTemplate().update(sql, configParam, ApplicationUtils.getServiceId());
    }
    
    
}
