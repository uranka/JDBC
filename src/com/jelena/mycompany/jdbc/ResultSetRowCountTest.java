package com.jelena.mycompany.jdbc;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetRowCountTest {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			// Get a Connection
			conn = JDBCUtil.getConnection();			
			// Request prepared statement
			pstmt = getSelectSQL(conn);
			// Execute the query
			ResultSet rs = pstmt.executeQuery();
			
			// Checking the properties of result set
			// Making sure I got a bidirectional ResultSet
			int cursorType = rs.getType();
			if (cursorType == ResultSet.TYPE_FORWARD_ONLY) {
				System.out.println("JDBC driver returned a forward-only cursor");
			}
			else {
				// move the cursor to the last row
				rs.last();
				
				// get  the last row number = number of rows in result set				
				int rowCount = rs.getRow();
				System.out.println("Row count: " + rowCount);
				
				// place the cursor before the first row  to process all rows again				
				rs.beforeFirst();
			}
			// process the result set
			while (rs.next()) {
				System.out.println("Employee ID: " + rs.getInt(1));
			}			
		}
		catch(SQLException e) {
			e.printStackTrace();			
		}
		finally {
			JDBCUtil.closeStatement(pstmt);
			JDBCUtil.commit(conn);
			JDBCUtil.closeConnection(conn);
		}
	}
	
	public static PreparedStatement getSelectSQL(Connection conn) throws SQLException {
		String SQL = "SELECT employee_id, first_name, last_name, " +
				" hire_date, salary " +
				"FROM employees";
		
		// requests a bidirectional scrollable ResultSet
		// insensitive to changes in the underlying database
		// read-only (cannot update its data)		
		PreparedStatement pstmt = conn.prepareStatement(SQL, TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
		return pstmt;
	}
}
