package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;

import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;

public class JdbcRowSetUpdateTest {

	public static void main(String[] args) {
		// To get a RowSetFactory use the newFactory() 
		// static method of the RowSetProvider class
		RowSetFactory factory = RowSetUtil.getRowSetFactory();	
		try(JdbcRowSet jdbcRs = factory.createJdbcRowSet()) {
			// Set the connection parameters
			RowSetUtil.setConnectionParameters(jdbcRs);
			// Set the auto-commit mode to false
			jdbcRs.setAutoCommit(false);
			
			// Set the command
			String sqlCommand = "SELECT employee_id, first_name, last_name, salary " +				
					"FROM employees " +
					"WHERE employee_id = ?";
			jdbcRs.setCommand(sqlCommand);
			
			// Set input parameter
			jdbcRs.setInt(1, 3);
			
			// Retrieve the data
			jdbcRs.execute();
			
			// If a row is retrieved, update the first row's salary
			// to 700000
			if (jdbcRs.next()) {
				int employee_id = jdbcRs.getInt("employee_id");
				jdbcRs.updateBigDecimal("salary", BigDecimal.valueOf(700000));
				jdbcRs.updateRow();
				
				// Commit the changes
				jdbcRs.commit();
				
				System.out.println("Salary has been set to " +
						"700000 for employee_id = " + employee_id);
			}
			else {
				System.out.println("No employee record was found.");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
