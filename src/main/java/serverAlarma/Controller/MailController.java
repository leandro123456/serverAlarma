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
				tablaprevia=tablaprevia+"<td>"+"UserMQTT"+"</td>";
				tablaprevia=tablaprevia+"<td>"+mqttUser+"</td>";
				tablaprevia=tablaprevia+"</tr>";
				tablaprevia=tablaprevia+"<tr>";
				tablaprevia=tablaprevia+"<td>"+"PasswordMQTT"+"</td>";
				tablaprevia=tablaprevia+"<td>"+mqttPAss+"</td>";
				tablaprevia=tablaprevia+"</tr>";
				tablaprevia=tablaprevia+"<tr>";
				tablaprevia=tablaprevia+"<td>"+"HomeAssistant Config"+"</td>";
				tablaprevia=tablaprevia+"<td>"+userOwner+"</td>";
				tablaprevia=tablaprevia+"</tr>";
			
			String body= "<h1>Welcome to COAICA DSC Alarm </h1> <br/> "
					+ "<h3>Below you can see the credentials generated for your device</h3> "
					+ "<table >\n" + 
					"  <tr>\n" + 
					"    <th>Field</th>\n" + 
					"    <th>Value</th> \n" + 
					"  </tr>\n" + tablaprevia+ 
					"</table>"
					+ "<br/>"//;
					//+ "<p> </p>"
					+ "<p>Your device will automatically update to set these settings and restart</p>";
			String pie = "<br/> <br/> <footer><p> COIACA</p></footer></BODY></HTML>";
			String formulario = String.format("%s%s%s%s", cabecera, body, "<br/> <br/>", pie);
			sendMail(formulario, email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void sendMail(String Mensaje,String destino) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("iot@qliq.com.ar", "nMvJRVdqb0DXlgpPVJnr");
			}
		});
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("COIACA"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(destino));
			message.setSubject("[CAICA]: no-reply");
			message.setText(Mensaje,"ISO-8859-1","html");
			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com","iot@cdash.space", "nMvJRVdqb0DXlgpPVJnr");
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
		MailController.sendMail(formulario, "leandrogabrielguzman@gmail.com");

	}

}
