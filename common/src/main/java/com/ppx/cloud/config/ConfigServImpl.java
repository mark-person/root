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
public class ConfigServImpl extends MyDaoSupport implements ConfigServ {

	public List<Config> listConfig() {
		String sql = "select * from config_value";
		List<Config> list = getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(Config.class));
		return list;
	}
	
    // 请求
    public Map<String, Object> sync(String configParam, String configValue) {
    	
    	// 请求其它服务
    	String uri = "/auto/config/sync?configParam=" + configParam + "&configValue=" + configValue;
    	
    	String sql = "select service_id from config_value where config_param = ?" + 
    			" and exists (select 1 from config_service where service_id = config_value.service_id and service_status = 1)";
    	List<String> serviceIdList = getJdbcTemplate().queryForList(sql, String.class, configParam);
    	
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
