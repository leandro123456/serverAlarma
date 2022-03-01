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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import serverAlarma.util.Settings;
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
		System.out.println("Se envio el mensaje");
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
			if(!topic.contains("/response")) {
				String msj= new String(message.getPayload());
				JSONObject json= new JSONObject(msj);
				String email= json.get("mail").toString();
				String passCuent= json.get("pass").toString();
				String passCuenta=new String(Base64.getEncoder().encode(passCuent.getBytes()));
				UserAlarm user= userdao.retrieveByMail(email);
				if(user==null) {
					user= new UserAlarm();
					user.setEmail(email);
					user.setPassCuenta(passCuenta);
					Date date=new Date();
					user.setFechaCreacion(date.toString());

					Device device=devicedao.retrieveFirst();
					if(device==null) {
						device=new Device();
						device.setName("primero");
						device.setSerialnumber("Coiaca-DSC010000000100");
						device.setUserowner("usuario-inicial");
						devicedao.create(device);
					}
					Integer number= Integer.parseInt(device.getSerialnumber().substring(13, device.getSerialnumber().length()));
					String newDeviceId= (number+1)+"";
					newDeviceId=device.getSerialnumber().substring(0,device.getSerialnumber().length()-newDeviceId.length())+newDeviceId;
					System.out.println("New Device id: "+newDeviceId);
					device.setSerialnumber(newDeviceId);
					devicedao.update(device);

					user.setDeviceId(newDeviceId);
					user.setMqttUser(newDeviceId);
					Random rand = new Random();
					int myRandomNumber = rand.nextInt(10000);
					String result = Integer.toHexString(myRandomNumber);
					String mqttPass=new String(Base64.getEncoder().encode(("random"+result).getBytes()));
					mqttPass=mqttPass.replaceAll("=", "R");
					System.out.println("MQTT password: "+ mqttPass);
					user.setMqttPassword(mqttPass);
					userdao.create(user);

					//enviar mensaje por mail
					try {
						MailController.enviarNotificacion(newDeviceId,mqttPass,email);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//enviar mensaje mqtt
					String clientIDRecibed=topic.replace("Deviceconfig/", "");
					sendResponseMQTT(clientIDRecibed,newDeviceId,mqttPass);
					System.out.println("-------------------------------------------------");
					System.out.println("-------------------------------------------------");
					System.out.println("Envio la respuesta via MQTT");
					System.out.println("-------------------------------------------------");
					System.out.println("-------------------------------------------------");
					
					//cargar usuario en mosquitto
					UploadUserInMosquitto(newDeviceId,mqttPass);
				}
				else {
					System.out.println("El usuario ya existe - verifico el password");
					if(user.getPassCuenta().equals(passCuenta)) {
						UserAlarm user1= new UserAlarm();
						user1.setEmail(email);
						user1.setPassCuenta(passCuenta);
						user1.setFechaCreacion(new Date().toString());

						Device device=devicedao.retrieveFirst();
						if(device==null) {
							device=new Device();
							device.setName("primero");
							device.setSerialnumber("Coiaca-DSC010000000100");
							device.setUserowner("usuario-inicial");
							devicedao.create(device);
						}
						Integer number= Integer.parseInt(device.getSerialnumber().substring(13, device.getSerialnumber().length()));
						String newDeviceId= (number+1)+"";
						newDeviceId=device.getSerialnumber().substring(0,device.getSerialnumber().length()-newDeviceId.length())+newDeviceId;
						System.out.println("New Device id: "+newDeviceId);
						device.setSerialnumber(newDeviceId);
						devicedao.update(device);

						user1.setDeviceId(newDeviceId);
						user1.setMqttUser(newDeviceId);
						Random rand = new Random();
						int myRandomNumber = rand.nextInt(10000);
						String result = Integer.toHexString(myRandomNumber);
						String mqttPass=new String(Base64.getEncoder().encode(("random"+result).getBytes()));
						mqttPass=mqttPass.replaceAll("=", "R");
						user1.setMqttPassword(mqttPass);
						userdao.create(user1);

						//enviar mensaje por mail
						try {
							MailController.enviarNotificacion(newDeviceId,mqttPass,email);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//enviar mensaje mqtt
						String clientIDRecibed=topic.replace("Deviceconfig/", "");
						sendResponseMQTT(clientIDRecibed,newDeviceId,mqttPass);
						System.out.println("Envio la respuesta via MQTT");
						
						//cargar usuario en mosquitto
						UploadUserInMosquitto(newDeviceId,mqttPass);
					}else {
						System.out.println("Las password no coincide con lo esperado");
						//devolver el error por mail para el mail
						try {
							//enviar mensaje mqtt
							String clientIDRecibed=topic.replace("Deviceconfig/", "");
							sendResponseMQTT(clientIDRecibed,"wrong-password");
							System.out.println("Envio la respuesta via MQTT");
							
							String mensaje= "The email address is already registered on the platform.... Error in the password sent, it does not match the registered password: "+passCuent;
							MailController.sendMail(mensaje,email);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MqttClient getClient() {
		return client;
	}


	private void UploadUserInMosquitto(String newDeviceId, String mqttPass) {
		System.out.println("Start save New user");
		Boolean wasOk=false;
		Session session = null;
		ChannelExec channel = null;
		try {
			session = new JSch().getSession("root", "161.35.254.222");
			session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String commando= "mosquitto_passwd -b /etc/mosquitto/passwd "+newDeviceId +" "+mqttPass;
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


	public void sendResponseMQTT(String clientIDRecibed,String newDeviceId, String mqttPass) {
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
				json.put("mqttpdw", mqttPass);
				String val=json.toString();
					MqttMessage msg = makemqttmessageString(val);
					msg.setQos(1);
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
	
	public void sendResponseMQTT(String clientIDRecibed,String message) {
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
				json.put("clt", message);
				json.put("mqttpdw", "");
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
