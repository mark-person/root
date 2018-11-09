package com.ppx.cloud.demo.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ppx.cloud.common.contoller.ControllerReturn;

@Controller
public class UploadImgController {
	
	private final static String UPLOAD = "upload/";
	
	private final static String IDEA_MODULE = "idea/";
	
	private final static String MAIN = "main/";
	
	private final static String ADDITIONAL = "additional/";
	
	
	public Map<?, ?> uploadIdea(@RequestParam("file") MultipartFile[] files) throws Exception {
		var returnList = new ArrayList<String>();
		
		// 不存就创建文件夹 >>>>>>>>>>>>>>>>>>>>>
		ApplicationHome home = new ApplicationHome(getClass());
		String modulePath = home.getSource().getParentFile().getParent() + "/" + UPLOAD + IDEA_MODULE;
		// 10天一个文件夹
		Date today = new Date();
		// String.format("%tj", d)一年的第几天
		String dateFolder = String.format("%ty", today)
				+ String.format("%02d", Integer.parseInt(String.format("%tj", today)) / 10) + "/";
					
		String mainPath = modulePath + dateFolder + MAIN;
		String additionalPath = modulePath + dateFolder + ADDITIONAL;
		File mainPathFile = new File(mainPath);
		if (!mainPathFile.exists()) {
			mainPathFile.mkdirs();
		}
		File additionalPathFile = new File(additionalPath);
		if (!additionalPathFile.exists()) {
			additionalPathFile.mkdirs();
		}
		
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			
			String fileName = file.getOriginalFilename();
			String ext = fileName.substring(fileName.lastIndexOf("."));
			String imgFileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;
			if (i == 0) {
				// >>>>>>>>>>>>>>>>>主
				file.transferTo(new File(mainPath + imgFileName));
				
				// 缩放
				try {
					// convert -resize 200x100 src.jpg dest.jpg 200×100(等比缩放)
					String miniPath = mainPath + imgFileName + "_100.png";
					String command = "convert " + mainPath + imgFileName + " " + miniPath;
					
					String[] commands = {"sh", "-c", "convert -resize 200x100 src.jpg dest.jpg"};
					Process process = Runtime.getRuntime().exec(commands);
					InputStream inputStream = process.getErrorStream();
					String cmdResult = new BufferedReader(new InputStreamReader(inputStream, "GBK")).lines()
							.collect(Collectors.joining(System.lineSeparator()));
					inputStream.close();
					System.out.println("command:" + command + "||" + cmdResult);
					if (!StringUtils.isEmpty(cmdResult)) {
						return ControllerReturn.error(cmdResult);
					}
				} catch (Exception e) {
					return ControllerReturn.error(e.getMessage());
				}
				returnList.add(IDEA_MODULE + dateFolder + MAIN + imgFileName);
			}
			else {
				// >>>>>>>>>>>>>>>>>附加
				file.transferTo(new File(additionalPath + imgFileName));
				returnList.add(IDEA_MODULE + dateFolder + ADDITIONAL + imgFileName);
			}
		}
		
		return ControllerReturn.success(returnList);
	}

}