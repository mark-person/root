package com.ppx.cloud.common.exception;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.util.NestedServletException;

import com.ppx.cloud.common.exception.custom.LoginException;
import com.ppx.cloud.common.exception.custom.NotFoundException;
import com.ppx.cloud.common.exception.custom.PermissionParamsException;
import com.ppx.cloud.common.exception.custom.PermissionResubmitException;
import com.ppx.cloud.common.exception.custom.PermissionUrlException;



/**
 * 分类异常，不是所有的异常都要全部信息
 *  9:紧急异常，需要紧急处理，打印
 *  0:不需要修改后端代码，不打印
 *  1:java异常，需要修改，打印
 *  2:jdbc部分异常，需要修改，打印
 *  3:模板错误
 *  4:mongodb异常
 *  -1:未知异常，打印
 * @author dengxz
 * @date 2017年4月1日
 */

public class ErrorCode {
    private static Map<Class<?>, Integer> errorMap = new HashMap<Class<?>, Integer>();
   
    // 防止攻击打印大量错误日志
    public final static int IGNORE_ERROR = 0;
    // java.lang异常
    private final static int JAVA_LANG_ERROR = 1;
    // jdbc异常
    private final static int JDBC_ERROR = 2;
    // mongodb异常
    private final static int MONGO_ERROR = 3;
    // thymeleaf异常
    private final static int THYMELEAF_ERROR = 4;
    // 紧急异常，需要紧急处理
    private final static int CRITICAL_ERROR = 9;
    // 未知异常
    private final static int UNKNOWN_ERROR = -1;
    
    static {
        errorMap.put(LoginException.class, IGNORE_ERROR);
        
        // 404
        errorMap.put(NotFoundException.class, IGNORE_ERROR);
        // 找到uri，参数缺少    
        errorMap.put(MissingServletRequestParameterException.class, IGNORE_ERROR);
        // 找到uri和参数，参数类型不匹配
        errorMap.put(TypeMismatchException.class, IGNORE_ERROR);
        // 单条记录查询结果为空,防攻击时打印大量错误日志
        // Spring中使用JdbcTemplate的queryForObject方法，当查不到数据时会抛出如下异常：EmptyResultDataAccessException
        errorMap.put(EmptyResultDataAccessException.class, IGNORE_ERROR);
        // 重复提交异常
        errorMap.put(PermissionResubmitException.class, IGNORE_ERROR);
        // 权限异常,超权url时报的异常
        errorMap.put(PermissionUrlException.class, IGNORE_ERROR);
        // 权限异常,参数超权的报的异常，如修改不是自己数据时报的异常（开发人员需要关注）
        errorMap.put(PermissionParamsException.class, IGNORE_ERROR);
        // 需要接收json参数 如：@RequestBody Test test
        errorMap.put(HttpMessageNotReadableException.class, IGNORE_ERROR);
        // URI不合法，太长等等
        errorMap.put(PermissionUrlException.class, IGNORE_ERROR);
        
        
        // 数据库连接不上(如没有启动，或死掉)
        errorMap.put(CannotGetJdbcConnectionException.class, CRITICAL_ERROR);
        // 数据库连接池已满
        errorMap.put(CannotCreateTransactionException.class, CRITICAL_ERROR);
    }
    
	public static ErrorBean getErroCode(Throwable e) {
	    if (errorMap.containsKey(e.getClass())) {
	        return new ErrorBean(errorMap.get(e.getClass()), e.getMessage());
	    }
		
		if (e instanceof BindException) {
			String msg = e.getClass().getSimpleName();
			// 参数类型错误或 查询每页记录超过最大数时
			int i = e.getMessage().indexOf("PermissionMaxPageSizeException");
			msg = (i >= 0) ? "maxPageSize forbidden" : msg;
			return new ErrorBean(0, msg);
		}
		else if (e instanceof NestedServletException) {
			String msg = e.getMessage();
			// 判断是否内存溢出
			// e.getMessage().indexOf("java.lang.OutOfMemoryError");
			
			// 判断是否找不到类
			// e.getMessage().indexOf("java.lang.NoClassDefFoundError");
			if (msg.indexOf("org.thymeleaf.exceptions.TemplateProcessingException") >= 0) {
				return new ErrorBean(THYMELEAF_ERROR, "TemplateProcessingException");
			}
			return new ErrorBean(CRITICAL_ERROR, msg);
		}
		
		if ("java.lang".equals(e.getClass().getPackage().getName())) {
			// java.lang异常 NullPointerException等
			return new ErrorBean(JAVA_LANG_ERROR, e.getClass().getSimpleName());
		}
		else if ("org.springframework.jdbc".equals(e.getClass().getPackage().getName())) {
			// jdbc异常，表不存，sql语法错误
			return new ErrorBean(JDBC_ERROR, e.getClass().getSimpleName());
		}
		else if ("org.springframework.dao".equals(e.getClass().getPackage().getName())) {
			// 必须栏插入空值，重复数据，Integer field返回int
			return new ErrorBean(JDBC_ERROR, e.getClass().getSimpleName());
		}
		else if ("org.springframework.data.mongodb".equals(e.getClass().getPackage().getName())) {
			// 必须栏插入空值，重复数据，Integer field返回int
			return new ErrorBean(MONGO_ERROR, e.getClass().getSimpleName());
		}
		
		return new ErrorBean(UNKNOWN_ERROR, e.getClass().getSimpleName());
	}
}
