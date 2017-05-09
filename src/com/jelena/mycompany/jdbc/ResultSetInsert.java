package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetInsert {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = getSelectSQL(conn);
			addRow(pstmt);			
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

	
	public static void addRow(PreparedStatement pstmt) throws SQLException {
		ResultSet rs = pstmt.executeQuery();
		// proveri concurrency tj. proveri da li je ResultSet updatable 
		// jer to sto ja trazim ResultSet.CONCUR_UPDATABLE ne znaci da cu i da dobijem		
		int concurrency = rs.getConcurrency();
		if (concurrency != ResultSet.CONCUR_UPDATABLE) {
			System.out.println("JDBC driver does not support updatable result sets.");
			return;
		}
		
		// move to imaginary insert row
		rs.moveToInsertRow();
		
		// set the values for all the columns
		// all non-nullable columns must be set
		rs.updateInt("employee_id", 11);
		rs.updateString("first_name", "Jane");
		rs.updateString("last_name", "Jones");
		// get hire date in java.sql.Date object
		Date hireDate = Date.valueOf("2017-09-23");
		rs.updateDate("hire_date", hireDate);
		// translate the double value into a BigDecimal
		BigDecimal salary = BigDecimal.valueOf(750000.0);
		rs.updateBigDecimal("salary", salary);
		rs.updateInt("job_id", 3);
		
		// send newly inserted row to the database
		rs.insertRow();
		// auto-commit mode is disabled so I must commit transaction
		// see try block inside main
		
		// move to the previously current row (that is the first row in rs) to print result set		
		// see printResultSet in  QueryEmployeeTest for printing everything from rs
		rs.moveToCurrentRow();
		while(rs.next()) {
			System.out.print("Employee ID: " + rs.getInt("employee_id"));
			System.out.print(", First Name: " + rs.getString("first_name"));
			System.out.print(", Last Name: " + rs.getString("last_name"));
			System.out.println(", Job ID: " + rs.getInt("job_id"));
		}		
		JDBCUtil.closeStatement(pstmt); // closes its rs also		
	}

// Mogla sam i obican Statement, ali neka bude Preparedstatement
// Although PreparedStatements have to be compiled, which would take some time, the compiled 
// version holds a reference to the SQL execution plan in the database.
// Once compiled, the PreparedStatement is stored in a connection specific cache, 
// so that the compiled version may be reused to achieve performance gains.	
	public static PreparedStatement getSelectSQL(Connection conn) throws SQLException {
			String SQL = "SELECT employee_id, first_name, last_name, " +
					" hire_date, salary, job_id " +
					"FROM employees";		
		PreparedStatement pstmt = conn.prepareStatement(SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		return pstmt;
	}
}
