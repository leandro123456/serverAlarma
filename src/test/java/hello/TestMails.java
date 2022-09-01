package hello;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONException;
import org.junit.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import serverAlarma.Controller.MailController;

public class TestMails {
	
	@Test
	public void sendMail() {
		String userID="userIDvalue";
		String newDeviceId="deviceIDVALUE";
		String mqttUser="mqttUser";
		String mqttPass="mqttPass";
		String email="leandroplay1448@gmail.com";//"federico@coiaca.com";//
		MailController.enviarNotificacion(userID,newDeviceId,mqttUser,mqttPass,email);
	}
	
	//@Test
	public void parseoDeMensaje() {
		String NdvHATypDvid= "1-1-DSC01-DSC010001022202";
		String[] a = NdvHATypDvid.split("-");
		for(int i=0;i<a.length;i++) 
			System.out.println(a[i]);
		
	}
	
	//@Test
	public void TestCreateUserMosquitto() {
		System.out.println("Start save New user");
		Boolean wasOk=false;
		Session session = null;
		ChannelExec channel = null;
		try {
			session = new JSch().getSession("root", "161.35.254.222");
			session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String user= "leandro231";
			String password= "pass1234";
			String commando= "mosquitto_passwd -b /etc/mosquitto/passwd "+user +" "+password;
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
		
	//@Test
	public void TestSSHTunnel() {
		System.out.println("Start save New user");
		Boolean wasOk=false;
		try {
			String user= "leandro191";
			String password= "pass1234";
			String commando= "mosquitto_passwd -b /etc/mosquitto/passwd "+user +" "+password;
			JSch jsch = new JSch();
			Session session = jsch.getSession("root", "161.35.254.222");
			session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
	        System.out.println("Connection established.");
	        Channel shellChannel = session.openChannel("shell");
//	        shellChannel.connect();
//	        shellChannel.setInputStream(System.in);
//	        shellChannel.setOutputStream(System.out);
	        PrintStream shellStream = new PrintStream(shellChannel.getOutputStream());
	        shellChannel.connect();
	        shellStream.println(commando);
	        shellStream.flush();
	        shellChannel.disconnect();
	        System.out.println("Desconecto el shell");
			session.disconnect();
			System.out.println("desconecto session");
			wasOk=true;
		} catch (Exception e) {
			e.printStackTrace();
			wasOk=false;
		}
		System.out.println("Save User done");
		if(wasOk) {
			System.out.println("Restart Service");
			try {
				String commando= "systemctl restart mosquitto";
				JSch jsch = new JSch();
				Session session = jsch.getSession("root", "161.35.254.222");
				session.setPassword("4cZ8i%~tz'dy$KVDHH;//%OZOI");
				java.util.Properties config = new java.util.Properties(); 
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
		        System.out.println("Connection established.");
		        Channel shellChannel = session.openChannel("shell");
		        shellChannel.connect();
		        shellChannel.setInputStream(System.in);
		        shellChannel.setOutputStream(System.out);
		        PrintStream shellStream = new PrintStream(shellChannel.getOutputStream());
		        shellChannel.connect();
		        shellStream.println(commando);
		        shellStream.flush();
		        System.out.println("SFTP Channel created.");
		        shellChannel.disconnect();
		        System.out.println("Desconecto el shell");
				session.disconnect();
				System.out.println("desconecto");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Restart Service done");
		}
	}
	
	//@Test
	public void TestcopyRemoteFiles() {
		String user= "";
		String password="";
		String serverPort="";
		String localFolder="";
		
		try {
				List<String> command = Arrays.asList("mosquitto_passwd -b /etc/mosquitto/passwd", "-P", serverPort, user + " " + password, localFolder);
				ProcessBuilder pb = new ProcessBuilder()
						.command(command)
						.directory(new File(localFolder));
				String s = null;     	
				try {
					Process p = pb.start();
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				} catch (Exception e) {
					e.printStackTrace();
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//@Test
	public void TestMailsConTabla() {
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
			tablaprevia=tablaprevia+"<td>"+"ClientID"+"</td>";
			tablaprevia=tablaprevia+"<td>"+"DSC01000000001"+"</td>";
			tablaprevia=tablaprevia+"</tr>";
			tablaprevia=tablaprevia+"<tr>";
			tablaprevia=tablaprevia+"<td>"+"UserMQTT"+"</td>";
			tablaprevia=tablaprevia+"<td>"+"DSC01000000001"+"</td>";
			tablaprevia=tablaprevia+"</tr>";
			tablaprevia=tablaprevia+"<tr>";
			tablaprevia=tablaprevia+"<td>"+"PasswordMQTT"+"</td>";
			tablaprevia=tablaprevia+"<td>"+"mypassword"+"</td>";
			tablaprevia=tablaprevia+"</tr>";
		
		String body= "<h1>Le damos la bienvenida a COAICA DSC Alarm </h1> <br/> "
				+ "<h3>A continuacion puede ver las credenciales generadas para su dispositivo</h3> "
				+ "<table >\n" + 
				"  <tr>\n" + 
				"    <th>Campo</th>\n" + 
				"    <th>Valor</th> \n" + 
				"  </tr>\n" + tablaprevia+ 
				"</table>"
				+ "<br/>"//;
				//+ "<p> </p>"
				+ "<p>Su dispositivo se actualizara automaticamente para establecer esta configuracion y se reiniciara</p>";
		String pie = "<br/> <br/> <footer><p> COIACA</p></footer></BODY></HTML>";
		String formulario = String.format("%s%s%s%s", cabecera, body, "<br/> <br/>", pie);
		MailController.sendMail(formulario, "leandroplay1448@gmail.com","deviceID");
		System.out.println("termino");
	}
}
