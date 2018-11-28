///**
// * 
// */
//package com.ppx.cloud;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//import com.aspose.words.Document;
//import com.aspose.words.DocumentBuilder;
//import com.aspose.words.License;
//import com.aspose.words.SaveFormat;
//import com.aspose.words.SaveOptions;
//
//public class TestPdf {
//
//	public static void main(String[] args) {
//		try {
//			getLicense();
//
//			Path path = Paths.get("E:\\html.html");
//			StringBuffer xml = new StringBuffer();
//			try {
//				List<String> lines = Files.readAllLines(path);
//				lines.forEach(str -> xml.append(str));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			String html = "<!DOCTYPE html> <html><head></head> <body><div class='editor-style'>" + xml.toString() + "</div></body> </html>";
//			Document doc = new Document();
//			DocumentBuilder builder = new DocumentBuilder(doc);
//			builder.insertHtml(html);
//
//			doc.save("E:\\test.docx", SaveOptions.createSaveOptions(SaveFormat.DOCX));// 生成doc文件
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		try {
//			// long old = System.currentTimeMillis();
//			// getPPXLicense();
////			System.out.println("----------------11111111111");
////			
////			Presentation pres = new Presentation("E:\\红海科技EHR汇报.pptx");// 输入pdf路径
////			
////			
////			
////			
////			
////			pres.save("E:\\test.pdf", SaveFormat.Pdf);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("99999999r");
//		}
//		System.out.println("end");
//	}
//
//	public static boolean getPPXLicense() throws Exception {
//		boolean result = false;
//		try {
//			InputStream is = TestPdf.class.getResourceAsStream("license.xml");
//			License aposeLic = new License();
//			aposeLic.setLicense(is);
//			result = true;
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		return result;
//	}
//
//	public static boolean getLicense() throws Exception {
//		boolean result = false;
//		try {
//			InputStream is = com.aspose.words.Document.class.getResourceAsStream("/License.xml");
//			License aposeLic = new License();
//			aposeLic.setLicense(is);
//			result = true;
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		return result;
//	}
//
//	public static void mainx(String[] args) {
//
//	}
//}
