package serverAlarma.Controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.ui.context.support.UiApplicationContextUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import serverAlarma.util.Settings;
import serverAlarma.util.Utils;
import serverAlarma.Configuration.HomeAssistanConfig;
import serverAlarma.Persistence.DAO.DeviceDAO;
import serverAlarma.Persistence.DAO.UserDAO;
import serverAlarma.Persistence.Model.Device;
import serverAlarma.Persistence.Model.UserAlarm;

public class MqttConnect implements MqttCallback{
	private static MqttConnect mqttconnect= null;
	private MqttClient client;
	private UserDAO userdao=new UserDAO();
	private DeviceDAO devicedao= new DeviceDAO();
	
	private MqttConnect(){
		this.iniciar();
	}
	
	public static MqttConnect getInstance(){
		if(mqttconnect==null){
			System.out.println("mqttconnect instancia es null");
			mqttconnect=new MqttConnect();
		}
		return mqttconnect;
	}
	
	public void iniciar(){
		String publisherId = UUID.randomUUID().toString();
		try {
			MqttClient publisher = new MqttClient(Settings.getInstance().getUriBroker(),publisherId,new MemoryPersistence());
			publisher.setCallback(this);
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setWill("ServerAlarm/Status", "offline".getBytes(), 0, true);
			options.setUserName(Settings.getInstance().getUserNameBroker());
			options.setPassword(Settings.getInstance().getPasswordBroker().toCharArray());
			if ( !publisher.isConnected()) {
	           	System.out.println("MQTT-client no esta conectado");
	           	publisher.connect(options);
	           	this.client =publisher;
	           	sendServerStatus(client, "online");
	           	if(!publisher.isConnected())
	           		iniciar();
	        }else {
	        	System.out.println("MQTT-client conecto a :" + publisher);
	        	this.client =publisher;
	        }
		} catch (Exception e) {
			System.out.println("mensaje: "+ e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void sendServerStatus(MqttClient client,String payload) throws MqttException {
		MqttMessage message = new MqttMessage(payload.getBytes());
		message.setQos(0);
		client.publish("ServerAlarm/Status", message);
		System.out.println("Se envio el mensaje del estado del Server Alarma");
	}
	
	
	@Override
	public void connectionLost(Throwable arg0) {
		Date fecha = new Date();
		System.out.println("ERROR  SE PERDIO LA CONECCION: "+ fecha.toString());
		iniciar();
		
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			if(!topic.contains("/response") && !topic.contains("homeassistant")) {
				String msj= new String(message.getPayload());
				JSONObject json= new JSONObject(msj);
				String email= json.get("mail").toString();
				String passCuent= json.get("pwd").toString();
				String NdvHATypDvid= json.get("NdvHATypDvid").toString();
				String[] a = NdvHATypDvid.split("-");
				Boolean needConfigHA=false;
				if(a[0].equals("1"))
					needConfigHA=true;
				String type=a[1];
				String deviceIDRecibe=a[2];
				System.out.println("Use HA: "+needConfigHA + " type: "+ type+ " deviceID recibio: "+deviceIDRecibe);
				String passCuenta=new String(Base64.getEncoder().encode(passCuent.getBytes()));
				UserAlarm user= userdao.retrieveByMail(email);
				if(user==null) {
					//Create User
					user= new UserAlarm();
					user.setEmail(email);
					user.setPassCuenta(passCuenta);
					Date date=new Date();
					user.setFechaCreacion(date.toString());
					String userID=Utils.generateRandomHexa();
					user.setUserID(userID);
					String newDeviceId= Utils.ObtainDeviceID(type);
					user.getDeviceIds().add(newDeviceId);
					String mqttUser=Utils.generateRandomHexa();
					user.setMqttUser(mqttUser);
					String mqttPass=Utils.generateRandomHexaPass();
					user.setMqttPassword(mqttPass);
					userdao.create(user);

					//enviar mensaje por mail
					try {
						MailController.enviarNotificacion(userID,newDeviceId,mqttUser,mqttPass,email);
					} catch (Exception e) {
						e.printStackTrace();
					}

					//enviar mensaje mqtt
					String clientIDRecibed=topic.replace("Deviceconfig/", "");
					sendResponseMQTT(clientIDRecibed,newDeviceId,userID,mqttUser,mqttPass);
					System.out.println("Envio la respuesta via MQTT");

					//implementacion para HomeAssistant
					if(false) {//needConfigHA
						System.out.println("Entro en config de HA");
						ResponseMQTTHomeAssistant.sendResponseMQTTHA(clientIDRecibed,newDeviceId,userID,mqttUser,mqttPass,type);
						System.out.println("Envio la respuesta via MQTT");
					}

					//cargar usuario en mosquitto
					Utils.CreateUserInMosquittoDB(newDeviceId,mqttUser,mqttPass);
					//Utils.UploadUserInMosquitto(mqttUser,mqttPass);
					System.out.println("Actualizo Mosquitto");

				}
				else {
					System.out.println("El usuario ya existe - verifico el password");
					if(user.getPassCuenta().equals(passCuenta)) {
						//si el dispositivo es nuevo
						if(deviceIDRecibe==null || deviceIDRecibe.contains("empty")) {
							System.out.println("entro en el nuevo dispositivo");
							String newDeviceId= Utils.ObtainDeviceID(type);
							user.getDeviceIds().add(newDeviceId);
							userdao.update(user);
							//enviar mensaje por mail
							try {
								MailController.enviarNotificacion(user.getUserID(),newDeviceId,user.getMqttUser(),user.getMqttPassword(),email);
							} catch (Exception e) {
								e.printStackTrace();
							}
							//enviar mensaje mqtt
							String clientIDRecibed=topic.replace("Deviceconfig/", "");
							sendResponseMQTT(clientIDRecibed,newDeviceId,user.getUserID(),user.getMqttUser(),user.getMqttPassword());
							System.out.println("Envio la respuesta via MQTT");

							//implementacion para HomeAssistant
							if(false) {//needConfigHA
								ResponseMQTTHomeAssistant.sendResponseMQTTHA(clientIDRecibed,newDeviceId,user.getUserID(),user.getMqttUser(),user.getMqttPassword(),type);
								System.out.println("Envio la respuesta via MQTT");
							}
							Utils.CreateACL(user.getMqttUser(),newDeviceId);

						}else {
							//si el dispositivo ya existia
							//enviar mensaje mqtt
							String clientIDRecibed=topic.replace("Deviceconfig/", "");
							sendResponseMQTT(clientIDRecibed,deviceIDRecibe,user.getUserID(),user.getMqttUser(),user.getMqttPassword());
							System.out.println("El DISPOSITIVO ya existia no cambio nada");
						}
					}else {
						System.out.println("Las password no coincide con lo esperado");
						//devolver el error por mail para el mail
						try {
							//enviar mensaje mqtt
							String clientIDRecibed=topic.replace("Deviceconfig/", "");
							sendResponseMQTT(clientIDRecibed,"wrong-password","","","");
							System.out.println("Envio la respuesta via MQTT");

							String mensaje= "The email address is already registered on the platform.... Error in the password sent, it does not match the registered password: "+passCuent;
							MailController.sendMail(mensaje,email);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			else if (topic.contains("homeassistant")) {
				HomeAssistanConfig.addConfig(topic,message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MqttClient getClient() {
		return client;
	}





	public void sendResponseMQTT(String clientIDRecibed,String newDeviceId,String userOwner, String mqttUser, String mqttPass) {
		try {
			String publisherId = UUID.randomUUID().toString();
			IMqttClient publisher = new MqttClient(Settings.getInstance().getUriBroker(),publisherId,new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			options.setUserName(Settings.getInstance().getUserNameBroker());
			options.setPassword(Settings.getInstance().getPasswordBroker().toCharArray());
			publisher.connect(options);		
			if ( !publisher.isConnected()) {
				System.err.println("Fallo la conexion al enviar la RESPUETA - reconectando");
				iniciar();
			}
			String topico="Deviceconfig/"+clientIDRecibed+"/response";
			try {
				JSONObject json= new JSONObject();
				json.put("clt", newDeviceId);
				json.put("mqtt", mqttUser+"-"+mqttPass);
				String val=json.toString();
					MqttMessage msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MqttMessage makemqttmessageString(String message1) {                       
	  	 MqttMessage message = new MqttMessage();
	  	 message.setPayload(message1.getBytes());
	  	 return message;
	  }	
   
}
