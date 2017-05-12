package com.jelena.mycompany.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class ResultSetMetaDataTest {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = getSelectSQL(conn);
			ResultSet rs = pstmt.executeQuery();
			printMetaData(rs);
			JDBCUtil.commit(conn);			
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
	
	public static void printMetaData(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		System.out.println("Column Count: " + columnCount);
		
		for (int i = 1; i <= columnCount; i++) {
			System.out.print("column index: " + i);
			System.out.print(", column name: " + rsmd.getColumnName(i));			
			// za kolone za koje nemam u SQL-u AS, bice isto sto i column name
			System.out.print(", column label: " + rsmd.getColumnLabel(i));			
			// tip u bazi
			System.out.print(", type in database: " + rsmd.getColumnTypeName(i));						
			// tip Java klase
			System.out.println(", Java class name: " + rsmd.getColumnClassName(i));						
		}		
	}
	
	
	public static PreparedStatement getSelectSQL(Connection conn) throws SQLException {
		String SQL = "SELECT employee_id AS \"employee ID\", first_name, last_name, " +
				" hire_date, salary " +
				"FROM employees";											
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		return pstmt;
	}
}
