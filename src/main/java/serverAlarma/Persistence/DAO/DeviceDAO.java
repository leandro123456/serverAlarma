package serverAlarma.Persistence.DAO;


import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.bson.conversions.Bson;

import serverAlarma.Persistence.Model.Device;
import serverAlarma.Persistence.Mongo.MongoDBClient;;


public class DeviceDAO extends MongoDBClient<Device>{

	public DeviceDAO() {
		super(Device.class);
	}
	
	public List<Device> retrieveAllDevices() {
		return this.retrieveAll();
	}
	
	public Device retrieveFirst() {
		return this.retrieveFirstObject();
	}
	
	public Device retrieveFirstbyType(String type) {
		Bson filter = eq("type", type);
		return this.retrieveByFilter(filter);
	}
	
	@Override
	protected String getDatabaseName() {
		return "MQTT-Alarm";
	}
	
	
}
