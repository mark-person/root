


/**
 *  A覆盖Spring源码，目的:约定优于配置，基础决定命运
 *  
 *  
 *  RequestResponseBodyMethodProcessor: Controller的方法返回值为 Map<?, ?> || Map<Object, Object>的，返回JSON
 *  AbstractHandlerMethodMapping: Controller的默认路径auto/controllerName/methodName
 *  DispatcherServlet: mvc的html默认路径
 *  StandardJavaScriptSerializer: json改成跟异步调用统一
 *  
 *  
 * @since Spring boot 2.1.0.RELEASE
 * @author mark
 * @date 2018年11月12日
 */
package org;

