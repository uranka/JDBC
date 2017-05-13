package com.jelena.mycompany.jdbc;

import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;

public class CachedRowSetPagingTest {

	public static void main(String[] args) {
		RowSetFactory factory = RowSetUtil.getRowSetFactory();
		try (CachedRowSet cachedRs = factory.createCachedRowSet()) {
			RowSetUtil.setConnectionParameters(cachedRs);
			String sqlCommand = "SELECT employee_id, first_name, last_name, " +
					"hire_date, salary " +
					"FROM employees ";
			cachedRs.setCommand(sqlCommand);
			// page size is 3
			cachedRs.setPageSize(3);
			
			// retrieves the first page
			cachedRs.execute();
			
			int pageCounter = 1;
			// retrieve and print employee details one page at a time
			do {
				System.out.println("Page #" + pageCounter);
				System.out.println("Row count of this page is " + cachedRs.size());
				// print rows currently in the row set (current page)
				RowSetUtil.printRowSet(cachedRs);
				System.out.println();
				pageCounter++;
			}while (cachedRs.nextPage());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}					
	}
}
