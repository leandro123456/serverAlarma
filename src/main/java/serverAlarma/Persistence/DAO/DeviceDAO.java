package serverAlarma.Persistence.DAO;


import java.util.List;


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
	
	@Override
	protected String getDatabaseName() {
		return "MQTT-Alarm";
	}
	
	
}
