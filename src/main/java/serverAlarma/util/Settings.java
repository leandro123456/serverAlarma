package serverAlarma.util;

import java.io.FileInputStream;
import java.util.Properties;


public class Settings {
	private Properties properties;
    private static Settings instance = null;
	private String uriBroker;
	private String userNameBroker;
	private String passwordBroker;


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
        try {
        	
            this.uriBroker = "tcp://161.35.254.222:1883";//properties.getProperty("uriBroker");
            this.userNameBroker = "cDashSVR";//properties.getProperty("userNameBroker");
            this.passwordBroker = "av1vEDacfGwXc5";//properties.getProperty("passwordBroker");
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
	
}

