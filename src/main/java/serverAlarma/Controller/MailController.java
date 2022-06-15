package serverAlarma.Controller;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class MailController {

	public static void enviarNotificacion(String userOwner,String newDeviceId,String mqttUser,String mqttPAss,String email) {
		try {
			//armado del mail
			String cabecera = "<HTML><head>\n" + 
					"  <style>\n" + 
					"  table {\n" + 
					"    width:100%;\n" + 
					"  }\n" + 
					"  table, th, td {\n" + 
					"    border: 1px solid black;\n" + 
					"    border-collapse: collapse;\n" + 
					"  }\n" + 
					"  th, td {\n" + 
					"    padding: 15px;\n" + 
					"    text-align: left;\n" + 
					"  }\n" + 
					"  tr:nth-child(even) {\n" + 
					"    background-color: #eee;\n" + 
					"  }\n" + 
					"  tr:nth-child(odd) {\n" + 
					"   background-color: #fff;\n" + 
					"  }\n" + 
					"  th {\n" + 
					"    background-color: black;\n" + 
					"    color: white;\n" + 
					"  }\n" + 
					"  </style>\n" + 
					"  </head><BODY><br/> <br/>";
			String tablaprevia="";
			//ultimas 1o zonas
				tablaprevia=tablaprevia+"<tr>";
				tablaprevia=tablaprevia+"<td>"+"DeviceID"+"</td>";
				tablaprevia=tablaprevia+"<td>"+newDeviceId+"</td>";
				tablaprevia=tablaprevia+"</tr>";
				tablaprevia=tablaprevia+"<tr>";
				tablaprevia=tablaprevia+"<td>"+"Coiaca MQTT broker username"+"</td>";
				tablaprevia=tablaprevia+"<td>"+mqttUser+"</td>";
				tablaprevia=tablaprevia+"</tr>";
				tablaprevia=tablaprevia+"<tr>";
				tablaprevia=tablaprevia+"<td>"+"Coiaca MQTT broker Password"+"</td>";
				tablaprevia=tablaprevia+"<td>"+mqttPAss+"</td>";
				tablaprevia=tablaprevia+"</tr>";
				tablaprevia=tablaprevia+"<tr>";
				tablaprevia=tablaprevia+"<td>"+"Home Assistant MQTT Discovery Topic prefix"+"</td>";
				tablaprevia=tablaprevia+"<td>"+userOwner+"</td>";
				tablaprevia=tablaprevia+"</tr>";
			
			String body= "<p>Hello,</p>"//<br/>
					+ "<p>Thanks for registering your Coiaca BRDSC01 device. You will find below credentials and information that will allow you to use all features and services.</p> "
					+ "<table >\n" + 
					"  <tr>\n" + 
					"    <th>Parameter</th>\n" + 
					"    <th>Value</th> \n" + 
					"  </tr>\n" + tablaprevia+ 
					"</table>"
					+ "<br/>"//;
					//+ "<p> </p>"
					+ "<h3>What these parameters are and why do you need them.</h3>"
			+"<p><b>DeviceID: </b>Unique identification of your device.</p>"
			+"<p><b>Coiaca MQTT broker username and password: </b>These credentials are the ones your device uses "
			+ "to connect to Coiaca MQTT broker and also can be used with any other a"
			+ "pplication that you may want to use to control your device with, using Coiaca MQTT broker.</p>"+
			"<p>If you don't want to use Coiaca MQTT broker, you must replace these credentials with the other broker's. "
			+ "And in that case, you will also need to configure the broker url and port accordingly.</p>"+
			"<p><b>Home Assistant MQTT Discovery Topic Prefix: </b>This parameter is used only on some Home Assistant integrations.</p>"+
			"<p>The device will use \"homeassistant\" as MQTT Discovery Topic Prefix. If you are using your own MQTT broker and you didn`t "
			+"change the Home Assistant MQTT configuration, you don't need to use this parameter, because \"homeassistant\" is the default one.</p>"+
			"<p>If you changed the Home Assistant MQTT configuration and you are not using the default MQTT Discovery Topic Prefix, you can change it "
			+"on your device configuration screen, updating the \"Default mqttDiscovery prefix\" field.</p>"+
			"<p>But If you are planning to use Home Assistant with Coiaca MQTT broker you must configure your Home Assistant to use this parameter as"
			+" MQTT Discovery Topic Prefix. You can find how to do it here: "
			+"<a href='https://www.home-assistant.io/docs/mqtt/discovery'>https://www.home-assistant.io/docs/mqtt/discovery/</a>."+
			"</p>"+
			"<p>You can find more information in the "
			+"<a href='https://coiaca.com/index.php/documentation/'> documentation section</a>"
			+" on "
			+"<a href='https://coiaca.com/'>Coiaca.com</a>."
			+ "</p>"+
			"<p>If you have any questions or need further assistance, please contact Coiaca customer support:"
			+"<a href='mailto:support@coiaca.com'> support@coiaca.com</a>."+
			"</p>"+
			"<p>Best Regards<br/>"+
			"Coiaca Support</p>";
			
			
			
			String pie = "<br/> <br/></BODY></HTML>";
			String formulario = String.format("%s%s%s%s", cabecera, body, "<br/> <br/>", pie);
			sendMail(formulario, email,newDeviceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void sendMail(String Mensaje,String destino,String deviceid) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("support@qliq.com.ar", "TkblMT~e6q|");
			}
		});
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("COIACA"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(destino));
			message.setSubject("Coiaca Device Registration: "+ deviceid);
			message.setText(Mensaje,"ISO-8859-1","html");
			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com","support@qliq.com.ar", "TkblMT~e6q|");
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
			System.out.println("Su mensaje se envio correctamente");

		} catch (MessagingException e) {
			System.out.println("fallo el envio del mensaje");
			throw new RuntimeException(e);
		}
	}



	public static void FinalZonasDeAlarmaNotificar() {

		//ENVIAR MAIL	CON UNA TABLA
		String cabecera = "<HTML><head>\n" + 
				"  <style>\n" + 
				"  table {\n" + 
				"    width:100%;\n" + 
				"  }\n" + 
				"  table, th, td {\n" + 
				"    border: 1px solid black;\n" + 
				"    border-collapse: collapse;\n" + 
				"  }\n" + 
				"  th, td {\n" + 
				"    padding: 15px;\n" + 
				"    text-align: left;\n" + 
				"  }\n" + 
				"  tr:nth-child(even) {\n" + 
				"    background-color: #eee;\n" + 
				"  }\n" + 
				"  tr:nth-child(odd) {\n" + 
				"   background-color: #fff;\n" + 
				"  }\n" + 
				"  th {\n" + 
				"    background-color: black;\n" + 
				"    color: white;\n" + 
				"  }\n" + 
				"  </style>\n" + 
				"  </head><BODY><br/> <br/>";
		String tablaprevia="";
		for(int i=0; i<10; i++) {
			tablaprevia=tablaprevia+"<tr>";
			tablaprevia=tablaprevia+"<td>"+"vector1[0]"+"</td>";
			tablaprevia=tablaprevia+"<td>"+"vector1[1]"+"</td>";
			tablaprevia=tablaprevia+"<td>"+"vector1[2]"+"</td>";
			tablaprevia=tablaprevia+"</tr>";
		}	
		String body= "<h1>Su alarma paso a Estado"+"Trigger"+" </h1> <br/> "
				+ "<h3>Se agregan los 10 regostros previos a que se ejecute su Alarma</h3> "
				+ "<table >\n" + 
				"  <tr>\n" + 
				"    <th>Zona</th>\n" + 
				"    <th>Estado</th> \n" + 
				"    <th>Fecha</th>\n" + 
				"  </tr>\n" + tablaprevia+ 
				"</table>"
				+ "<br/>";
		String pie = "<br/> <br/> <footer><p> Dash</p></footer></BODY></HTML>";
		String formulario = String.format("%s%s%s%s", cabecera, body, "<br/> <br/>", pie);
		MailController.sendMail(formulario, "leandrogabrielguzman@gmail.com","");

	}

}
