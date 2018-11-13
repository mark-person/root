/**
 * 
 */
package com.ppx.cloud;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * @author mark
 * @date 2018年11月13日
 */
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DocResult;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;

public class Test {

	public static void main(String[] args) {
		
		System.out.println("------------begin----------");
		
		// Connect to server on localhost
		Session mySession = new SessionFactory().getSession("mysqlx://localhost:33060/world_x?user=root&password=@Dengppx123456");

		Schema myDb = mySession.getDefaultSchema();
		
		// Use the collection 'my_collection'
		Collection myColl = myDb.getCollection("test");
		
		
		// Specify which document to find with Collection.find() and
		// fetch it from the database with .execute()
		DocResult myDocs = myColl.find("name = :param").limit(1).bind("param", "abcSabc").execute();
		
		
		DbDoc doc = myDocs.fetchOne();
		
		// Print document
		System.out.println("end............:" + doc.toString());

		mySession.close();
	}
}


















