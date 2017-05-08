package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateEmployeeTest {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = getUpdateSQL(conn);
			//giveRaise(pstmt, 1, BigDecimal.valueOf(10.0));
			giveRaise(pstmt, 3, BigDecimal.valueOf(10.0));
			JDBCUtil.commit(conn);
			System.out.println("Salary of the employee updated successfully.");
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

	
	public static void giveRaise(PreparedStatement pstmt,
			int employeeId, BigDecimal percentRaise) throws SQLException {
		// Set all the input parameters
		pstmt.setBigDecimal(1, percentRaise);
		pstmt.setInt(2, employeeId);
		// Execute the statement
		pstmt.executeUpdate();		
	}
	
	 
	public static PreparedStatement getUpdateSQL(Connection conn) throws SQLException {
		String SQL = "UPDATE employees " +
				"SET salary = salary + salary * (?/100) " +
				"WHERE employee_id = ? ";				
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		return pstmt;
	}	
}
