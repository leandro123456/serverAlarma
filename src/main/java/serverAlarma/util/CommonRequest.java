package serverAlarma.util;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;

public class CommonRequest {
	public static DeviceParticular ObtainDeviceNameByDeviceID(String deviceId) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			DeviceParticular device = restTemplate.getForObject(Settings.getInstance().getMyUrl()+"/devicemanagement/findbydevid/"+deviceId , DeviceParticular.class);
			return device;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static String SendUpdateDeviceStatusRequest(String deviceId, String topic, String msg) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    JSONObject json = new JSONObject();
		    json.put("topico",topic);
		    json.put("payload", msg);
		    json.put("deviceid", deviceId);
		    //System.out.println(json.toString());
		    HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
			String result = restTemplate.postForObject(Settings.getInstance().getMyUrl()+"/devicestatus/update",request, String.class);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
