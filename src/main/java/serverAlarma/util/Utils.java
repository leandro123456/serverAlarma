package serverAlarma.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

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

	public static void CreateUserInMosquittoDB(String newDeviceId, String mqttUser, String mqttPass) {
		RestTemplate restTemplate = new RestTemplate();
		//createUser
		restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/userbrocker/create/"+mqttUser + "/"+mqttPass, String.class);
		System.out.println("Created USER successfully");
		//create Acl
		restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/aclbrocker/createfirst/"+mqttUser, String.class);
		System.out.println("ACL first created successfully");
		restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/aclbrocker/update/"+mqttUser+"/"+newDeviceId, String.class);
		System.out.println("ACL created successfully");
	}
	
	public static void UpdateTopicsToUser(String mqttUser  , String newDeviceId) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/aclbrocker/update/"+mqttUser+"/"+newDeviceId, String.class);
		System.out.println("ACL created successfully");
	}
	
	
	public static String hashPassword(String mqttPass) {
		//local
//		Process process = Runtime.getRuntime().exec("/home/mosquitto-go-auth/./pw -p "+mqttPass);
//		InputStream inputstream = process.getInputStream();
//		BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream);
//		passHashed = new BufferedReader(new InputStreamReader(bufferedinputstream, StandardCharsets.UTF_8))
//				 .lines().collect(Collectors.joining("\n"));
		
		Session session = null;
		ChannelExec channel = null;
		try {
			session = new JSch().getSession("root", "161.35.254.222");
			session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String commando= "/home/mosquitto-go-auth/./pw -p "+mqttPass;
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(commando);
			ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			channel.setOutputStream(responseStream);
			channel.connect();
			
			
			InputStream inputstream = channel.getInputStream();
			BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream);
			
			String passHashed = new BufferedReader(new InputStreamReader(bufferedinputstream, StandardCharsets.UTF_8))
					 .lines()
					 .collect(Collectors.joining("\n"));
			
	
			while (channel.isConnected()) {
				Thread.sleep(100);
			}
			session.disconnect();
			return passHashed;
	
		}catch (Exception e) {
			if (session != null) 
				session.disconnect();
			if (channel != null) 
				channel.disconnect();
		}
		return null;
	}
}