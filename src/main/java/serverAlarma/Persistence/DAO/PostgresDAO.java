package serverAlarma.Persistence.DAO;


import serverAlarma.Persistence.Model.PostgresID;
import serverAlarma.Persistence.Mongo.MongoDBClient;;


public class PostgresDAO extends MongoDBClient<PostgresID>{

	public PostgresDAO() {
		super(PostgresID.class);
	}
	
	
	public PostgresID retrieveFirst() {
		return this.retrieveFirstObject();
	}
	
	
	@Override
	protected String getDatabaseName() {
		return "MQTT-Alarm";
	}
	
	
}
