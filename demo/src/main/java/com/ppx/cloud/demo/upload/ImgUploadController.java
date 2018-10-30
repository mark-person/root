package com.ppx.cloud.demo.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.exception.custom.PermissionParamsException;

@Controller
public class ImgUploadController {

	private final static Set<String> MODULE_SET = Set.of("idea");

	public Map<?, ?> upload(@RequestParam("file") MultipartFile[] files, @RequestParam String module) throws Exception {

		if (!MODULE_SET.contains(module)) {
			throw new PermissionParamsException("模块名称:'" + module + "'错误！");
		}

		var returnList = new ArrayList<String>();

		String errorMsg = "";
		for (MultipartFile file : files) {

			// 10天一个文件夹
			Date d = new Date();
			// String.format("%tj", d)一年的第几天
			String dateFolder = String.format("%ty", d)
					+ String.format("%02d", Integer.parseInt(String.format("%tj", d)) / 10);

			String fileName = file.getOriginalFilename();
			String ext = fileName.substring(fileName.lastIndexOf("."));
			String imgFileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;

			ApplicationHome home = new ApplicationHome(getClass());
			String uploadPath = home.getSource().getParentFile().getParent() + "/upload/";

			String uploadFolder = uploadPath + module + "/" + dateFolder;
			File uploadFolderFile = new File(uploadFolder);
			if (!uploadFolderFile.exists()) {
				uploadFolderFile.mkdirs();
			}

			String r = module + "/" + dateFolder + "/" + imgFileName;
			String uploadFile = uploadPath + r;
			file.transferTo(new File(uploadFile));

			String miniPath = uploadPath + module + "/" + dateFolder + "/mini";
			File miniPathFile = new File(miniPath);
			if (!miniPathFile.exists()) {
				miniPathFile.mkdirs();
			}

			try {
				String command = "convertx -resize 100x100 " + uploadFile + " " + miniPath + "/" + imgFileName + "_200"
						+ ext;
				System.out.println("0000000:" + command);
				Process process = Runtime.getRuntime().exec(command);
				InputStream inputStream = process.getErrorStream();
				String cmdResult = new BufferedReader(new InputStreamReader(inputStream, "GBK")).lines()
						.collect(Collectors.joining(System.lineSeparator()));
				System.out.println(".....result:" + cmdResult);
				inputStream.close();

				if (!StringUtils.isEmpty(cmdResult)) {
					errorMsg = cmdResult;
					break;
				}
			} catch (Exception e) {
				errorMsg = e.getMessage();
				break;
			}

			// convert -resize 200x100 src.jpg dest.jpg
			// 虽然明确指定了图片大小为200×100，但dest.jpg的不一定就是200×100，因为是等比缩放的

			returnList.add(r);
		}

		if (!StringUtils.isEmpty(errorMsg)) {
			return ControllerReturn.error(errorMsg);
		} else {
			return ControllerReturn.success(returnList);
		}

	}

}