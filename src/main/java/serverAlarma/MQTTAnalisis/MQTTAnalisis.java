package serverAlarma.MQTTAnalisis;

import java.util.Base64;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.V1Analisis.V1UpdateDatabaseStatus;
import serverAlarma.util.CommonRequest;
import serverAlarma.util.Settings;
import serverAlarma.util.Utils;

public class MQTTAnalisis {

	public static void VerifyMsg(String topic, MqttMessage message) {
		if(topic.startsWith("DSC01000")) {
			V1UpdateDatabaseStatus.UpdateForTopic(topic,new String(message.getPayload()));
		}else {
			String[]topico= topic.split("/");				
			if(topico[1].contains("DSC01") && topico[2].contains("Partition") && !topico[2].contains("activePartition")) {
				System.out.println("topico: "+ topico[1]);
				String msg= new String(message.getPayload());
				if(!msg.equals("pending")) {
					DeviceParticular device= CommonRequest.ObtainDeviceNameByDeviceID(topico[1]);
					if(device!=null) {
						List<String> users= Utils.ObtaintUserByDeviceID(device);
						if(!users.isEmpty()) {
							for(String userEmail: users) {
								List<String> phones= ObtainPhonesByUser(userEmail);
								if(phones!=null && !phones.isEmpty()) {
									for(String phone :phones) {
										sendFirebaseNotfication(phone,device.getDeviceName(),msg);
									}
								}
							}
						}
					}
					else {
						System.out.println("device null");
					}
				}
			}
		}
	}

	private static void sendFirebaseNotfication(String phone, String deviceName,String payload) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    JSONObject json = new JSONObject();
		    json.put("payload", payload);
		    json.put("devicename", deviceName);
		    json.put("to", phone);
		    HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
			String result=restTemplate.postForObject(Settings.getInstance().getMyUrl()+"/firebase/singlenotification",request,String.class);
			System.out.println("firebase notification response: "+ result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static List<String> ObtainPhonesByUser(String userEmail) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String emailCoded=new String(Base64.getEncoder().encode(userEmail.getBytes()));
			List<String> phones = restTemplate.getForObject(Settings.getInstance().getMyUrl()+"/userphone/createlist/"+emailCoded , List.class);
			return phones;
		} catch (Exception e) {
			return null;
		}
	}



}
