package serverAlarma.Persistence.Mongo;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;


public abstract class MongoDBObject implements Serializable {

	private static final long serialVersionUID = 4159332223208159712L;
	
	@Id
	private String id;
	
	public MongoDBObject() {
		this.id = new ObjectId().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
