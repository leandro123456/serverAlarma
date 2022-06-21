package serverAlarma.Persistence.Mongo;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;

abstract class MongoDBProperties {
	
	private static final String HOST_MONGODB = "10.136.18.162";//"localhost";//
	private static final Integer PORT_MONGODB = 27017;
	private static final Boolean USE_SSL_MONGODB = false;
	
	protected abstract String getDatabaseName();
	
	protected MongoClient getMongoClient() {
//		MongoCredential mongoCredential = getMongoCredential();
		return new MongoClient(getServerAddress(), getMongoClientOptions());
	}
	
	private List<ServerAddress> getServerAddress() {
		ServerAddress serverAddress = new ServerAddress(HOST_MONGODB, PORT_MONGODB);
		
		List<ServerAddress> listServerAddress = new ArrayList<ServerAddress>();
		listServerAddress.add(serverAddress);
		
		return listServerAddress;
	}
	
	private MongoClientOptions getMongoClientOptions() {
		Builder builder = new Builder();
		builder.sslEnabled(USE_SSL_MONGODB);
		MongoClientOptions mongoOptions = builder.build();
		return mongoOptions;
	}
	
//	private MongoCredential getMongoCredential() {
//		return MongoCredential.createScramSha1Credential(USER_MONGODB, getDatabaseName(), PASSWORD_MONGODB.toCharArray());
//	}
}
