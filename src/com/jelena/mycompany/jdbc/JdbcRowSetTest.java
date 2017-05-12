package com.jelena.mycompany.jdbc;

import java.sql.SQLException;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;

public class JdbcRowSetTest {

	public static void main(String[] args) {
		// To get a RowSetFactory use the newFactory() static method
		// of the RowSetProvider class
		RowSetFactory factory = RowSetUtil.getRowSetFactory();
		
		// Use a try-with-resources block
		// to create row set of jdbc type
		// SQLException is thrown if JdbcRowSet cannot be created
		try (JdbcRowSet jdbcRs = factory.createJdbcRowSet()) {
			// Set the connection parameters
			RowSetUtil.setConnectionParameters(jdbcRs);
			// Set the command and input parameters
			String sqlCommand = "SELECT employee_id, first_name, last_name, " +
			"hire_date, salary " +
			"FROM employees " + 
			"WHERE employee_id between ? and ?";
			
			jdbcRs.setCommand(sqlCommand);
			jdbcRs.setInt(1, 3);
			jdbcRs.setInt(2, 11);
			// Retrieve the data
			jdbcRs.execute();
			// Scroll to the last row to get the row count It may throw an
			// exception if the underlying JdbcRowSet implementation
			// does not support a bidirectional scrolling result set.
			try {
				jdbcRs.last();
				System.out.println("Row Count: " + jdbcRs.getRow());
				// Position the cursor before the first row
				jdbcRs.beforeFirst();
			}
			catch(SQLException e) {
				System.out.println("JdbcRowSet implementation" +
						" supports forward-only scrolling");
			}
			// Print the records in the rowset
			RowSetUtil.printRowSet(jdbcRs);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}


