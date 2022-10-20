package hello;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import serverAlarma.FirebaseNotification.SendFirebaseNotfication;
import serverAlarma.MQTTAnalisis.MQTTAnalisis;
import serverAlarma.Persistence.Postgresql.Controller.UserPhoneController;


public class TestNotificaciones {

	//@Test
	public void testEsperar() {
		try { 
			System.out.println("espera: "+ new Date().toString());
			Thread.sleep(5000);
			System.out.println("termino espera: "+ new Date().toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Thread Interrupted");
        }
		System.out.println("termino");
	}
	
	@Test
	public void testSendNotificationBy() {
		 MqttMessage message= new MqttMessage();
		 message.setPayload("disarmed".getBytes());
		 String topico="52B5493A/DSC010000000105/Partition1";
		 MQTTAnalisis.VerifyMsg(topico,message);
	}
}
