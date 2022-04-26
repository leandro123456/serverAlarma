package serverAlarma.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import serverAlarma.Persistence.DAO.DeviceDAO;
import serverAlarma.Persistence.Model.Device;

public class Utils {
	
	public static String generateRandomHexa(){
		String[] letters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		String color = "";
		for (int i = 0; i < 8; i++ ) {
		    color += letters[(int) Math.round(Math.random() * 15)];
		}
		return color;
	}

	public static String generateRandomHexaPass() {
		String[] letters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		String color = "";
		for (int i = 0; i < 16; i++ ) {
		    color += letters[(int) Math.round(Math.random() * 15)];
		}
		return color;
	}




	public static void UploadUserInMosquitto(String mqttUser, String mqttPass) {
		System.out.println("Start save New user");
		Boolean wasOk=false;
		Session session = null;
		ChannelExec channel = null;
		try {
			session = new JSch().getSession("root", "161.35.254.222");
			session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String commando= "mosquitto_passwd -b /etc/mosquitto/passwd "+mqttUser +" "+mqttPass;
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(commando);
			ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			channel.setOutputStream(responseStream);
			channel.connect();
	
			while (channel.isConnected()) {
				Thread.sleep(100);
			}
			session.disconnect();
			wasOk=true;
	
		}catch (Exception e) {
			if (session != null) 
				session.disconnect();
			if (channel != null) 
				channel.disconnect();
			wasOk=false;
		} 
	
		if(wasOk) {
			System.out.println("Restart Service");
			try {
				String commando= "systemctl restart mosquitto";
				JSch jsch = new JSch();
				session = jsch.getSession("root", "161.35.254.222");
				session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
				java.util.Properties config = new java.util.Properties(); 
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(commando);
				ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
				channel.setOutputStream(responseStream);
				channel.connect();
	
				while (channel.isConnected()) {
					Thread.sleep(100);
				}
				session.disconnect();
	
			}catch (Exception e) {
				if (session != null) 
					session.disconnect();
				if (channel != null) 
					channel.disconnect();
			} 
			System.out.println("Restart Service done");
		}
	}

	public static String ObtainDeviceID(String type) {
		DeviceDAO devicedao= new DeviceDAO();
		Device device=devicedao.retrieveFirstbyType(type);
		if(device==null) {
			device=new Device();
			device.setName("primero");
			device.setSerialnumber(type+"0000000100");
			device.setUserowner(type+"usuario-inicial");
			device.setType(type);
			devicedao.create(device);
		}
		Integer number= Integer.parseInt(device.getSerialnumber().replace(type, ""));
		String newDeviceId= (number+1)+"";
		newDeviceId=device.getSerialnumber().substring(0,device.getSerialnumber().length()-newDeviceId.length())+newDeviceId;
		System.out.println("New Device id: "+newDeviceId);
		device.setSerialnumber(newDeviceId);
		devicedao.update(device);
		return newDeviceId;
	}

	public static void CreateUserInMosquittoDB(String newDeviceId, String mqttUser, String mqttPass) {
		String host ="127.0.0.1";
		Connection c = null;
		String passHashed=null;
		try {
			Process process = Runtime.getRuntime().exec("/home/steven/Desktop/mosquitto-go-auth/./pw -p "+mqttPass);
			InputStream inputstream = process.getInputStream();
			BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream);
			
			passHashed = new BufferedReader(new InputStreamReader(bufferedinputstream, StandardCharsets.UTF_8))
					 .lines()
					 .collect(Collectors.joining("\n"));
			 
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://"+Settings.getInstance().getMqttDBurl()+"/"+
					Settings.getInstance().getMqttDBName(),Settings.getInstance().getMqttDBUser(),
					Settings.getInstance().getMqttDBPass());
			c.setAutoCommit(false);
			String sql = "INSERT INTO users (username,password_hash,is_admin) VALUES (?,?,?);";
			PreparedStatement pstmt = c.prepareStatement(sql);
			pstmt.setString(1, mqttUser);
			pstmt.setString(2, passHashed);
			pstmt.setBoolean(3, false);
			pstmt.execute();
			pstmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			if(e.getMessage().contains("duplicate key value")) 
				CreateUserInMosquittoDB(newDeviceId,mqttUser,mqttPass);
		}
		System.out.println("Created USER successfully");
		
		CreateFirstACL(mqttUser);
		CreateACL(mqttUser,newDeviceId);
	}
	
	
	private static void CreateFirstACL(String mqttUser) {
		Connection c = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://"+Settings.getInstance().getMqttDBurl()+"/"+
					Settings.getInstance().getMqttDBName(),Settings.getInstance().getMqttDBUser(),
					Settings.getInstance().getMqttDBPass());
			c.setAutoCommit(false);
			String sql = "INSERT INTO acls (username,topic,rw)"+ "VALUES (?, ?, ?);";
			PreparedStatement pstmt = c.prepareStatement(sql);
			pstmt.setString(1, mqttUser);
			pstmt.setString(2,"homeassistant/#");
			pstmt.setInt(3, 2);
			pstmt.executeUpdate();
			pstmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			if(e.getMessage().contains("duplicate key value"))
				CreateFirstACL(mqttUser);
		}
		System.out.println("ACL created successfully");
		
	}
	

	public static void CreateACL(String mqttUser, String newDeviceId) {
		Connection c = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://"+Settings.getInstance().getMqttDBurl()+"/"+
					Settings.getInstance().getMqttDBName(),Settings.getInstance().getMqttDBUser(),
					Settings.getInstance().getMqttDBPass());
			c.setAutoCommit(false);
			String sql = "INSERT INTO acls (username,topic,rw)"+ "VALUES (?, ?, ?);";
			PreparedStatement pstmt = c.prepareStatement(sql);
			pstmt.setString(1, mqttUser);
			pstmt.setString(2, newDeviceId+"/#");
			pstmt.setInt(3, 4);
			pstmt.executeUpdate();
			pstmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			if(e.getMessage().contains("duplicate key value"))
				CreateACL(mqttUser,newDeviceId);
		}
		System.out.println("ACL created successfully");
		
	}

}