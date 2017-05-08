package com.jelena.mycompany.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteEmployeeTest {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = getDeleteSQL(conn);
			// Deletes employee whose employee_id is 2
			deleteEmployee(pstmt, 2);
			JDBCUtil.commit(conn);
			System.out.println("Employee deleted successfully.");
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			JDBCUtil.rollback(conn);
		}
		finally {
			JDBCUtil.closeStatement(pstmt);
			JDBCUtil.closeConnection(conn);
		}			
	}
	
	
	public static void deleteEmployee (PreparedStatement pstmt,
			int employeeId) throws SQLException {
		// Set all the input parameters
		pstmt.setInt(1, employeeId);
		// Execute the statement
		pstmt.executeUpdate();		
	}
	
	
	public static PreparedStatement getDeleteSQL(Connection conn) throws SQLException {
		String SQL = "DELETE FROM employees " +
				"WHERE employee_id = ? ";					
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		return pstmt;
	}
}
