package com.ppx.cloud.monitor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.util.StringUtils;

import com.ppx.cloud.common.config.ObjectMapperCustomer;
import com.ppx.cloud.monitor.config.MonitorThresholdProperties;
import com.ppx.cloud.monitor.pojo.AccessLog;



/**
 * 访问日志工具类
 * @author mark
 * @date 2018年11月7日
 */
public class MonitorUtils {

    private static OperatingSystemMXBean operatingSystemMXBean;

    public static OperatingSystemMXBean getOperatingSystemMXBean() {
        return operatingSystemMXBean;
    }

    public static void setOperatingSystemMXBean(OperatingSystemMXBean operatingSystemMXBean) {
        MonitorUtils.operatingSystemMXBean = operatingSystemMXBean;
    }
    
    /**
     * 并发数｜最大处理时间｜最大时线程名称｜最大时Uri
     * @return
     */
    public static Map<String, Object> getRequestInfo(boolean isOverMem) {
    	
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("isOverTime", false);
        
        int concurrentN = 0;
        long maxProcessingTime = 0;
        String maxWorkerThreadName = "";
        String maxUri = "";     
        List<String> maxMsg = new ArrayList<String>();
        
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            Set<ObjectInstance> set = mBeanServer.queryMBeans(new ObjectName("*:type=RequestProcessor,*"), null);
            Iterator<ObjectInstance> iterator = set.iterator();
            while (iterator.hasNext()) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                Object currentUri = mBeanServer.getAttribute(oi.getObjectName(), "currentUri");
                if (currentUri != null) {
                    concurrentN++;                                                      
                }
                Long requestProcessingTime = (Long)mBeanServer.getAttribute(oi.getObjectName(), "requestProcessingTime");
                if (currentUri != null && requestProcessingTime > maxProcessingTime) {
                    maxProcessingTime = requestProcessingTime;
                    Object threadName = mBeanServer.getAttribute(oi.getObjectName(), "workerThreadName");
                    if (threadName != null) {
                        maxWorkerThreadName = threadName.toString();
                    }                           
                    maxUri = currentUri.toString();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        if (maxProcessingTime > MonitorThresholdProperties.DUMP_MAX_TIME) {
            info.put("isOverTime", true);
        }
        // 超过指定时间或内存超出，则输出线程信息
        if ((maxProcessingTime > MonitorThresholdProperties.DUMP_MAX_TIME || isOverMem) && !"".equals(maxWorkerThreadName)) {
            Thread t = MonitorUtils.getThread(maxWorkerThreadName);
            if (t instanceof TaskThread) {
                // 找出accessLog
                AccessLog accessLog = TaskThread.getAccessLog();
                if (accessLog != null) {
                    info.put("accessLog", accessLog);
                }
            }
            
            StackTraceElement[] stackTraceElements = t.getStackTrace();                 
            for (StackTraceElement e : stackTraceElements) {
                if (e.getClassName().startsWith("com.ppx") && e.getLineNumber() > 0) {
                    maxMsg.add(e.getClassName() + "(" + e.getLineNumber() + ")");
                }
            }
        }
        info.put("concurrentN", concurrentN);
        info.put("maxProcessingTime", maxProcessingTime);
        info.put("maxUri", maxUri);
        if (maxMsg.size() > 0) {
            info.put("timeOverDump", maxMsg);
        }
        return info;
    }
    
    public static Thread getThread(String threadName) {
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        Iterator<Thread> it = map.keySet().iterator();  
        while (it.hasNext()) {  
            Thread t = (Thread) it.next(); 
            if (t.getName().equals(threadName)) return t; 
        }
        return null;
    }
    
    public static long getTotalPhysicalMemorySize() {       
        try {
            Method m = operatingSystemMXBean.getClass().getDeclaredMethod("getTotalPhysicalMemorySize");
            m.setAccessible(true);
            long totalPhysicalMemorySize = (Long)m.invoke(operatingSystemMXBean) / 1024 / 1024;
            return totalPhysicalMemorySize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static long getFreePhysicalMemorySize() {        
        try {
            Method m = operatingSystemMXBean.getClass().getDeclaredMethod("getFreePhysicalMemorySize");
            m.setAccessible(true);
            long totalPhysicalMemorySize = (Long)m.invoke(operatingSystemMXBean)  / 1024 / 1024;
            return totalPhysicalMemorySize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static double getSystemCpuLoad() {       
        try {
            Method m = operatingSystemMXBean.getClass().getDeclaredMethod("getSystemCpuLoad");
            m.setAccessible(true);
            Double systemCpuLoad = (Double)m.invoke(operatingSystemMXBean);
            return systemCpuLoad;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /*
     * Limitations:
     *  The getSystemLoadAverage() and disk space querying methods are only available under Java 6. Also,
     *  some JMX functionality may not be available to all platforms 
     *  (i.e. it's been reported that getSystemLoadAverage() returns -1 on Windows).
     */
    public static double getProcessCpuLoad() {
        try {
            Method m = operatingSystemMXBean.getClass().getDeclaredMethod("getProcessCpuLoad");
            m.setAccessible(true);
            Double processCpuLoad = (Double)m.invoke(operatingSystemMXBean);
            return processCpuLoad;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static long getTotalSpace() {   
    	// 获取磁盘分区列表  File[] roots = File.listRoots();启动时java.lang.NoSuchMethodError: java.util.BitSet._jr$ig$wordsInUse
    	
    	File f = new File("/");
    	long totalSpace = f.getTotalSpace() / 1024 / 1024;
    	return totalSpace;
    }
    
    /**
         * 获取磁盘分区列表
     * @return
     */
    public static long getUsableSpace() {
    	// 获取磁盘分区列表  File[] roots = File.listRoots();启动时java.lang.NoSuchMethodError: java.util.BitSet._jr$ig$wordsInUse
    	
        File f = new File("/");
        long usableSpace = f.getUsableSpace() / 1024 / 1024;
        return usableSpace;
    }

    /**
     * 读取请求参数(包括get和post)
     * 
     * @param request
     * @return
     */
    public static String getParams(HttpServletRequest request) {
        List<String> params = new ArrayList<String>();
        Enumeration<String> pNames = request.getParameterNames();
        while (pNames.hasMoreElements()) {
            String name = pNames.nextElement();
            String[] v = request.getParameterValues(name);
            for (String s : v) {
                params.add(name + "=" + s);
            }
        }
        return StringUtils.collectionToDelimitedString(params, "&");
    }
    
    public static String getExcepiton(Throwable t) {
        // 存放少量异常信息com.ppx开头  加上getCause().getCause()
        List<StackTraceElement> printListStack = new ArrayList<StackTraceElement>();
        List<StackTraceElement> allListStack = new ArrayList<StackTraceElement>();
        
        StackTraceElement[] stackTraceElement = t.getStackTrace();
        allListStack.addAll(Arrays.asList(stackTraceElement));
        
        if (t.getCause() != null) {
            StackTraceElement[] cause1 = t.getCause().getStackTrace();
            allListStack.addAll(Arrays.asList(cause1));
        }
        if (t.getCause() != null && t.getCause().getCause() != null) {
            StackTraceElement[] cause2 = t.getCause().getCause().getStackTrace();
            allListStack.addAll(Arrays.asList(cause2));
        }
        
        for (StackTraceElement element : allListStack) {
            if (element.getClassName().startsWith("com.ppx") && element.getLineNumber() > 0) {
                printListStack.add(element);
            }
        }
        
        Throwable myThrowable = null;
        if (!printListStack.isEmpty()) {
            myThrowable = new Exception(t.getClass().getName() + ":" + t.getMessage());
            myThrowable.setStackTrace(printListStack.toArray(new StackTraceElement[printListStack.size()]));
        } else {
            // 如果没有找到com.ppx的异常，则打印所有
            myThrowable = t;
        }
        
        String r = "";
        try {
            String myThrowableJson = new ObjectMapperCustomer().writeValueAsString(myThrowable);
            r = myThrowableJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return r;
    }
    
    public static String getReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            String preUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            referer = referer.replace(preUrl, "");
        }
        return referer;
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>CPU
    public static String getCpuTopDetail() {
        try {
            String pid = System.getProperty("PID");
            // top -Hp PID 结果转十六进制
            Process topProcess = Runtime.getRuntime().exec("top -bHp " + pid +" -n 1");
            
            String topOutput1 = "";
            String topOutput2 = "";
            StringBuffer topMsg = new StringBuffer();
            try (InputStream inputStream = topProcess.getInputStream();
                     InputStreamReader isr = new InputStreamReader(inputStream);
                     BufferedReader reader = new BufferedReader(isr);) {
                 String line;
                 int i = 0;
                 while ((line = reader.readLine()) != null) {
                     ++i;
                     topMsg.append(line + "\n");
                     if (i == 8) {
                         topOutput1 = line;
                     }
                     else if (i == 9) {
                         topOutput2 = line;
                     }
                     else if (i == 12) {
                         break;
                     }
                 }
            }
            
            String threadId1 = "";
            String threadId2 = "";
            if (!StringUtils.isEmpty(topOutput1) && !StringUtils.isEmpty(topOutput2)) {
                threadId1 = topOutput1.trim().split(" ")[0];
                threadId2 = topOutput2.trim().split(" ")[0];
            }
            
            String threadIdHex1 = Integer.toHexString(Integer.parseInt(threadId1));
            String threadIdHex2 = Integer.toHexString(Integer.parseInt(threadId2));
            
            List<String> traceList1 = getTrace(threadIdHex1);
            List<String> traceList2 = getTrace(threadIdHex2);
            // 
            String threadMsg1 = "";
            String s1 = "";
            String threadMsg2 = "";
            String s2 = "";
            if (!traceList1.isEmpty()) {
                String theadName1 = traceList1.get(0).split("\" ")[0].replaceAll("\"", "");
                threadMsg1 = getTheadMsg(theadName1);
                s1 = StringUtils.collectionToDelimitedString(traceList1, "\n");
            }
            if (!traceList2.isEmpty()) {
                String theadName2 = traceList2.get(0).split("\" ")[0].replaceAll("\"", "");
                threadMsg2 = getTheadMsg(theadName2);  
                s2 = StringUtils.collectionToDelimitedString(traceList2, "\n");
            }
            
            return topMsg.toString() + "trace1:\n" + s1 + "-----\ntrace2:\n"  + s2 
                    + "\nthread1:\n" + threadMsg1 + "-----\nthread2:\n" + threadMsg2;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    private static List<String> getTrace(String threadIdHex) throws Exception  {
        List<String> traceList = new ArrayList<String>();
        
        String pid = System.getProperty("PID");
        String javaBinPath = System.getProperty("java.home").replace("jre", "bin/");
        String jstackCmd = "sh -c '" + javaBinPath + "jstack " + pid + " | grep 0x" + threadIdHex + "\\  -A 15'";
        
        Process process = Runtime.getRuntime().exec(jstackCmd);
        try (InputStream inputStream = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(inputStream);
                 BufferedReader reader = new BufferedReader(isr);) {
             String line;
             while ((line = reader.readLine()) != null) {
                 traceList.add(line);
             }
        }
        return traceList;
    }
    
    private static String getTheadMsg(String theadName) {
        Thread thread = MonitorUtils.getThread(theadName);
        String threadMsg = "";
        if (thread != null && thread instanceof TaskThread) {
            AccessLog accessLog = TaskThread.getAccessLog();
            List<String> infoList = AccessLogUtils.getInfoList(accessLog);
            threadMsg = StringUtils.collectionToDelimitedString(infoList, "\n");
        }
        else if (thread != null) {
            threadMsg = thread.getClass().getName();
        }
        return threadMsg;
    }
    
    
    
    

}
