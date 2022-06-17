package serverAlarma.Monitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import serverAlarma.Controller.MqttConnect;
import serverAlarma.util.Settings;

@Component
public class MonitorComponent {
	@Scheduled(fixedDelay = 60000, initialDelay = 1000) 
	public void fixedDelaySch() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		Date now = new Date(); 
		String strDate = sdf.format(now);
		System.out.println("Fixed Delay scheduler: " + strDate);
		sendResponseMQTT(strDate,Settings.getInstance().getUserNameBroker(),Settings.getInstance().getPasswordBroker());
		
	}
	
	
	@SuppressWarnings("resource")
	public void sendResponseMQTT(String date, String mqttUser, String mqttPass) {
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
			String topico="ServerAlarm/keepAlive";
			JSONObject json= new JSONObject();
			json.put("date", date);
			MqttMessage message = new MqttMessage();
			message.setPayload(json.toString().getBytes());
			message.setQos(0);
			message.setRetained(true);
			publisher.publish(topico,message); 
			TimeUnit.SECONDS.sleep(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}