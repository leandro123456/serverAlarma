package serverAlarma.Persistence.Postgresql.Controller;

import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import serverAlarma.Controller.MailController;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceStatus;
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
	
	@GetMapping("/usermanagement/getall")
	public List<UserAlarm> getAllUsers(){
		return iuserAlarm.getAllUserAlarm();
	}
	
	@GetMapping("/usermanagment/getfirst")
	public UserAlarm getFirstUsers(){
		return iuserAlarm.getAllUserAlarm().get(0);
	}
	
	@PostMapping("/usermanagment/getone")
	public UserAlarm getOneUsers(@RequestBody String info){
		System.out.println("user email: "+ info);
		UserAlarm user=iuserAlarm.findUserAlarmByEmail(info);
		if(user!=null)
		System.out.println("user obtained: "+ user.getEmail());
		else {
			user= new UserAlarm();
			user.setEmail("empty");
			System.out.println("user is null");
		}
		System.out.println(user.getEmail());
		return user;
	}
	
	@PostMapping("/usermanagment/updatemqttparams")
	public String updateMqttParamas(@RequestBody String info){
		System.out.println("updatemqtt: "+info);
		JSONObject jsonResponse = new JSONObject();
		try {
			JSONObject jsonRequest = new JSONObject(info);
			String email= jsonRequest.getString("email");
			UserAlarm user= iuserAlarm.findUserAlarmByEmail(email);
			if(user!=null) {
				user.setMqttUrl(jsonRequest.getString("mqttUrl"));
				user.setMqttPort(jsonRequest.getInt("mqttPort")+"");
				user.setMqttUser(jsonRequest.getString("mqttUser"));
				user.setMqttIsSecure(jsonRequest.getBoolean("mqttIsSecure"));
				user.setMqttPassword(jsonRequest.getString("mqttPassword"));
				iuserAlarm.saveUserAlarm(user);
				jsonResponse.put("result", "ok");
			}
			else {
				jsonResponse.put("result", "Failed - user dont exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonResponse.put("result", "Server Error-"+e.getMessage());
		}
		return jsonResponse.toString();
	}
	
	@GetMapping("/test/userauth/create")
	public String createUserv2(){
		RestTemplate restTemplate = new RestTemplate();
		try {
			UserAlarm user= new UserAlarm();
			user.setEmail("leandroplay@gmail.com");
			user.setPassCuenta("cin");
			Date date=new Date();
			user.setFechaCreacion(date.toString());
			String userID=Utils.generateRandomHexa();
			user.setMqttUserID(userID);
			String newDeviceId = restTemplate.getForObject(Settings.getInstance().getMyUrl()+"/device/deviceid/"+"DSC01", String.class);
			
			String mqttUser=Utils.generateRandomHexa();
			user.setMqttUser("mqttusr");
			String mqttPass=Utils.generateRandomHexaPass();
			user.setMqttPassword("mqttpwd");
			user.setMqttIsSecure(false);
			user.setMqttPort("1883");
			user.setMqttUrl("remota");
					
			List<String> IdDevices= new ArrayList<>();
			IdDevices.add("DSC0100000010");
			IdDevices.add("mydeviceID");
			IdDevices.add(newDeviceId);
			user.setDevices(IdDevices.toString());
			iuserAlarm.saveUserAlarm(user);
			
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@PostMapping(value="/useralarm/login")
	public String createJWT(@RequestBody String jsoninfo) {
		JSONObject jsonResponse = new JSONObject();
		try {
			JSONObject jsonRequest = new JSONObject(jsoninfo);
			String email=jsonRequest.getString("email");
			String pass=jsonRequest.getString("password");
			UserAlarm user=iuserAlarm.findUserAlarmByEmail(email); 
			if(user!=null) {
				System.out.println();
				System.out.println();
				if(pass.equals(user.getPassCuenta())){
					String token=Utils.CreateToken(user.getMqttUserID(), user.getEmail());
					if(token!=null) {
						jsonResponse.put("token", token);
						jsonResponse.put("is_active", user.getCuenta_iniciada());
						jsonResponse.put("code_active",user.getPassword());
						jsonResponse.put("result", "ok");
					}else {
						jsonResponse.put("token", "empty");
						jsonResponse.put("result", "Falied - Server Reject contact to support area support@coiaca.com");
					}
					
					jsonResponse.put("mail", user.getEmail());
				}
				else {
					jsonResponse.put("token", "empty");
					jsonResponse.put("result", "wronPassword");
					jsonResponse.put("mail", user.getEmail());
				}
			}else {
				jsonResponse.put("result", "Failed - User does exist");
				jsonResponse.put("token", "empty");
			}
			return jsonResponse.toString();
		} catch (Exception e) {
			e.printStackTrace();
			jsonResponse.put("result", "Failed - Contact the support area support@coiaca.com");
			jsonResponse.put("token", "empty");
			jsonResponse.put("mail", "empty");
			return jsonResponse.toString();
		}
	}
	
	@PostMapping(value="/usermanagment/updatepassword")
	public String updateUserPAssword(@RequestBody String jsoninfo) {
		JSONObject jsonResponse = new JSONObject();
		try {
			JSONObject jsonRequest = new JSONObject(jsoninfo);
			String email=jsonRequest.getString("email");
			String pass=jsonRequest.getString("password");
			UserAlarm user=iuserAlarm.findUserAlarmByEmail(email); 
			if(user!=null) {
				user.setPassCuenta(pass);
				iuserAlarm.saveUserAlarm(user);
				jsonResponse.put("result", "ok");
			}
			else {
				jsonResponse.put("result", "Failed - User does exist");
			}
			return jsonResponse.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			jsonResponse.put("result", "Failed - Contact the support area support@coiaca.com");
			return jsonResponse.toString();
		}
	}
	
	
	@PostMapping(value="/useralarm/register")
	public String RegisterDeviceRoot(@RequestBody String jsoninfo) {
		System.out.println("llego register: "+ jsoninfo);
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
			//create DeviceID
			String newDeviceId = restTemplate.getForObject(Settings.getInstance().getMyUrl()+"/device/deviceid/"+type, String.class);
			
			//Create User
			Map<String, String>result=createUser(email,passCuenta,newDeviceId,false);
			String userID=result.get("userID");
			String mqttUser=result.get("mqttUser");
			String mqttPass=result.get("mqttPass");
			
			//Create Device
			DeviceParticular dev= new DeviceParticular();
			dev.setDevid(newDeviceId);
			dev.setTipo(type);
			dev.setUserowner(email);
			idevice.saveDeviceParticular(dev);
			
			//enviar mensaje por mail
			try { MailController.enviarNotificacion(userID,newDeviceId,mqttUser,mqttPass,email);
			} catch (Exception e) {e.printStackTrace();}
			
			//Create MQTT user					
			Utils.CreateUserInMosquittoDB(mqttUser,mqttPass,userID);
			System.out.println("Actualizo Mosquitto");
			
			JSONObject json= new JSONObject();
			json.put("devid", newDeviceId);
			json.put("rootinfo", userID);
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
					dev.setDevid(newDeviceId);
					dev.setTipo(type);
					dev.setUserowner(email);
					idevice.saveDeviceParticular(dev);
					
					//enviar mensaje por mail
					try {
						MailController.enviarNotificacion(user.getMqttUserID(),newDeviceId,user.getMqttUser(),user.getMqttPassword(),email);
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
	
	
	@PostMapping(value="/useralarm/registerapp")
	public String RegisterDeviceRootApp(@RequestBody String jsoninfo) {
		System.out.println("llego register app: "+ jsoninfo);
		JSONObject jsonRequest = new JSONObject(jsoninfo);
		JSONObject jsonResponse= new JSONObject();
		try {
			try {
				String passvalid=jsonRequest.getString("vrf");
				if(!EncryptorPassword.decrypt(passvalid).equals("MENSAJElaqueva")) {
					JSONObject json= new JSONObject();
					json.put("result", "Password Validation Invalid...contact the support area support@coiaca.com");
					return json.toString();
				}	
			} catch (Exception e) {
				JSONObject json= new JSONObject();
				json.put("result", "Validation Invalid...contact the support area support@coiaca.com");
				return json.toString();
			}
			
			String email= jsonRequest.getString("mail");
			String passCuenta= jsonRequest.getString("pass");
			UserAlarm user= iuserAlarm.findUserAlarmByEmail(email);
			if(user==null) {
				Boolean isApp=jsonRequest.getBoolean("isApp");
				if(isApp) {
					try {
						//Create User
						Map<String, String>result=createUser(email,passCuenta,null, isApp);
						String code=result.get("initcode");
						
						//enviar codigo por mail
						try {
							MailController.MensajeBievenidaApp( email,code);
						} catch (Exception e) {	e.printStackTrace();}
						
						//Create Response
						JSONObject json= new JSONObject();
						json.put("result", "ok");
						return json.toString();
					} catch (Exception e) {
						e.printStackTrace();
						//Create Response
						JSONObject json= new JSONObject();
						json.put("result", e.getMessage());
						return json.toString();
					}
				}
				else {
					jsonResponse.put("result", "Internal Validation Failed...contact the support area support@coiaca.com");
					return jsonResponse.toString();
				}

			}
			else {
				jsonResponse.put("result", "User Already Exist...contact the support area support@coiaca.com");
				return jsonResponse.toString();
			}
		} catch (Exception e) {
			jsonResponse.put("result", "Internal Error...please contact the support area support@coiaca.com");
			return jsonResponse.toString();
		}
	}
	
	@PostMapping(value="/useralarm/activateapp")
	public String ActivateCount(@RequestBody String info) {
		JSONObject jsonRequest = new JSONObject(info);
		JSONObject jsonResponse= new JSONObject();
		try {
			String email= jsonRequest.getString("email");
			String initCode=jsonRequest.getString("code");
			UserAlarm user=iuserAlarm.findUserAlarmByEmail(email);
			if(user!=null) {
				if(initCode.equals(user.getPassword())) {
					user.setCuenta_iniciada(true);
					iuserAlarm.saveUserAlarm(user);
					//enviar mensaje por mail
					try { MailController.enviarNotificacion(user.getMqttUserID(),null,user.getMqttUser(),user.getMqttPassword(),email);
					} catch (Exception e) {e.printStackTrace();}
					//Create MQTT user					
					Utils.CreateUserInMosquittoDB(user.getMqttUser(),user.getMqttPassword(),user.getMqttUserID());
					System.out.println("Actualizo Mosquitto");
					jsonResponse.put("result", "ok");
				}
				else {
					jsonResponse.put("result", "user code incorrect");
				}
				return jsonResponse.toString();
			}
			else {
				jsonResponse.put("result", "user does not exist");
				return jsonResponse.toString();
			}
	
		} catch (Exception e) {
			jsonResponse.put("result", "password-wrong");
			return jsonResponse.toString();
		}
		
	}
	
	private Map<String, String> createUser(String email, String passCuenta, String newDeviceId, Boolean isApp) {
		HashMap<String,String> result= new HashMap<>();
		UserAlarm user= new UserAlarm();
		user.setEmail(email);
		user.setPassCuenta(passCuenta);
		Date date=new Date();
		user.setFechaCreacion(date.toString());
		String userID=Utils.generateRandomHexa();
		user.setMqttUserID(userID);
		result.put("userID", userID);
		String mqttUser=Utils.generateRandomHexa();
		user.setMqttUser(mqttUser);
		result.put("mqttUser", mqttUser);
		String mqttPass=Utils.generateRandomHexaPass();
		user.setMqttPassword(mqttPass);
		result.put("mqttPass", mqttPass);
		if(newDeviceId!=null) {
			String devices="["+newDeviceId+"]";
			user.setDevices(devices);
		}
		if(isApp) {
			user.setCuenta_iniciada(false);
			String initCode=Utils.generateRandomHexa();
			user.setPassword(initCode);
			result.put("initcode", initCode);
			user.setMqttIsSecure(false);
			user.setMqttUrl(Settings.getInstance().getBrokerUrl());
			user.setMqttPort(Settings.getInstance().getBrokerIp());
			user.setMqttIsSecure(Settings.getInstance().getBrokerIsSecure());
		}
		iuserAlarm.saveUserAlarm(user);
		
		return result;
	}

	@GetMapping("/useralarm/findbydevid/{devid}")
	public String RegisterDevice(@PathVariable("devid") String devid) {
		DeviceParticular dev= idevice.findByDevid(devid);
		if(dev==null)
			return "failed device dont exist";
		UserAlarm user= iuserAlarm.findUserAlarmByEmail(dev.getUserowner());
		if(user==null)
			return null;
		String result= user.getMqttUserID();
		return result;
	}
	
	@PostMapping(value="/useralarm/forgotpassword")
	public String ForgotPasswordUser(@RequestBody String jsoninfo) {
		System.out.println("llego forgot pass: "+ jsoninfo);
		JSONObject jsonRequest = new JSONObject(jsoninfo);
		JSONObject jsonResponse= new JSONObject();
		try {
			String email=jsonRequest.getString("email");
			UserAlarm user= iuserAlarm.findUserAlarmByEmail(email);
			if(user!=null) {
				try {
					String newPass=Utils.generateRandomHexaPass();
					user.setPassCuenta(newPass);
					iuserAlarm.saveUserAlarm(user);
					String mensaje= "This is the temporary password for the account, once started change it from the configuration section: "+ newPass;
					MailController.sendMail(mensaje,email,"");
					jsonResponse.put("response", "ok");
					return jsonResponse.toString();
				} catch (Exception e) {
					e.printStackTrace();
					jsonResponse.put("response", "Failed - The mail could not be sent... contact the support area support@coiaca.com");
					return jsonResponse.toString();
				}
			}else {
				jsonResponse.put("response", "Failed - There is no registered user with this email");
				return jsonResponse.toString();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			JSONObject json= new JSONObject();
			json.put("response", e.getMessage());
			return json.toString();
			
		}
	}
	
}
