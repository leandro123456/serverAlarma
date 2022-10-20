package serverAlarma.FirebaseNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import serverAlarma.Persistence.Postgresql.JPA.Extension.DeviceParticularRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserPhone;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.UserPhone;
import serverAlarma.util.Settings;

@CrossOrigin(origins = "*")
@RestController
public class SendFirebaseNotfication {
	@Autowired
	private IDeviceParticular idevice;
	@Autowired
	private IUserPhone iuserphone;

	private String myKey="key=AAAA61YobUY:APA91bGEN7McZKun4lD1Ewr2lpCTA9uT4RtQ3pz0XhO1H7U4_JJofnA-zX8z6_btJCaClW0ko2SMk3ZfgsWDVLLZ_CPDQ7ipsrINUvHn6CrGRWV84Gc6KWZy90nVnJt81QbDeNubRb36";
	
	@PostMapping("/firebase/notify")
	public String SendNotificationToPhones(@RequestBody String jsoninfo) {
		JSONObject jsonRequest = new JSONObject(jsoninfo);
		String topic=jsonRequest.getString("topic");
		String payload=jsonRequest.getString("payload");
		String[] information= topic.split("/");
		if(information.length<=0) {
			return "failed";
		}
		String deviceid=information[1];
		System.out.println(deviceid);
		DeviceParticular device= idevice.findByDevid(deviceid);
		if(device!=null) {
			List<String>phones=  ObtainPhones(device.getUserowner());
			//TODO userShared
			device.getUsershared();
			for(String to: phones) {
				SendNoficationByFirebase(device.getDeviceName(), payload, to);
			}
			return "success";
		}
		else {
			System.out.println("device null");
			return "failed";
		}
	}
	
	@PostMapping("/firebase/singlenotification")
	public String SendNotificationToOnePhone(@RequestBody String jsoninfo) {
		try {
			JSONObject jsonRequest = new JSONObject(jsoninfo);
			String to=jsonRequest.getString("to");
			String payload=jsonRequest.getString("payload");
			String devicename=jsonRequest.getString("devicename");
			SendNoficationByFirebase(devicename, payload, to);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
	

	private void SendNoficationByFirebase(String deviceName, String payload, String to) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", myKey);
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String payloadFormated=payloadFormated(payload);
	    JSONObject json = new JSONObject();
	    json.put("priority","high");
	    json.put("to", to);
	    JSONObject jsonNotif= new JSONObject();
	    jsonNotif.put("title","Cdash Notification");
	    jsonNotif.put("body", "Device "+deviceName+" change to "+payloadFormated);
	    json.accumulate("notification", jsonNotif);
	    JSONObject jsonData= new JSONObject();
	    jsonData.put("status", payloadFormated);
	    json.accumulate("data", jsonData);
	    System.out.println(json.toString());
	    HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String msgResult = restTemplate.postForObject(Settings.getInstance().getFirebaseUrl(),request ,String.class);
		System.out.println(msgResult);
		
	}

	private String payloadFormated(String payload) {
		String result="";
		if(payload.equals("disarmed"))
			result="Alarm Disarmed";
		else if(payload.equals("armed_home"))
			result="Alarm Armed Home";
		else if(payload.equals("armed_away"))
			result="Alarm Armed Away";
		else if(payload.equals("armed_nite"))
			result="Alarm Armed Night";
		else 
			result=payload;
		return result;
		
	}

	private List<String> ObtainPhones(String email) {
		List<UserPhone> phonesRegistered = iuserphone.findAllByEmail(email);
		List<String> result= new ArrayList<>();
		for(UserPhone phone: phonesRegistered) {
			result.add(phone.getPhone());
		}
		return result;
	}
}
