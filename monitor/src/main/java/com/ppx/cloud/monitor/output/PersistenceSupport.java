package com.ppx.cloud.monitor.output;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.mysql.cj.xdevapi.Column;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.JsonString;
import com.mysql.cj.xdevapi.Row;
import com.mysql.cj.xdevapi.SqlResult;
import com.mysql.cj.xdevapi.Type;
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
		int totalRows = t.sql(cSql.toString(), paraList).fetchOne().getInt(0);
		
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
	
	
	
	
	
	protected List<Map<String, Object>> queryTablePage(LogTemplate t, Page page, StringBuilder cSql, StringBuilder qSql,
			List<Object> paraList) {
		paraList = paraList == null ? new ArrayList<Object>() : paraList;
		int totalRows = t.sql(cSql.toString(), paraList).fetchOne().getInt(0);
		
		page.setTotalRows(totalRows);
		if (totalRows == 0) {
			return new ArrayList<Map<String, Object>>();
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
		
		SqlResult sr = t.sql(qSql.toString(), paraList);
		
		List<Row> list = sr.fetchAll();
		
		
		
		
		
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		List<Column> colList = sr.getColumns();
		for (Column col : colList) {
			System.out.println("out:" + col.getColumnName() + "||" + col.getType());
		}
		
		for (Row row : list) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (Column col : colList) {
				if (col.getType() == Type.INT) {
					map.put(col.getColumnName(), row.getInt(col.getColumnName()));
				}
				else if (col.getType() == Type.TIMESTAMP) {
					map.put(col.getColumnName(), row.getTimestamp(col.getColumnName()));
				}
				else if (col.getType() == Type.JSON) {
					var r = row.getString(col.getColumnName());
					if (!StringUtils.isEmpty(r) && r.startsWith("[")) {
						map.put(col.getColumnName(), r);
					}
					else {
						var v = row.getDbDoc(col.getColumnName());
						Map<String, Object> mapmap = new LinkedHashMap<String, Object>();
						((DbDoc)v).forEach((kk, vv) -> {
							if (v instanceof JsonString) {
								mapmap.put(kk, ((JsonString)vv).getString());
							}
							else {
								mapmap.put(kk, vv.toString());
							}
						});
						map.put(col.getColumnName(), mapmap);
					}
				}
			}
			
			returnList.add(map);
		}
		return returnList;
	}
	
	
	
}