package com.ppx.cloud.monitor.output;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.mysql.cj.xdevapi.Column;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DbDocImpl;
import com.mysql.cj.xdevapi.JsonNumber;
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
		
		for (Row row : list) {
			returnList.add(getRowMap(row, colList));
		}
		return returnList;
	}
	
	private Map<String, Object> getRowMap(Row row, List<Column> colList) {
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
				// DbDoc不支持[]的json
				if (!StringUtils.isEmpty(r) && r.startsWith("[")) {
					map.put(col.getColumnName(), r);
				}
				else {
					DbDoc doc = row.getDbDoc(col.getColumnName());
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					doc.forEach((k, v) -> {
						if (v instanceof JsonString) {
							m.put(k, ((JsonString)v).getString());
						}
						else if (v instanceof JsonNumber) {
							m.put(k, ((JsonNumber)v).getBigDecimal());
						}
						else {
							m.put(k, v.toString());
						}
					});
					map.put(col.getColumnName(), m);
				}
			}
			else {
				map.put(col.getColumnName(), row.getString(col.getColumnName()));
			}
		}
		return map;
	}
	
	protected List<Map<String, Object>> queryCollectionPage(LogTemplate t, Page page, StringBuilder cSql, StringBuilder qSql,
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
	
		for (Row row : list) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			row.getDbDoc("doc").forEach((k, v) -> {
				if (v instanceof DbDocImpl) {
					Map<String, Object> mapmap = new LinkedHashMap<String, Object>();
					((DbDoc)v).forEach((docK, docV) -> {
						if (v instanceof JsonString) {
							mapmap.put(docK, ((JsonString)docV).getString());
						}
						else {
							mapmap.put(docK, docV.toString());
						}
					});
					map.put(k, mapmap);
				}
				else if (v instanceof JsonString) {
					map.put(k, ((JsonString)v).getString());
				}
				else {
					map.put(k, v.toString());
				}
			});
			returnList.add(map);
		}
		return returnList;
	}
	
	
}
