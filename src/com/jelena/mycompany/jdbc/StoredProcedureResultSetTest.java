package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoredProcedureResultSetTest {

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			// print job details for employee whose id is 1
			printEmployeeJobDetails(conn, 1);
			printEmployeeJobDetails(conn, 11);
			printEmployeeJobDetails(conn, 3);
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
	
	public static void printEmployeeJobDetails(Connection conn, int employeeId) throws SQLException {
		// construct the stored procedure call in a string format
		String SQL = "{ call get_employee_job_details(?) }";
		CallableStatement cstmt = null;
		try {
			// prepare CallableStatement
			cstmt = conn.prepareCall(SQL);
			// set IN parameters to be passed to stored procedure
			cstmt.setInt(1, employeeId);
			// get the result set
			ResultSet rs = cstmt.executeQuery();
			// process the result set
			printResultSet(rs);
		}
		finally {
			// close statement, closes rs also
			// close statement no matter whether exception is thrown or not
			JDBCUtil.closeStatement(cstmt);		
		}
	}
	
	public static void printResultSet(ResultSet rs) throws SQLException {		
		if (!rs.isBeforeFirst()) {
		    System.out.println("No data.");
		} 
		// first_name, last_name, job_title, salary, min_salary, max_salary
		while(rs.next()) {			
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			String jobTitle = rs.getString("job_title");			
			
			// salary can be null
			BigDecimal salary = rs.getBigDecimal("salary");
			boolean isSalaryNull = rs.wasNull();
			
			BigDecimal min_salary = rs.getBigDecimal("min_salary");
			boolean isMinSalaryNull = rs.wasNull();
			
			BigDecimal max_salary = rs.getBigDecimal("max_salary");
			boolean isMaxSalaryNull = rs.wasNull();
											
			System.out.print("First Name: " + firstName);
			System.out.print(", Last Name: " + lastName);
			System.out.print(", Job Title: " + jobTitle);
			
			if (isSalaryNull) {
				System.out.print(", Salary: null");
			}
			else {
				System.out.print(", Salary: " + salary);
			}
			
			if (isMinSalaryNull) {
				System.out.print(", Min. Salary: null");
			}
			else {
				System.out.print(", Min. Salary: " + min_salary);
			}
			
			if (isMaxSalaryNull) {
				System.out.println(", Max. Salary: null");
			}
			else {
				System.out.println(", Max. Salary: " + max_salary);
			}		
		}
	}
}
