package serverAlarma.Persistence.Model;

import serverAlarma.Persistence.Mongo.MongoDBObject;



public class Device extends MongoDBObject{

	private static final long serialVersionUID = -4346222511562336633L;
	static final public String TERMOMETRO = "termometro";
	static final public String SONOFF = "sonoff";
	static final public String ALARMA = "alarma";
	
	private String name;
	private String userowner;
	private String serialnumber;
	private String type;

	
	public Device() {
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getUserowner() {
		return userowner;
	}

	public void setUserowner(String userowner) {
		this.userowner = userowner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}
}
