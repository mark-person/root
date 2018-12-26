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

import com.ppx.cloud.common.exception.custom.IllegalRequestException;
import com.ppx.cloud.common.exception.custom.LoginException;
import com.ppx.cloud.common.exception.security.PermissionParamsException;
import com.ppx.cloud.common.exception.security.PermissionResubmitException;
import com.ppx.cloud.common.exception.security.PermissionUriException;


// 0:成功 -1:系统忙(500错误) 4000:存在 400x业务逻辑；403?:权限；404?: 4040 no found 参数 uri长度、不合法等

/**
 * 分类异常，不是所有的异常都要全部信息
 * 0:不需要修改后端代码，不打印
 * 9:紧急异常，需要紧急处理，打印
 * 1:java.lang, jdbc, thymeleaf等异常，  局部异常
 * @author mark
 * @date 2018年12月26日
 */
public class ErrorUtils {
	
	
	// // 0:成功 -1:系统忙(500错误) 4000:存在 400x其它业务逻辑；403?:权限；404?: 4040 no found 参数 uri长度、不合法等
    private static Map<Class<?>, Integer> errorMap = new HashMap<Class<?>, Integer>();
   
    // 防止攻击打印大量错误日志，只记录一条错误信息，不记录详情
    // 权限异常， 参数必填，参数类型错误，
    public final static int ERROR_LEVEL_IGNORE = 0;
    // 紧急异常，需要紧急处理，影响全局
    private final static int ERROR_LEVEL_CRITICAL = 9;
    // java.lang, jdbc, thymeleaf等异常，  局部异常
    private final static int ERROR_LEVEL_NORMAL = 1;
    
    
    // 未知级别异常
    private final static int ERROR_LEVEL_UNKNOWN = -1;
    
    static {
        errorMap.put(LoginException.class, ERROR_LEVEL_IGNORE);
        
        // 404
        errorMap.put(IllegalRequestException.class, ERROR_LEVEL_IGNORE);
        // 找到uri，参数缺少    
        errorMap.put(MissingServletRequestParameterException.class, ERROR_LEVEL_IGNORE);
        // 找到uri和参数，参数类型不匹配
        errorMap.put(TypeMismatchException.class, ERROR_LEVEL_IGNORE);
        // 单条记录查询结果为空,防攻击时打印大量错误日志
        // Spring中使用JdbcTemplate的queryForObject方法，当查不到数据时会抛出如下异常：EmptyResultDataAccessException
        errorMap.put(EmptyResultDataAccessException.class, ERROR_LEVEL_IGNORE);
        // 重复提交异常
        errorMap.put(PermissionResubmitException.class, ERROR_LEVEL_IGNORE);
        // 权限异常,超权url时报的异常
        errorMap.put(PermissionUriException.class, ERROR_LEVEL_IGNORE);
        // 权限异常,参数超权的报的异常，如修改不是自己数据时报的异常（开发人员需要关注）
        errorMap.put(PermissionParamsException.class, ERROR_LEVEL_IGNORE);
        // 需要接收json参数 如：@RequestBody Test test
        errorMap.put(HttpMessageNotReadableException.class, ERROR_LEVEL_IGNORE);
        // URI不合法，太长等等
        errorMap.put(PermissionUriException.class, ERROR_LEVEL_IGNORE);
        
       
        
        
        // 数据库连接不上(如没有启动，或死掉)
        errorMap.put(CannotGetJdbcConnectionException.class, ERROR_LEVEL_CRITICAL);
        // 数据库连接池已满
        errorMap.put(CannotCreateTransactionException.class, ERROR_LEVEL_CRITICAL);
    }
    
	public static ErrorPojo getErroCode(Throwable e) {
	    if (errorMap.containsKey(e.getClass())) {
	        return new ErrorPojo(errorMap.get(e.getClass()), e.getMessage(), ERROR_LEVEL_IGNORE);
	    }
		
		if (e instanceof BindException) {
			String msg = e.getClass().getSimpleName();
			// 参数类型错误或 查询每页记录超过最大数时
			int i = e.getMessage().indexOf("IllegalRequestException");
			msg = (i >= 0) ? "maxPageSize forbidden" : msg;
			return new ErrorPojo(ErrorCode.ERROR_IGNORE, msg, ERROR_LEVEL_IGNORE);
		}
		else if (e instanceof NestedServletException) {
			String msg = e.getMessage();
			// 判断是否内存溢出
			if (msg.indexOf("OutOfMemoryError") >= 0) {
				return new ErrorPojo(ErrorCode.ERROR_OUT_OF_MEMORY, msg, ERROR_LEVEL_CRITICAL);
			}
			if (msg.indexOf("NoClassDefFoundError") >= 0) {
				return new ErrorPojo(ErrorCode.ERROR_LANG, msg, ERROR_LEVEL_CRITICAL);
			}
			
			// 判断是否找不到类
			// e.getMessage().indexOf("java.lang.NoClassDefFoundError");
			if (msg.indexOf("org.thymeleaf.exceptions") >= 0) {
				return new ErrorPojo(ErrorCode.ERROR_TEMPLATE, msg, ERROR_LEVEL_NORMAL);
			}
			return new ErrorPojo(ErrorCode.ERROR_UNKNOWN, msg, ERROR_LEVEL_UNKNOWN);
		}
		
		if ("java.lang".equals(e.getClass().getPackage().getName())) {
			// java.lang异常 NullPointerException等
			return new ErrorPojo(ErrorCode.ERROR_LANG, e.getClass().getSimpleName(), ERROR_LEVEL_NORMAL);
		}
		else if ("org.springframework.jdbc".equals(e.getClass().getPackage().getName())) {
			// jdbc异常，表不存，sql语法错误
			return new ErrorPojo(ErrorCode.ERROR_JDBC, e.getClass().getSimpleName(), ERROR_LEVEL_NORMAL);
		}
		else if ("org.springframework.dao".equals(e.getClass().getPackage().getName())) {
			// 必须栏插入空值，重复数据，Integer field返回int
			return new ErrorPojo(ErrorCode.ERROR_DAO, e.getClass().getSimpleName(), ERROR_LEVEL_NORMAL);
		}
		else if ("org.thymeleaf.exceptions".equals(e.getClass().getPackage().getName())) {
			return new ErrorPojo(ErrorCode.ERROR_TEMPLATE, e.getClass().getSimpleName(), ERROR_LEVEL_NORMAL);
		}
		return new ErrorPojo(ErrorCode.ERROR_UNKNOWN , e.getClass().getSimpleName(), ERROR_LEVEL_UNKNOWN);
	}
}
