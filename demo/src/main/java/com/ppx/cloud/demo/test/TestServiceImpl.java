package com.ppx.cloud.demo.test;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;

@Service
public class TestServiceImpl extends MyDaoSupport {

	public List<Test> list(Page page, Test pojo) {

		// 默认排序，后面加上需要从页面传过来的排序的，防止SQL注入
		// page.addDefaultOrderName("test_id").addPermitOrderName("test_price").addUnique("test_id");

		// 分开两条sql，mysql在count还会执行子查询, 总数返回0将不执行下一句
		var c = createCriteria("where").addAnd("t.test_name like ?", "%", pojo.getTestName(), "%");
		 
		page.addDefaultOrderName("t.test_id").addPermitOrderName("t.test_name").addUnique("t.test_id");
		
		
		var cSql = new StringBuilder("select count(*) from test t").append(c);
		var qSql = new StringBuilder("select * from test t").append(c);
		
		List<Test> list = queryPage(Test.class, page, cSql, qSql, c.getParaList());
		return list;
	}
	
	public int insert(Test pojo) {
        // 后面带不允许重名的字段（该字段需要建索引）
        return insertEntity(pojo, "test_name");
    }
	
	public Test get(Integer id) {
		Test pojo = getJdbcTemplate().queryForObject("select * from test where test_id = ?",
                BeanPropertyRowMapper.newInstance(Test.class), id);     
        return pojo;
    }
    
    public int update(Test bean) {
        // 后面带不允许重名的字段（该字段需要建索引）
        return updateEntity(bean, "test_name");
    }
    
    public int delete(Integer id) {
        return getJdbcTemplate().update("delete from test where test_id = ?", id);
    }

}
