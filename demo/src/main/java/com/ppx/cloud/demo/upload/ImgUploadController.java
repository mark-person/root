package com.ppx.cloud.demo.upload;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ppx.cloud.common.contoller.ControllerReturn;

@Controller
public class ImgUploadController {

    private DateFormat df = new SimpleDateFormat("yyyyMMdd");
    
    
    private final static Set<String> MODULE_SET = Set.of("idea");


    public Map<?, ?> upload(@RequestParam("file") MultipartFile[] files, @RequestParam String module) throws Exception {
    	
    	if (!MODULE_SET.contains(module)) {
    		return ControllerReturn.error("模块名称:'" + module + "'错误！");
    	}
    	
    	var returnList = new ArrayList<String>();
    	for (MultipartFile file : files) {
    	
	    	String fileName = file.getOriginalFilename();
	    	String ext = fileName.substring(fileName.lastIndexOf("."));
	    	String imgFileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;
	    	
	    	ApplicationHome home = new ApplicationHome(getClass());
	    	String yyyyMMdd = df.format(new Date());
	        
	        String upload = home.getSource().getParentFile().getParent() + "/upload/" + module + "/" + yyyyMMdd;
	        File uploadFilePath = new File(upload);
	        if (!uploadFilePath.exists()) {
	        	uploadFilePath.mkdirs();
	        }
	        
	        String r = module + "/" + yyyyMMdd + "/" + imgFileName;
	        File uploadFile = new File(home.getSource().getParentFile().getParent() + "/upload/" + r);
	        file.transferTo(uploadFile);
	        returnList.add(r);
    	}

        return ControllerReturn.success(returnList);
    }

   

}