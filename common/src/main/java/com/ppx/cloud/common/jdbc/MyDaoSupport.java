package com.ppx.cloud.common.jdbc;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.StringUtils;

import com.ppx.cloud.common.exception.custom.JdbcUpdateNoPkException;
import com.ppx.cloud.common.jdbc.annotation.Column;
import com.ppx.cloud.common.jdbc.annotation.Id;
import com.ppx.cloud.common.jdbc.annotation.Table;
import com.ppx.cloud.common.page.LimitRecord;
import com.ppx.cloud.common.page.Page;

/**
 * insert update queryPage等 操作实体对象
 * 
 * @author mark
 * @date 2018年10月28日
 */
public class MyDaoSupport extends JdbcDaoSupport {

	@Autowired
	protected void setMyJdbcTemplate(JdbcTemplate jdbcTemplate) {
		super.setJdbcTemplate(jdbcTemplate);
	}

	/**
	 * 插入一个对象进数据库
	 * 
	 * @param bean 实体对象
	 * @return 返回主键值，如何传入主键，则返回"1"
	 */
	protected int insertEntity(Object bean) {
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor[] propertyDescriptor = bw.getPropertyDescriptors();

		List<String> colList = new ArrayList<String>();
		List<String> questionList = new ArrayList<String>();
		List<Object> valList = new ArrayList<Object>();

		for (PropertyDescriptor d : propertyDescriptor) {
			if ("class".equals(d.getName()) || d.getWriteMethod() == null) {
				continue;
			}

			Column col = getColumnAnnotation(bw, d.getName());
			if (col != null && (col.readonly() || !col.insertable())) {
				continue;
			}

			Object val = bw.getPropertyValue(d.getName());
			if (val == null) {
				continue;
			}

			colList.add(underscoreName(d.getName()).toLowerCase());
			questionList.add("?");
			valList.add(val);
		}
		StringBuilder b = new StringBuilder("insert into ").append(getTableName(bean)).append("(")
				.append(StringUtils.collectionToCommaDelimitedString(colList)).append(")values(")
				.append(StringUtils.collectionToCommaDelimitedString(questionList)).append(")");
		return getJdbcTemplate().update(b.toString(), valList.toArray());
	}

	/**
	 * 插入一个对象进数据库
	 * 
	 * @param bean           实体对象
	 * @param uniqueColNames 对应数据库要有唯一索引
	 * @return 返回主键值， 如何传入主键，则返回"1",已经存在uniqueColNames则返回"0"
	 */
	protected int insertEntity(Object bean, String... uniqueColNames) {

		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor[] propertyDescriptor = bw.getPropertyDescriptors();

		List<String> colList = new ArrayList<String>();
		List<String> questionList = new ArrayList<String>();
		List<Object> valList = new ArrayList<Object>();

		List<String> uniqueColNameList = new ArrayList<String>(2);
		List<Object> uniqueObjList = new ArrayList<Object>(2);

		for (PropertyDescriptor d : propertyDescriptor) {
			if ("class".equals(d.getName()) || d.getWriteMethod() == null) {
				continue;
			}

			Column col = getColumnAnnotation(bw, d.getName());
			if (col != null && (col.readonly() || !col.insertable())) {
				continue;
			}

			Object val = bw.getPropertyValue(d.getName());
			if (val == null) {
				continue;
			}

			String colName = underscoreName(d.getName()).toLowerCase();
			colList.add(colName);
			questionList.add("?");
			valList.add(val);

			for (String uniqueColName : uniqueColNames) {
				if (colName.equals(uniqueColName)) {
					uniqueColNameList.add(colName + "=?");
					uniqueObjList.add(val);
				}
			}
		}

		StringBuilder b = new StringBuilder("insert into ").append(getTableName(bean)).append("(")
				.append(StringUtils.collectionToCommaDelimitedString(colList)).append(")").append(" select ")
				.append(StringUtils.collectionToCommaDelimitedString(questionList))
				.append(" from dual where not exists (select 1 from ").append(getTableName(bean)).append(" where ")
				.append(StringUtils.collectionToDelimitedString(uniqueColNameList, " and ")).append(")");
		valList.addAll(uniqueObjList);
		return getJdbcTemplate().update(b.toString(), valList.toArray());
	}

