package serverAlarma.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import serverAlarma.Controller.MqttConnect;
import serverAlarma.Persistence.DAO.UserDAO;
import serverAlarma.Persistence.Model.UserAlarm;
import serverAlarma.util.Settings;

public class HomeAssistanConfig {
	
	public static void addConfig(String topic, MqttMessage message) {
		UserDAO userdao= new UserDAO();
		//Reenviar la configuracion a HA Cloud
		UserAlarm user=userdao.retrieveDeviceId(ObtainDeviceId(topic, message));
		String userID=user.getUserID();
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
			topic=topic.replace("homeassistant", userID);
			message.setQos(0);
			message.setRetained(false);
			publisher.publish(topic,message);
			publisher.disconnect();
			publisher.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String ObtainDeviceId(String topic, MqttMessage message) {
		String msj= new String(message.getPayload());
		JSONObject json= new JSONObject(msj);
		String deviceid= json.get("~").toString();
		if(!topic.contains(deviceid)) {
			System.out.println("TOPICO para filtrado: "+ topic);
			deviceid=topic.replace("/config", "");
			deviceid=deviceid.replace("homeassistant/", "");
			String[]  val= deviceid.split("/");
			deviceid=val[1];
			if(deviceid.contains("/")) {
				val= deviceid.split("/");
				deviceid=val[1];
			}
		}
		System.out.println("DeviceId obtenido: "+ deviceid);
		return deviceid;
	}

}
