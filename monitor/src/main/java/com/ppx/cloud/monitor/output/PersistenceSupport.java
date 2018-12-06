package com.ppx.cloud.monitor.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.StringUtils;

import com.mysql.cj.xdevapi.Row;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.page.Page;

public class PersistenceSupport {
	
	protected static final String COL_START = "col_start";
	
	protected static final String COL_GATHER = "col_gather";
	
	protected static final String COL_ACCESS = "col_access";
	
	protected static final String COL_ERROR = "col_error";

	protected static final String COL_ERROR_DETAIL = "col_error_detail";

	protected static final String COL_DEBUG = "col_debug";
	
	protected static final String TABLE_CONF = "conf";

	protected static final String TABLE_SERVICE = "service";

	protected static final String TABLE_STAT_URI = "stat_uri";

	protected static final String TABLE_STAT_SQL = "stat_sql";

	protected static final String TABLE_STAT_RESPONSE = "stat_response";

	protected static final String TABLE_STAT_WARNING = "stat_warning";

	
	
	
	
	
	protected List<Row> queryPage(LogTemplate t, Page page, StringBuilder cSql, StringBuilder qSql,
			List<Object> paraList) {
		paraList = paraList == null ? new ArrayList<Object>() : paraList;
		int totalRows = (int)t.sql(cSql.toString(), paraList).count();
		page.setTotalRows(totalRows);
		if (totalRows == 0) {
			return new ArrayList<Row>();
		}

		// order by
		if (!StringUtils.isEmpty(page.getOrderName())) {
			qSql.append(" order by ").append(page.getOrderName()).append(" ").append(page.getOrderType());
			// 数据库使用快速排序，防止分页的数据出现相同情况
			if (!Objects.equals(page.getOrderName(), page.getUnique()) && !StringUtils.isEmpty(page.getUnique())) {
				qSql.append(",").append(page.getUnique());
			}
		}

		qSql.append(" limit ?, ?");
		paraList.add((page.getPageNumber() - 1) * page.getPageSize());
		paraList.add(page.getPageSize());
		
		List<Row> r = t.sql(qSql.toString(), paraList).fetchAll();
		return r;
	}
}
