package serverAlarma.Persistence.Postgresql.Controller;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import serverAlarma.Controller.MailController;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserAlarm;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.UserAlarm;
import serverAlarma.util.EncryptorPassword;
import serverAlarma.util.Settings;
import serverAlarma.util.Utils;

@CrossOrigin(origins = "*")
@RestController
public class UserAlarmController {

	@Autowired
	private IUserAlarm iuserAlarm;
	
	@Autowired
	private IDeviceParticular idevice;
	
	@GetMapping("/useralarm/getall")
	public List<UserAlarm> getAllUsers(){
		return iuserAlarm.getAllUserAlarm();
	}
	
	
	@PostMapping(value="/useralarm/register")
	public String RegisterDeviceRoot(@RequestBody String jsoninfo) {
		JSONObject jsonRequest = new JSONObject(jsoninfo);
		try {
			String passvalid=jsonRequest.getString("vrf");
			if(!EncryptorPassword.decrypt(passvalid).equals("MENSAJElaqueva")) {
				JSONObject json= new JSONObject();
				json.put("devid", "password-validation-invalid");
				json.put("mqttusr", "");
				json.put("mqttpwd", "");
				return json.toString();
			}	
		} catch (Exception e) {
			JSONObject json= new JSONObject();
			json.put("devid", "password-validation-wrong");
			json.put("mqttusr", "");
			json.put("mqttpwd", "");
			return json.toString();
		}
		
		String email= jsonRequest.getString("mail");
		String devid= jsonRequest.getString("devid");
		String passCuenta= jsonRequest.getString("pass");
		String type = jsonRequest.getString("type");
		
		UserAlarm user= iuserAlarm.findUserAlarmByEmail(email);
		RestTemplate restTemplate = new RestTemplate();
		if(user==null) {
			//Create User
			user= new UserAlarm();
			user.setEmail(email);
			user.setPassCuenta(passCuenta);
			Date date=new Date();
			user.setFechaCreacion(date.toString());
			String userID=Utils.generateRandomHexa();
			user.setUserID(userID);
			String newDeviceId = restTemplate.getForObject(Settings.getInstance().getMyUrl()+"/device/deviceid/"+type, String.class);
			DeviceParticular dev= new DeviceParticular();
			dev.setDeviceid(newDeviceId);
			dev.setType(type);
			dev.setUserowner(email);
			idevice.saveDeviceParticular(dev);
			
			String mqttUser=Utils.generateRandomHexa();
			user.setMqttUser(mqttUser);
			String mqttPass=Utils.generateRandomHexaPass();
			user.setMqttPassword(mqttPass);
			iuserAlarm.saveUserAlarm(user);
			
			//enviar mensaje por mail
			try {
				MailController.enviarNotificacion(userID,newDeviceId,mqttUser,mqttPass,email);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Create MQTT user					
			Utils.CreateUserInMosquittoDB(newDeviceId,mqttUser,mqttPass);
			System.out.println("Actualizo Mosquitto");
			
			JSONObject json= new JSONObject();
			json.put("devid", newDeviceId);
			json.put("mqttusr", mqttUser);
			json.put("mqttpwd", mqttPass);
			
			return json.toString();
		}
		else {
			System.out.println("El usuario ya existe - verifico el password");
			if(user.getPassCuenta().equals(passCuenta)) {
				//si el dispositivo es nuevo
				if(devid==null || devid.contains("empty")) {
					System.out.println("entro en el nuevo dispositivo");
					String newDeviceId = restTemplate.getForObject(Settings.getInstance().getMyUrl()+"/device/deviceid/"+type, String.class);
					DeviceParticular dev= new DeviceParticular();
					dev.setDeviceid(newDeviceId);
					dev.setType(type);
					dev.setUserowner(email);
					idevice.saveDeviceParticular(dev);
					
					//enviar mensaje por mail
					try {
						MailController.enviarNotificacion(user.getUserID(),newDeviceId,user.getMqttUser(),user.getMqttPassword(),email);
					} catch (Exception e) {
						e.printStackTrace();
					}
				
					//Update MQTT user
					Utils.UpdateTopicsToUser(user.getMqttUser(),newDeviceId);
					
					JSONObject json= new JSONObject();
					json.put("devid", newDeviceId);
					json.put("mqttusr", user.getMqttUser());
					json.put("mqttpwd", user.getMqttPassword());
					return json.toString();
				}else {
				    JSONObject jsonRequestvalue = new JSONObject();
				    jsonRequestvalue.put("mail", email);
					String listOwnerDevices = restTemplate.postForObject(Settings.getInstance().getMyUrl()+"/useralarm/getdevices",jsonRequestvalue.toString(), String.class);
					if(!listOwnerDevices.contains(devid)) {
						JSONObject json= new JSONObject();
						json.put("devid", "invalid-deviceid");
						json.put("mqttusr", "");
						json.put("mqttpwd", "");
						return json.toString();
					}
					//El dispositivo ya existia
					System.out.println("El DISPOSITIVO ya existia no cambio nada");
					JSONObject json= new JSONObject();
					json.put("devid", devid);
					json.put("mqttusr", user.getMqttUser());
					json.put("mqttpwd", user.getMqttPassword());
					return json.toString();
				}
				
			}else {
				System.out.println("Las password no coincide con lo esperado");
				try {
					String mensaje= "The email address is already registered on the platform.... Error in the password sent, it does not match the registered password: "+ passCuenta;
					MailController.sendMail(mensaje,email,"");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				JSONObject json= new JSONObject();
				json.put("devid", "wrong-password");
				json.put("mqttusr", "");
				json.put("mqttpwd", "");
				return json.toString();
			}
		}
	}
	
	@GetMapping("/useralarm/findbydevid/{devid}")
	public String RegisterDevice(@PathVariable("devid") String devid) {
		DeviceParticular dev= idevice.findByDeviceid(devid);
		if(dev==null)
			return "failed device dont exist";
		UserAlarm user= iuserAlarm.findUserAlarmByEmail(dev.getUserowner());
		if(user==null)
			return null;
		String result= user.getUserID();
		return result;
	}
}
