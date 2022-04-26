package serverAlarma.Controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import serverAlarma.util.Settings;

public class ResponseMQTTHomeAssistant {
	
	public static void sendResponseMQTTHA(String clientIDRecibed,String deviceId,String userOwner, String mqttUser, String mqttPass, String type) {
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
				MqttConnect.getInstance().iniciar();
			}

			String topico="";
			switch (type) {
			case "DSC01":				
				try {
					topico=userOwner+"/alarm_control_panel/"+deviceId+"/config";
					JSONObject json= new JSONObject();
					json.put("name", "Security Partition 1");
					json.put("state_topic", userOwner+"/"+deviceId+"/Partition1");
					json.put("availability_topic", userOwner+"/"+deviceId+"/Status");
					json.put("command_topic", userOwner+"/"+deviceId+"/Set");
					json.put("payload_disarm", "1D");
					json.put("payload_arm_home", "1S");
					json.put("payload_arm_away", "1A");
					json.put("payload_arm_night", "1N");
					String val=json.toString();
					MqttMessage msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);

					topico=userOwner+"/sensor/"+deviceId+"/config";
					json= new JSONObject();
					json.put("name", "Wifi Alarma");
					json.put("device_class", "signal_strength");
					json.put("state_topic", userOwner+"/"+deviceId+"/keepAlive");
					json.put("unit_of_measurement", "dBm");
					json.put("value_template", "{{ value_json.dBm }}");
					json.put("availability_topic", userOwner+"/"+deviceId+"/Status");
					json.put("icon", "mdi:shield");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);

					topico=userOwner+"/binary_sensor/"+deviceId+"-Trouble/config";
					json= new JSONObject();
					json.put("name", "Security Trouble");
					json.put("device_class", "problem");
					json.put("state_topic", userOwner+"/"+deviceId+"/Trouble");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z1/config";
					json= new JSONObject();
					json.put("name", "Zone 1");
					json.put("device_class", "door");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone1");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z2/config";
					json= new JSONObject();
					json.put("name", "Zone 2");
					json.put("device_class", "window");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone2");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);

					topico=userOwner+"/binary_sensor/"+deviceId+"-Z3/config";
					json= new JSONObject();
					json.put("name", "Zone 3");
					json.put("device_class", "motion");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone3");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z4/config";
					json= new JSONObject();
					json.put("name", "Zone 4");
					json.put("device_class", "motion");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone4");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z5/config";
					json= new JSONObject();
					json.put("name", "Zone 5");
					json.put("device_class", "motion");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone5");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z6/config";
					json= new JSONObject();
					json.put("name", "Zone 6");
					json.put("device_class", "motion");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone6");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z7/config";
					json= new JSONObject();
					json.put("name", "Zone 7");
					json.put("device_class", "motion");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone7");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
					
					topico=userOwner+"/binary_sensor/"+deviceId+"-Z8/config";
					json= new JSONObject();
					json.put("name", "Zone 8");
					json.put("device_class", "motion");
					json.put("state_topic", userOwner+"/"+deviceId+"/Zone8");
					json.put("payload_on", "1");
					json.put("payload_off", "0");
					val=json.toString();
					msg = makemqttmessageString(val);
					msg.setQos(0);
					msg.setRetained(false);
					publisher.publish(topico,msg); 
					TimeUnit.SECONDS.sleep(3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
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
