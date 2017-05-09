package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// updates row using ResultSet object
// increases salary of all employees with non-null salary by 10%
// employees with null salary are given 100000

public class ResultSetUpdate {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = getSelectSQL(conn);
			// give 10% raise
			giveRaise(pstmt, BigDecimal.valueOf(10));			
			JDBCUtil.commit(conn);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			JDBCUtil.rollback(conn);
		}
		finally {
			JDBCUtil.closeConnection(conn);			
		}
	}
	
	
	public static void giveRaise(PreparedStatement pstmt, BigDecimal raise) throws SQLException {
		// get the result set
		ResultSet rs = pstmt.executeQuery();
		
		// check concurrency
		int concurrency = rs.getConcurrency();
		if (concurrency != ResultSet.CONCUR_UPDATABLE) {
			System.out.println("JDBC driver does not support updatable result sets.");
			return;
		}
		
		// modify salaries
		while(rs.next()) {
			// get the  salary
			BigDecimal salary = rs.getBigDecimal("salary");
			
			// modify it
			BigDecimal newSalary;
			if (rs.wasNull()) {
				newSalary = BigDecimal.valueOf(100000);
			}
			else {
				newSalary = salary.add(salary.multiply((raise.divide(BigDecimal.valueOf(100)))));
			}
			
			// update the salary column with the new value
			rs.updateBigDecimal("salary", newSalary);
			
			// show changes i am about to make in the database
			System.out.print("Employee ID: " + rs.getInt("employee_id"));
			System.out.print(", First Name: " + rs.getString("first_name"));
			System.out.print(", Last Name: " + rs.getString("last_name"));
			System.out.print(", old salary: " + salary);
			System.out.println(", new salary: " + newSalary);
			
			// send the changes to the database
			rs.updateRow();					
		}		
	}
	
	
	public static PreparedStatement getSelectSQL(Connection conn) throws SQLException {
		String SQL = "SELECT employee_id, first_name, last_name, salary " +				
				"FROM employees";		
		PreparedStatement pstmt = conn.prepareStatement(SQL, ResultSet.TYPE_FORWARD_ONLY,
															ResultSet.CONCUR_UPDATABLE);
		return pstmt;
	}	

}
