package com.ppx.cloud.demo.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;

	public ModelAndView list() {
		ModelAndView mv = new ModelAndView();

		return mv;
	}

	public Map<?, ?> test() {
		var map = new HashMap<String, Object>();
		impl.test();

		map.put("result", 4);
		return map;
	}

	public static void main(String[] args) {
		System.out.println(".....................1");
		String c = "convert -resize 100x100 E:\\eclipse-workspace-201809\\root\\demo/upload/idea/1830/1224db07b00b47798dcc70c42859bef7.png E:\\eclipse-workspace-201809\\root\\demo/upload/idea/1830/mini/1224db07b00b47798dcc70c42859bef7.png_200.png";

		try {
			System.out.println("0000000:" + c);
			Process process = Runtime.getRuntime().exec(c);
			InputStream inputStream = process.getErrorStream();
			String result = new BufferedReader(new InputStreamReader(inputStream, "GBK")).lines()
					.collect(Collectors.joining(System.lineSeparator()));
			System.out.println(".....result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

}
