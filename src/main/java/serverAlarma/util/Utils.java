package serverAlarma.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.DeviceStatus;

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

	public static void CreateUserInMosquittoDB(String mqttUser, String mqttPass,String rootDirection) {
		RestTemplate restTemplate = new RestTemplate();
		//createUser
		restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/userbrocker/create/"+mqttUser + "/"+mqttPass, String.class);
		System.out.println("Created USER successfully");
		//create Acl
		restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/aclbrocker/createfirst/"+mqttUser+"/"+rootDirection, String.class);
		System.out.println("ACL first created successfully");
		//restTemplate.getForEntity(Settings.getInstance().getMyUrl()+"/aclbrocker/update/"+mqttUser+"/"+rootDirection, String.class);
		//System.out.println("ACL created successfully");
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

	public static String CreateToken(String id,String user) {
		try {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);
			byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=");
			Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
			Map<String,Object> map= new HashMap<>();
			map.put("email", user);
			JwtBuilder builder = Jwts.builder().setId(Utils.generateRandomHexa()+"_"+nowMillis+"_"+id)
					.setClaims(map)
					.setIssuedAt(now)
					.setSubject("login_user")
					.setIssuer("coiacaSmart")
					.signWith(signatureAlgorithm, signingKey);

			long expMillis = nowMillis + new Long("10000000");
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
			String token=builder.compact();
			return token;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> ObtaintUserByDeviceID(DeviceParticular device) {
		List<String> result= new ArrayList<>();
		if(!device.getUserowner().equals(""))
			result.add(device.getUserowner());
		try {
			if(device.getUsershared()!=null && !device.getUsershared().equals("") && !device.getUsershared().equals("[]")) {
				String userShared= device.getUsershared().replace("[", "");
				userShared=userShared.replace("]", "");
				String[]userparse= userShared.split(",");
				if(userparse.length>0) {
					for(int i=0; i>userparse.length;i++) {
						if(!userparse[i].equals(""))
							result.add(userparse[i]);
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("error in ObtaintUserByDeviceID");
		}
		return result;
	}

	public static String obtainTopicKey(String[] topicP) {
		String topicKey="";
		try {
			for(int i=2; i<topicP.length; i++) { 
				topicKey=topicKey+topicP[i]+"-";
			}
			if(topicKey.endsWith("-")) {
				topicKey=topicKey.substring(0, topicKey.length()-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topicKey;
	}

	public static DeviceStatus updateDeviceStatusValues(DeviceParticular devParticular, DeviceStatus devStatus,
			String msg, String topic, String deviceId) {
		if(topic.equals(devParticular.getTopicActPartition())) {
			devStatus.setActPartition(Integer.parseInt(msg));
		}
		if(topic.equals(devParticular.getTopicTrouble())) {
			devStatus.setTrouble(Integer.parseInt(msg));
		}
		if(topic.equals(devParticular.getTopicStatus())) {
			devStatus.setStatus(msg);
		}
		String topicKey="";
		if(deviceId!=null) {
			topicKey=topic.replace(deviceId, "");
			topicKey=topicKey.replaceAll("/", "");
		}else {
			String[] topicP=topic.split("/");
			topicKey=Utils.obtainTopicKey(topicP);			
		}
		//System.out.println(new Date().toString() + " - topicKey: "+ topicKey);
		
		JSONObject jsonData = null;
		if(devStatus.getImportData()!=null && !devStatus.getImportData().isEmpty()) {
			jsonData=new JSONObject(devStatus.getImportData());
			jsonData.remove(topicKey);
			jsonData.put(topicKey, msg);
		}else {
			jsonData=new JSONObject();
			jsonData.put(topicKey, msg);
		}
		devStatus.setImportData(jsonData.toString());
		return devStatus;
	}
}