package serverAlarma.Persistence.DAO;


import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.bson.conversions.Bson;

import serverAlarma.Persistence.Model.UserAlarm;
import serverAlarma.Persistence.Mongo.MongoDBClient;



public class UserDAO extends MongoDBClient<UserAlarm>{

	
	public UserDAO() {
		super(UserAlarm.class);
	}

	public List<UserAlarm> retrieveAllUsers() {
		return this.retrieveAll();
	}
	
	public UserAlarm retrieveByMail(String email) {
		Bson filter = eq("email", email);
		return this.retrieveByFilter(filter);
	}
	
	public UserAlarm retrieveDeviceId(String deviceId) {
		Bson filter = eq("deviceId", deviceId);
		return this.retrieveByFilter(filter);
	}
	
    public void deleteUser(UserAlarm user) {
		this.delete(user);
	}
	
	@Override
	protected String getDatabaseName() {
		return "MQTT-Alarm";
	}
}
