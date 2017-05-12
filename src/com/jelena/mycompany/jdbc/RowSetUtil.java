package com.jelena.mycompany.jdbc;

import java.math.BigDecimal;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.RowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

public class RowSetUtil {
	private static boolean driverLoaded = false;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat(			
			"dd. MMM yyyy.");  //  17. Mar 1987.


	public static void setConnectionParameters(RowSet rs) throws SQLException {
		// Register the JDBC driver only once for your database
		if (!driverLoaded) {
			// Change the JDBC driver class for your database
			Driver mysqlDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(mysqlDriver);
			driverLoaded = true;
		}
		// Set the rowset database connection properties
		String dbURL ="jdbc:mysql://localhost:3306/mycompany";		
		String userId = "root";
		String password = "";
		rs.setUrl(dbURL);
		rs.setUsername(userId);
		rs.setPassword(password);
	}
	

	public static RowSetFactory getRowSetFactory() {
		try {
			RowSetFactory factory = RowSetProvider.newFactory();
			return factory;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
	public static void printRowSet(RowSet rs) throws SQLException {	
		if (!rs.isBeforeFirst()) {
		    System.out.println("No data.");
		} 
		while(rs.next()) {
			int employeeId = rs.getInt("employee_id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			
			// hire_date can be null
			Date hireDate = rs.getDate("hire_date");
			boolean isHireDateNull = rs.wasNull();
			
			// salary can be null
			BigDecimal salary = rs.getBigDecimal("salary");
			boolean isSalaryNull = rs.wasNull();
			
			String formattedHireDate = null;
			if (!isHireDateNull) {
				formattedHireDate = formatDate(hireDate);
			}
			
			System.out.print("Employee ID: " + employeeId);
			System.out.print(", First Name: " + firstName);
			System.out.print(", Last Name: " + lastName);
			if (isHireDateNull) {
				System.out.print(", Hire Date: null");
			}
			else {
				System.out.print(", Hire Date: " + formattedHireDate);
			}
			if (isSalaryNull) {
				System.out.println(", Salary: null");
			}
			else {
				System.out.println(", Salary: " + salary);
			}			
		}
	}
	
	
	public static String formatDate(Date dt) {
		if (dt == null) {
			return "";
		}
		else {
			String formattedDate = sdf.format(dt);
			return formattedDate;
		}		
	}	
}
