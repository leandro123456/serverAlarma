package serverAlarma.util;

import java.io.FileInputStream;
import java.util.Properties;


public class Settings {
	private Properties properties;
    private static Settings instance = null;
	private String uriBroker;
	private String userNameBroker;
	private String passwordBroker;
	private String mqttDBurl;
	private String mqttDBName;
	private String mqttDBUser;
	private String mqttDBPass;


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
            this.mqttDBurl = properties.getProperty("mqttDBurl");
            this.mqttDBName = properties.getProperty("mqttDBName");
            this.mqttDBUser = properties.getProperty("mqttDBUser");
            this.mqttDBPass = properties.getProperty("mqttDBPass");
        } catch (Exception e) {
        	System.out.println("Error... no se puede leer el archivo de propiedades");
            e.printStackTrace();
        }
    }

	public String getMqttDBurl() {
		return mqttDBurl;
	}

	public String getMqttDBName() {
		return mqttDBName;
	}

	public String getMqttDBUser() {
		return mqttDBUser;
	}

	public String getMqttDBPass() {
		return mqttDBPass;
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
	
}

