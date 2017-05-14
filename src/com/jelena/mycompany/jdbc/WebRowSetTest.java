package com.jelena.mycompany.jdbc;

import java.io.StringWriter;
import java.sql.SQLException;

import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.WebRowSet;

public class WebRowSetTest {

	public static void main(String[] args) {
		RowSetFactory factory = RowSetUtil.getRowSetFactory();
		try (WebRowSet webRs = factory.createWebRowSet()) {
			RowSetUtil.setConnectionParameters(webRs);
			String sqlCommand = "SELECT employee_id, first_name, last_name " +					
					"FROM employees " + 
					"WHERE employee_id between ? and ?";
			webRs.setCommand(sqlCommand);
			webRs.setInt(1, 3);
			webRs.setInt(2, 11);
			webRs.execute();		
			
			// first() moves the cursor to the first row in this ResultSet object
			// true if the cursor is on a valid row; 
			// false if there are no rows in the result set
			if (webRs.first()) {
				// change the last name for the first record
				webRs.updateString("last_name", "Who knows?");				
			}
			
			// get the XML representation of the webRs
			StringWriter sw = new StringWriter();
			webRs.writeXml(sw);
			String webRsXML = sw.toString();
			
			// print the exportes XML from the WebRowSet
			System.out.println(webRsXML);			
		} 
		catch (SQLException e) {			
			e.printStackTrace();
		}
	}
}