	/**
	 * 更新实体bean
	 * 
	 * @param bean 实体对象
	 * @return 更新成功返回1,失败返回0
	 */
	protected int updateEntity(Object bean) {
		return updateNoUnique(bean, null);
	}

	protected int updateEntity(Object bean, LimitRecord limitRecord) {
		return updateNoUnique(bean, limitRecord);
	}

	private int updateNoUnique(Object bean, LimitRecord limitRecord) {
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor[] propertyDescriptor = bw.getPropertyDescriptors();

		List<String> colList = new ArrayList<String>();
		List<Object> valList = new ArrayList<Object>();
		String idSql = "";
		Object idObj = null;

		for (PropertyDescriptor d : propertyDescriptor) {
			if ("class".equals(d.getName()) || d.getWriteMethod() == null) {
				continue;
			}

			Object val = bw.getPropertyValue(d.getDisplayName());
			if (val == null) {
				continue;
			}

			Column col = getColumnAnnotation(bw, d.getName());
			if (col != null && (col.readonly() || !col.updatable())) {
				continue;
			}

			String colName = underscoreName(d.getName()).toLowerCase();
			if (isIdAnnotation(bw, d.getName())) {
				idSql = colName + "=?";
				idObj = val;
			} else {
				colList.add(colName + "=?");
				valList.add(val);
			}
		}

		if (idObj == null) {
			throw new JdbcUpdateNoPkException();
		}

		StringBuilder b = new StringBuilder("update ").append(getTableName(bean)).append(" set ")
				.append(StringUtils.collectionToCommaDelimitedString(colList)).append(" where ").append(idSql);
		if (limitRecord != null) {
			b.append(" and ").append(limitRecord.getColName()).append("=?");
		}
		valList.add(idObj);
		if (limitRecord != null) {
			valList.add(limitRecord.getColValue());
		}

		return getJdbcTemplate().update(b.toString(), valList.toArray());
	}

	/**
	 * 更新实体bean
	 * 
	 * @param bean           实体对象
	 * @param uniqueColNames 对应数据库要有唯一索引
	 * @return 更新成功返回1,失败返回0
	 */
	protected int updateEntity(Object bean, String... uniqueColNames) {
		return updateUnique(bean, null, uniqueColNames);
	}

	protected int updateEntity(Object bean, LimitRecord limitRecord, String... uniqueColNames) {
		return updateUnique(bean, limitRecord, uniqueColNames);
	}

	private int updateUnique(Object bean, LimitRecord limitRecord, String... uniqueColNames) {
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor[] propertyDescriptor = bw.getPropertyDescriptors();

		List<String> colList = new ArrayList<String>();
		List<Object> valList = new ArrayList<Object>();
		String idColName = "";
		Object idObj = null;

		List<String> uniqueColNameList = new ArrayList<String>(2);
		List<Object> uniqueObjList = new ArrayList<Object>(2);

		for (PropertyDescriptor d : propertyDescriptor) {
			if ("class".equals(d.getName()) || d.getWriteMethod() == null) {
				continue;
			}

			Object val = bw.getPropertyValue(d.getName());
			if (val == null) {
				continue;
			}

			Column col = getColumnAnnotation(bw, d.getName());
			if (col != null && (col.readonly() || !col.updatable())) {
				continue;
			}

			String colName = underscoreName(d.getName()).toLowerCase();
			if (isIdAnnotation(bw, d.getName())) {
				idColName = colName;
				idObj = val;
			} else {
				colList.add(colName + "=?");
				valList.add(val);
			}
			for (String uniqueColName : uniqueColNames) {
				if (colName.equals(uniqueColName)) {
					uniqueColNameList.add(colName + "=?");
					uniqueObjList.add(val);
				}
			}
		}

		if (idObj == null) {
			throw new JdbcUpdateNoPkException();
		}

		String tableName = getTableName(bean);

		StringBuilder b = new StringBuilder("update ").append(tableName).append(" join (select ? ").append(idColName)
				.append(" from dual where not exists (select 1 from ").append(tableName).append(" where ")
				.append(StringUtils.collectionToDelimitedString(uniqueColNameList, " and ")).append(" and ")
				.append(idColName).append("!=?)) t").append(" set ")
				.append(StringUtils.collectionToCommaDelimitedString(colList)).append(" where ").append(tableName)
				.append(".").append(idColName).append("=t.").append(idColName);
		if (limitRecord != null) {
			b.append(" and ").append(tableName).append(".").append(limitRecord.getColName()).append("=?");
		}

		List<Object> paraList = new ArrayList<Object>();
		paraList.add(idObj);
		paraList.addAll(uniqueObjList);
		paraList.add(idObj);
		paraList.addAll(valList);
		if (limitRecord != null) {
			paraList.add(limitRecord.getColValue());
		}

		return getJdbcTemplate().update(b.toString(), paraList.toArray());
	}

