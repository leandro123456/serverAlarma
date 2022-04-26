package hello;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestPostgres {
	

	
	//@Test
	public void testConnectToPostgress() {
		 Connection c = null;
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/mosquitto",
	            "postgres", "postgres");
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Connection Opened database successfully");
	   }
	
	//@Test
	public void testSelectPostgres() {
	      Connection c = null;
	      Statement stmt = null;
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/mosquitto",
	            "postgres", "postgres");
	         c.setAutoCommit(false);
	         System.out.println("Opened database successfully");

	         stmt = c.createStatement();
	         ResultSet rs = stmt.executeQuery( "SELECT * FROM acls;" );
	         while ( rs.next() ) {
//	        	 ACL acl= new ACL();
//	        	 acl.setId(rs.getInt("id"));
//	        	 acl.setUsername(rs.getString("username"));
//	        	 acl.setTopic(rs.getString("topic"));
//	        	 acl.setRw(rs.getInt("rw"));	        	
//	        	 System.out.println("name: "+acl.getUsername() +" permition: "+ acl.getRw());
	         }
	         rs.close();
	         stmt.close();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         System.exit(0);
	      }
	      System.out.println("Operation done successfully");
	}
	
	//@Test
	public void testInsertPostgres() {
		String host ="127.0.0.1";
//		acl.setUsername("nuevo");
//		acl.setTopic("#");
//		acl.setRw(4);
//		
		Connection c = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://"+host+":5432/mosquitto","postgres", "postgres");
			c.setAutoCommit(false);
			String sql = "INSERT INTO acls (username,topic,rw)"+ "VALUES (?, ?, ?);";
			PreparedStatement pstmt = c.prepareStatement(sql);
			pstmt.setString(1, "nuevo2");
			pstmt.setString(2, "#");
			pstmt.setInt(3, 3);
			pstmt.executeUpdate();
			pstmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println("desde aca--"+ e.getMessage() +"--hasta aca");
			if(e.getMessage().contains("duplicate key value")) {
				testInsertPostgres();
			}
		}
		System.out.println("ACL created successfully");
	}
	
	//@Test
	public void testInsertUser() {
		String host ="127.0.0.1";
		Connection c = null;
		String pass="sapo1";
		 String text=null;
		try {
			Process process = Runtime.getRuntime().exec("/home/steven/Desktop/mosquitto-go-auth/./pw -p "+pass);
			InputStream inputstream = process.getInputStream();
			BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream);
			
			text = new BufferedReader(new InputStreamReader(bufferedinputstream, StandardCharsets.UTF_8))
					 .lines()
					 .collect(Collectors.joining("\n"));
			 
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://"+host+":5432/mosquitto","postgres", "postgres");
			c.setAutoCommit(false);
			String sql = "INSERT INTO users (username,password_hash,is_admin) VALUES (?,?,?);";
			PreparedStatement pstmt = c.prepareStatement(sql);
			pstmt.setString(1, "sapo");
			pstmt.setString(2, text);
			pstmt.setBoolean(3, false);
			pstmt.execute();
			pstmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			e.printStackTrace();
		}
		System.out.println("Created USER successfully");
	}
	
}
