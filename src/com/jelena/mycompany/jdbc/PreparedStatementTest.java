package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PreparedStatementTest {
	
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = getInsertSQL(conn);
			// get hire date in java.sql.Date object
			Date hireDate = Date.valueOf("1999-01-12");
			// translate the double value into a BigDecimal
			BigDecimal salary = BigDecimal.valueOf(560000.0);
			// Insert two employees
			insertEmployee(pstmt, 2,"Marko","Markov", hireDate, 3, salary);
			insertEmployee(pstmt, 3,"Jelena","Gavanski", null, 3, null);	
						
			JDBCUtil.commit(conn);
			System.out.println("Employees inserted successfully.");
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
			JDBCUtil.rollback(conn);
		}
		finally {
			JDBCUtil.closeStatement(pstmt);
			JDBCUtil.closeConnection(conn);
		}
	}
	
	
	// inserts all except LOBs (picture and cv)	
	public static void insertEmployee(PreparedStatement pstmt,
			int employeeId, String firstName, String lastName,
			Date hireDate, int jobId, BigDecimal salary) throws SQLException {		
			// Set all the input parameters
			pstmt.setInt(1, employeeId);
			pstmt.setString(2, firstName);
			pstmt.setString(3, lastName);
			// hire date can be null			
			if (hireDate == null) {
				pstmt.setNull(4, Types.DATE);
			}
			else {
				pstmt.setDate(4, hireDate);
			}
			pstmt.setInt(5, jobId);
			// salary can be null
			if (salary == null) {
				pstmt.setNull(6, Types.DECIMAL);
			}
			else {
				pstmt.setBigDecimal(6, salary);
			}			
			// Execute the statement
			pstmt.executeUpdate();
	}
	
	
	public static PreparedStatement getInsertSQL(Connection conn) throws SQLException {
			String SQL = "INSERT INTO employees " +
						"(employee_id, first_name, last_name, hire_date, job_id, salary)" +
						"values " +
						"(?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			return pstmt;
	}
}