	protected int getLastInsertId() {
		return getJdbcTemplate().queryForObject("select LAST_INSERT_ID();", Integer.class);
	}

	/**
	 * 分页查询对象列表
	 * 
	 * @param c        对象类型
	 * @param page     分页bean
	 * @param cSql     总数sql
	 * @param qSql     查询sql
	 * @param paraList 查询参数
	 * @return 返回对List<对象>
	 */
	protected <T> List<T> queryPage(Class<T> c, Page page, StringBuilder cSql, StringBuilder qSql,
			List<Object> paraList) {
		paraList = paraList == null ? new ArrayList<Object>() : paraList;
		int totalRows = super.getJdbcTemplate().queryForObject(cSql.toString(), Integer.class, paraList.toArray());
		page.setTotalRows(totalRows);
		if (totalRows == 0) {
			return new ArrayList<T>();
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
		List<T> r = (List<T>) super.getJdbcTemplate().query(qSql.toString(), BeanPropertyRowMapper.newInstance(c),
				paraList.toArray());
		return r;
	}

	protected <T> List<T> queryPage(Class<T> c, Page page, StringBuilder cSql, StringBuilder qSql) {
		return queryPage(c, page, cSql, qSql, null);
	}

	/**
	 * 取得查询条件对象
	 * 
	 * @param prev 前缀sql(一般是where或and)
	 * @return
	 */
	protected MyCriteria createCriteria(String prev) {
		return new MyCriteria(prev);
	}

	// >>>>>>>>>>>>>>>>>>>>private>>>>>>>>>>>>>>>>>>>>>

	/**
	 * 取得表名,如果有Table annotation则在Table annotation里取，否则读bean name转下划线方式
	 * 
	 * @param bean 实体对象
	 * @return 表名
	 */
	private String getTableName(Object bean) {
		Table table = bean.getClass().getAnnotation(Table.class);
		return (table == null) ? underscoreName(bean.getClass().getSimpleName()) : table.name();
	}

	/**
	 * Convert a name in camelCase to an underscored name in lower case. Any upper
	 * case letters are converted to lower case with a preceding underscore.
	 * 
	 * @param name the original name
	 * @return the converted name
	 */
	private String underscoreName(String name) {
		if (!StringUtils.hasLength(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder(name.substring(0, 1).toLowerCase());
		for (int i = 1; i < name.length(); i++) {
			String s = name.substring(i, i + 1);
			String slc = s.toLowerCase();
			if (!s.equals(slc)) {
				result.append("_").append(slc);
			} else {
				result.append(s);
			}
		}
		return result.toString();
	}

	private boolean isIdAnnotation(BeanWrapper bw, String fieldName) {
		return bw.getPropertyTypeDescriptor(fieldName).getAnnotation(Id.class) == null ? false : true;
	}

	private Column getColumnAnnotation(BeanWrapper bw, String fieldName) {
		return bw.getPropertyTypeDescriptor(fieldName).getAnnotation(Column.class);
	}

}
