package com.ppx.cloud.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ApplicationUtils {
    
    // spring的上下文 启动时初始化 ApplicationUtils.context = SpringApplication.run(DemoApplication.class, args);
    public static ConfigurableApplicationContext context;
    
    public static String JAR_HOME;
    
    public String getJarHome()  {
//    	if ()
//    	ApplicationHome home = new ApplicationHome(DemoApplication.class);
//        ApplicationUtils.JAR_HOME = home.getSource().getParent() + "/";
        
        return "";
    }
    
    private static String serviceId = "";
    
    @Value("${server.port}")
    private String serverPort;
    
    private static String staticServerPort;
    
    public ApplicationUtils() {
    	ApplicationUtils.staticServerPort = serverPort;
    }
    
    /**
     * 取得服务ID, ip:port组成
     * 
     * @return
     */
    public static String getServiceId() {
        if (!StringUtils.isEmpty(serviceId)) {
            return serviceId;
        }
        
        String ip = "";
        String port = staticServerPort;

        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            // windows >>>>>>>>>>>>>>>>>>>>>
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
                ip = "127.0.0.1";
            }
        } else {
            // linux >>>>>>>>>>>>>>>>>>>>>>>>>
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                        .hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    String name = intf.getName();
                    if (!name.contains("docker") && !name.contains("lo")) {
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                                .hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                String ipaddress = inetAddress.getHostAddress().toString();
                                if (!ipaddress.contains("::") && !ipaddress.contains("0:0:")
                                        && !ipaddress.contains("fe80")) {
                                    ip = ipaddress;
                                }
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
                ip = "127.0.0.1";
            }
        }
        return ip + ":" + port;
    }
    
}
