package serverAlarma.util;

import java.io.FileInputStream;
import java.util.Properties;


public class Settings {
	private Properties properties;
    private static Settings instance = null;
	private String uriBroker;
	private String userNameBroker;
	private String passwordBroker;
	private String mqttURI;
	private String mqttPort;
	private String mqttIsSecure;


	private Settings() {
        this.load();
    }

    public static Settings getInstance() {
        if (instance==null) {
            instance = new Settings();
        }
        return instance;
    }


    public void load() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("/var/cdash/properties/cdash.properties"));
            this.uriBroker = properties.getProperty("uriBroker");
            this.userNameBroker = properties.getProperty("userNameBroker");
            this.passwordBroker = properties.getProperty("passwordBroker");
            this.mqttURI = properties.getProperty("mqttURI");
            this.mqttPort = properties.getProperty("mqttPort");
            this.mqttIsSecure = properties.getProperty("mqttIsSecure"); 
        } catch (Exception e) {
        	System.out.println("Error... no se puede leer el archivo de propiedades");
            e.printStackTrace();
        }
    }

	public String getUriBroker() {
		return uriBroker;
	}

	public String getUserNameBroker() {
		return userNameBroker;
	}

	public String getPasswordBroker() {
		return passwordBroker;
	}

	public String getMqttURI() {
		return mqttURI;
	}

	public String getMqttPort() {
		return mqttPort;
	}

	public String getMqttIsSecure() {
		return mqttIsSecure;
	}   
	
}

