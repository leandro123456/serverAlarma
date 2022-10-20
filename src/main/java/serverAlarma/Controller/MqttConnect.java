package serverAlarma.Controller;

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


import serverAlarma.util.Settings;
import serverAlarma.Configuration.HomeAssistanConfig;
import serverAlarma.MQTTAnalisis.MQTTAnalisis;
import serverAlarma.Monitor.MonitorComponent;

public class MqttConnect implements MqttCallback{
	private static MqttConnect mqttconnect= null;
	private MqttClient client;
	
	private MqttConnect(){
		this.iniciar();
	}
	
	public static MqttConnect getInstance(){
		if(mqttconnect==null){
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
	           	System.out.println(MonitorComponent.getTime()+ "  INFO	MQTT-client no esta conectado- Retry");
	           	publisher.connect(options);
	           	this.client =publisher;
	           	sendServerStatus(client, "online");
	           	if(!publisher.isConnected()) {
	           		iniciar();
	           	}
	        }else {
	        	System.out.println("MQTT-client Connected to: " + publisher);
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
		message.setRetained(true);
		client.publish("ServerAlarm/Status", message);
		System.out.println(MonitorComponent.getTime()+ "  INFO	Se envio el mensaje del estado del Server Alarma");
	}
	
	
	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println(MonitorComponent.getTime()+ "  INFO	ERROR  SE PERDIO LA CONECCION");
		iniciar();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			if (topic.contains("homeassistant")) {
				//TODO
				HomeAssistanConfig.addConfig(topic,message);
			}
			else {
			//	System.out.println("llego a mqtt analisis");
				MQTTAnalisis.VerifyMsg(topic, message);
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
					publisher.disconnect();
					publisher.close();
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
