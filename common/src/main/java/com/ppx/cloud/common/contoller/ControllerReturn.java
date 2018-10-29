package com.ppx.cloud.common.contoller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ControllerReturn {
	
	private static final String result = "result";
	
	private static final String msg = "msg";
	
	private static final String value = "value";
	
	public static final Map<Object, Object> SUCCESS = Map.of(result, 1, msg, "SUCCESS");
	
	public static final Map<Object, Object> EXISTS = Map.of(result, 0, msg, "EXISTS");
	
	public static final Map<Object, Object> ERROR = Map.of(result, -1, msg, "ERROR");
	
	public static Map<Object, Object> error(Object... object) {
		if (object == null || object.length == 0) {
			return SUCCESS;
		}
		if (object.length == 1) {
			if ("1".equals(object[0].toString())) {
				return ERROR;
			}
			else if (object[0].getClass() == String.class || object[0].getClass().getSuperclass() == Number.class
					|| object[0].getClass() == Boolean.class) {
				return Map.of(result, -1, msg, "ERROR", value, object[0]);
			}
		}
		
		Map<Object, Object> r = new LinkedHashMap<Object, Object>(object.length + 2);
		r.putAll(ERROR);
		for (Object obj : object) {
			if (obj instanceof Map) {
                r.putAll((Map<?, ?>)obj);
            }
			else if (obj != null) {
				// 第一个字母变小写
				char[] strChar = obj.getClass().getSimpleName().toCharArray();
				strChar[0] += 32;
				String key = String.valueOf(strChar);
				r.put(key, obj);
			}
		}
		return r;
	}
	
	public static Map<Object, Object> success(Object... object) {
		if (object == null || object.length == 0) {
			return SUCCESS;
		}
		if (object.length == 1) {
			if ("1".equals(object[0].toString())) {
				return SUCCESS;
			}
			else if ("0".equals(object[0].toString())) {
				return EXISTS;
			}
			else if (object[0].getClass() == String.class || object[0].getClass().getSuperclass() == Number.class
					|| object[0].getClass() == Boolean.class) {
				return Map.of(result, 1, msg, "SUCCESS", value, object[0]);
			}
		}
		
		Map<Object, Object> r = new LinkedHashMap<Object, Object>(object.length + 2);
		r.putAll(SUCCESS);
		for (Object obj : object) {
			if (obj instanceof Map) {
                r.putAll((Map<?, ?>)obj);
            }
			else if (obj != null) {
				// 第一个字母变小写
				char[] strChar = obj.getClass().getSimpleName().toCharArray();
				strChar[0] += 32;
				String key = String.valueOf(strChar);
				r.put(key, obj);
			}
		}
		return r;
	}
	
	
	/**
         * 返回错误(HMTL格式)
     * 
     * @param response
     * @param errorCode
     * @param errorInfo
     */
    public static void returnErrorHtml(HttpServletResponse response, Integer errorCode, String errorInfo) {
    	response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write("[" + errorCode + "]" + "System Message[" + errorInfo + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回错误(JSON格式)
     * 
     * @param response
     * @param errorCode
     * @param errorInfo
     */
    public static void returnErrorJson(HttpServletResponse response, Integer errorCode, String errorInfo) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Map<Object, Object> map = ControllerReturn.error(errorCode, errorInfo);
        try (PrintWriter printWriter = response.getWriter()) {
            String returnJson = new ObjectMapper().writeValueAsString(map);
            printWriter.write(returnJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void returnJson(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
