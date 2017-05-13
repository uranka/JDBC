package com.jelena.mycompany.jdbc;

import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;

public class CashedRowSetTest {

	public static void main(String[] args) {
		RowSetFactory factory = RowSetUtil.getRowSetFactory();
		try (CachedRowSet cachedRs = factory.createCachedRowSet()) {
			RowSetUtil.setConnectionParameters(cachedRs);
			String sqlCommand = "SELECT employee_id, first_name, last_name, " +
					"hire_date, salary " +
					"FROM employees " + 
					"WHERE employee_id between ? and ?";
			cachedRs.setCommand(sqlCommand);
			cachedRs.setInt(1, 3);
			cachedRs.setInt(2, 11);
			cachedRs.execute();
			// number of rows in the CachedRowSet object
			System.out.println("Row count: " + cachedRs.size());
			RowSetUtil.printRowSet(cachedRs);			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
