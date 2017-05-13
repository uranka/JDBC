package com.jelena.mycompany.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class LOBTest {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		//String inPicturePath = "picture3.jpg";
		//String inPicturePath = "picture6.jpg";
		// updating employee's picture with a new picture
		String inPicturePath = "picture6new.jpg";
		ensureFileExistence(inPicturePath);
		try {
			conn = JDBCUtil.getConnection();			
			pstmt = getUpdateSQL(conn);			
			//updateEmployeePicture(conn, pstmt, 3, inPicturePath);
			updateEmployeePicture(conn, pstmt, 6, inPicturePath);
			JDBCUtil.commit(conn);
			System.out.println("Updated employee's picture successfully.");	
		}
		catch(SQLException e) {		
			System.out.println("Updating employee's picture failed: ");
			System.out.println(e.getMessage());
			JDBCUtil.rollback(conn);
		}
		finally {
			JDBCUtil.closeConnection(conn);
		}
	}
	
		
	public static void updateEmployeePicture(Connection conn, PreparedStatement pstmt,
			int employeeId,  String pictureFilePath) throws SQLException {
		try {
			// Set the picture data
			if (pictureFilePath != null) {
				// We need to create a Blob object first
				Blob pictureBlob = conn.createBlob();
				readInPictureData(pictureBlob, pictureFilePath);
				pstmt.setBlob(1, pictureBlob);
			}
			pstmt.setInt(2, employeeId);		
			pstmt.executeUpdate();					
		}
		catch (IOException | SQLException e) {
			throw new SQLException(e);
		}
		finally {
			JDBCUtil.closeStatement(pstmt);
		}
	}

	
	// iz fajla slike u blob
	public static void readInPictureData(Blob pictureBlob, String pictureFilePath) 
			throws FileNotFoundException, IOException, SQLException {
		// Get the output stream of the Blob object to write
		// the picture data to it.
		int startPosition = 1; // start writing from the beginning
		OutputStream out = pictureBlob.setBinaryStream(startPosition);
		FileInputStream fis = new FileInputStream(pictureFilePath);
		// Read from the file and write to the Blob object
		int b = -1;
		while ((b = fis.read()) != -1) {
			out.write(b);
		}
		fis.close();
		out.close();
	}

	
	public static PreparedStatement getUpdateSQL(Connection conn) throws SQLException {
		String SQL = "UPDATE employees " +
				"SET picture = ? " +
				"WHERE employee_id = ? ";				
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		return pstmt;
	}
	
	
	public static void ensureFileExistence(String filePath) {
		Path path = Paths.get(filePath);
		if (!Files.exists(path)) {
			throw new RuntimeException("File " + path.toAbsolutePath() + " does not exist");
		}
	}	
}
