package serverAlarma.util;

import java.io.FileInputStream;
import java.util.Properties;


public class Settings {
	private Properties properties;
    private static Settings instance = null;
	private String uriBroker;
	private String userNameBroker;
	private String passwordBroker;
	private String myUrl;
	private String brokerUrl;
	private String brokerIp;
	private Boolean brokerIsSecure;
	private String firebaseUrl;
	


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
            this.uriBroker = properties.getProperty("uriBroker");//"tcp://161.35.254.222:1883";//
            this.brokerUrl= properties.getProperty("brokerUrl"); //localhost
            this.brokerIp=properties.getProperty("brokerIp");//1883
            this.brokerIsSecure=Boolean.parseBoolean(properties.getProperty("brokerIsSecure"));
            this.userNameBroker = properties.getProperty("userNameBroker");//"cDashSVR";//
            this.passwordBroker = properties.getProperty("passwordBroker");//"av1vEDacfGwXc5";//
            this.myUrl= properties.getProperty("myServerProvisioningURL");//"http://salty-wave-32309.herokuapp.com";//
            this.firebaseUrl= properties.getProperty("firebaseUrl");
        } catch (Exception e) {
        	System.out.println("Error... no se puede leer el archivo de propiedades");
            e.printStackTrace();
        }
    }

	public String getFirebaseUrl() {
		return firebaseUrl;
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

	public String getMyUrl() {
		return myUrl;
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public String getBrokerIp() {
		return brokerIp;
	}

	public Boolean getBrokerIsSecure() {
		return brokerIsSecure;
	}	
}

