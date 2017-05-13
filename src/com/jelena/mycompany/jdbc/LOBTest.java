package com.jelena.mycompany.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class LOBTest {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		//String inPicturePath = "picture3.jpg";
		//String inPicturePath = "picture6.jpg";
		// updating employee's picture with a new picture
		// String inPicturePath = "picture6new.jpg";
		String inPicturePath = "picture7.jpg";
		ensureFileExistence(inPicturePath);
		try {
			conn = JDBCUtil.getConnection();
			
			// UPDATE PICTURE
			try {				
				pstmt = getUpdateSQL(conn);			
				//updateEmployeePicture(conn, pstmt, 3, inPicturePath);				
				//updateEmployeePicture(conn, pstmt, 6, null);
				//updateEmployeePicture(conn, pstmt, 6, inPicturePath);
				updateEmployeePicture(conn, pstmt, 7, inPicturePath);
				JDBCUtil.commit(conn);
				System.out.println("Updated employee's picture successfully.");					
			}
			catch(SQLException e) {		
				System.out.println("Updating employee's picture failed: ");
				System.out.println(e.getMessage());
				JDBCUtil.rollback(conn);
			}
			
			// RETRIEVE PICTURE	
			//String outPicturePath = "out_picture6.jpg";	
			String outPicturePath = "out_picture7.jpg";			
			try {
				//retrieveEmployeePicture(conn, 6, outPicturePath);
				retrieveEmployeePicture(conn, 7, outPicturePath);
				JDBCUtil.commit(conn);
				System.out.println("Retrieved and saved employee's picture successfully");				
			}
			catch(SQLException e) {
				System.out.println("Retrieving employee's picture failed: ");
				System.out.println(e.getMessage());
				JDBCUtil.rollback(conn);
			}
			
			// UPDATE CV
			String inCvPath = "cv7.txt";
			ensureFileExistence(inCvPath);
			try {				
				pstmt = getUpdateCvSQL(conn);				
				updateEmployeeCv(conn, pstmt, 7, inCvPath);
				JDBCUtil.commit(conn);
				System.out.println("Updated employee's cv successfully.");					
			}
			catch(SQLException e) {		
				System.out.println("Updating employee's cv failed: ");
				System.out.println(e.getMessage());
				JDBCUtil.rollback(conn);
			}
			
			// RETRIEVE CV
			String outCvPath = "out_cv7.txt";
			try {				
				retrieveEmployeeCv(conn, 7, outCvPath);
				//retrieveEmployeeCv(conn, 8, outCvPath);
				JDBCUtil.commit(conn);
				System.out.println("Retrieved and saved employee's cv successfully");				
			}
			catch(SQLException e) {
				System.out.println("Retrieving employee's cv failed: ");
				System.out.println(e.getMessage());
				JDBCUtil.rollback(conn);
			}
				
		}
		catch(Exception e) {
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
			else {
				pstmt.setNull(1, Types.BLOB);
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
	
	public static PreparedStatement getUpdateCvSQL(Connection conn) throws SQLException {
		String SQL = "UPDATE employees " +
				"SET cv = ? " +
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
	
	public static void retrieveEmployeePicture(Connection conn,
			int employeeId,
			String picturePath) 
					throws SQLException {
		String SQL = "SELECT employee_id, picture " +
					"FROM employees " +
					"where employee_id = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, employeeId);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				//int empId = rs.getInt("employee_id");
				Blob pictureBlob = rs.getBlob("picture");
				if (pictureBlob != null) {
					savePicture(pictureBlob, picturePath); 
					pictureBlob.free(); // frees the Blob object and releases the resources that it holds
				}	
			}
		}
		catch(IOException | SQLException e) {
			throw new SQLException(e);			
		}
		finally {
			JDBCUtil.closeStatement(pstmt);
		}
	}
	
	public static void savePicture(Blob pictureBlob, String filePath)
			throws SQLException, IOException {
		InputStream in = pictureBlob.getBinaryStream();
		FileOutputStream fos = new  FileOutputStream(filePath);
		int b = -1;
		while ((b = in.read()) != -1) {
			fos.write(b);
		}
		fos.close();		
	}
	
	
	public static void updateEmployeeCv(Connection conn,
			PreparedStatement pstmt,
			int employeeId,
			String cvFilePath) throws SQLException {
		try {			
			if (cvFilePath != null) {
				// We need to create a Clob object first
				Clob cvClob = conn.createClob();
				readInCvData(cvClob, cvFilePath);
				pstmt.setClob(1, cvClob);
			}
			else {
				pstmt.setNull(1, Types.CLOB);
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
	
	// iz txt fajla u clob
	public static void readInCvData(Clob cvClob, String cvFilePath)
			throws FileNotFoundException, IOException, SQLException {
			// Get the character output stream of the Clob object
			// to write the resume data to it.
			int startPosition = 1; // start writing from the beginning
			Writer writer = cvClob.setCharacterStream(startPosition); // za upis u clob
			FileReader fr = new FileReader(cvFilePath); // za citanje txt fajla
			// Read from the file and write to the Clob object
			int b = -1;
			while ((b = fr.read()) != -1) {
				writer.write(b);
			}
			fr.close();
			writer.close();
	}	
	

	public static void retrieveEmployeeCv(Connection conn,
			int employeeId, 
			String cvPath) 
					throws SQLException {
		String SQL = "SELECT employee_id, cv " +
					"FROM employees " +
					"where employee_id = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, employeeId);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {							
				Clob cvClob = rs.getClob("cv");
				if (cvClob != null) {
					saveCv(cvClob, cvPath);
					cvClob.free();
				}	
				// What should I do if the cvClob is null?
				// perhaps it would be better if function returned false
				// in the case there is no cv
				// true otherwise
				//else {
					//System.out.println("There is no cv");
				//}
			}
		}
		catch(IOException | SQLException e) {
			throw new SQLException(e);			
		}
		finally {
			JDBCUtil.closeStatement(pstmt);
		}
	}
	
	
	public static void saveCv(Clob resumeClob, String filePath)
			throws SQLException, IOException {
			FileWriter fw = new FileWriter(filePath); // za upis u txt fajl
			Reader reader = resumeClob.getCharacterStream(); // za citanje clob-a
			int b = -1;
			while ((b = reader.read()) != -1) {
				fw.write((char) b);
			}	
			fw.close();
	}		
}
