/**
 * 
 */
package com.ppx.cloud;

import java.io.InputStream;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

public class Test {

	public static void main(String[] args) {
		try {
			getLicense();
			Document doc = new Document("E:\\test.docx");
			doc.save("E:\\test.pdf", SaveFormat.PDF);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static boolean getLicense() throws Exception {
		boolean result = false;
		try {
			InputStream is = com.aspose.words.Document.class.getResourceAsStream("/License.xml");
			License aposeLic = new License();
			aposeLic.setLicense(is);
			result = true;
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public static void mainx(String[] args) {

	}
}
