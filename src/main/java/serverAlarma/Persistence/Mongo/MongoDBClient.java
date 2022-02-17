package serverAlarma.Persistence.Mongo;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.mongodb.morphia.Morphia;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@SuppressWarnings("rawtypes")
public abstract class MongoDBClient<T extends MongoDBObject> extends MongoDBProperties {

	private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	protected final Class<T> classType;
	private final String collectionName;
	protected final Morphia morphiaMapper;
	
	protected MongoDBClient(Class<T> classType) {
		this.classType = classType;
		this.collectionName = this.classType.getSimpleName();
		
		Set<Class> classSet = new HashSet<Class>();
		classSet.add(this.classType);
		this.morphiaMapper = new Morphia(classSet);
		this.morphiaMapper.getMapper().getOptions().setStoreNulls(true);
		this.morphiaMapper.getMapper().getOptions().setStoreEmpties(true);
	}
	
	public void create(T object) {
		MongoClient mongoClient = getMongoClient();
		
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			mongoCollection.insertOne(object);
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
	}
	
	public void update(T object) {
		MongoClient mongoClient = getMongoClient();
		
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			mongoCollection.updateOne(eq("_id", object.getId()), parseToDocumentUpdate(object));
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
	}
	
	public void delete(T object) {
		MongoClient mongoClient = getMongoClient();
		
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			
			mongoCollection.deleteOne(eq("_id", object.getId()));
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
	}
	
	protected T retrieveByFilter(Bson filter) {
		return retrieve(filter, null);
	}
	
	protected T retrieveFirstObject() {
		return retrieve(null, null);
	}
	
	protected T retrieveByFilterAndSort(Bson filter, Bson sort) {
		return retrieve(filter, sort);
	}
	
	private T retrieve(Bson filter, Bson sort) {
		MongoClient mongoClient = getMongoClient();
		
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			
			FindIterable<T> iterable;
			if (filter != null) iterable = mongoCollection.find(filter);
			else iterable = mongoCollection.find();
			
			if (sort != null) return iterable.sort(sort).first();
			else return iterable.first();
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
		
		return null;
	}
	
	protected List<T> retrieveAll() {
		return retrieveList(null, null);
	}
	
	protected List<T> retrieveListByFilter(Bson filter) {
		return retrieveList(filter, null);
	}

	protected List<T> retrieveListByFilterAndSort(Bson filter, Bson sort) {
		return retrieveList(filter, sort);
	}
	
	protected List<T> retrieveListByFilterAndSortWithLimit(Bson filter, Bson sort,int elementsLimit) {
		return retrieveListWithLimit(filter, sort, elementsLimit);
	}
	
	private List<T> retrieveList(Bson filter, Bson sort) {
		MongoClient mongoClient = getMongoClient();
		List<T> listObject = null;
		
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			MongoCursor<T> cursor;
			if(sort == null) {
				if(filter != null) {
					cursor = mongoCollection.find(filter).iterator();
				} else {
					cursor = mongoCollection.find().iterator();
				}
			} else {
				if(filter != null) {
					cursor = mongoCollection.find(filter).sort(sort).iterator();
				} else {
					cursor = mongoCollection.find().sort(sort).iterator();
				}
			}

			listObject = new ArrayList<T>();
			while(cursor.hasNext()) 
				listObject.add(cursor.next());
				
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
		
		return listObject;
	}
	
	private List<T> retrieveListWithLimit(Bson filter, Bson sort, int elementsLimit) {
		MongoClient mongoClient = getMongoClient();
		List<T> listObject = null;
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			MongoCursor<T> cursor;
			if(sort == null) {
				if(filter != null) {
					cursor = mongoCollection.find(filter).limit(elementsLimit).iterator();
				} else {
					cursor = mongoCollection.find().limit(elementsLimit).iterator();
				}
			} else {
				if(filter != null) {
					cursor = mongoCollection.find(filter).sort(sort).limit(elementsLimit).iterator();
				} else {
					cursor = mongoCollection.find().sort(sort).limit(elementsLimit).iterator();
				}
			}
			listObject = new ArrayList<T>();
			while(cursor.hasNext()) 
				listObject.add(cursor.next());
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
		return listObject;
	}
	
	protected <R> List<R> getListOfValues(String field, Class<R> classType) {
		MongoClient mongoClient = getMongoClient();
		
		try {
			List<R> valuesList = new ArrayList<R>();
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			DistinctIterable<R> distinctIterable = mongoCollection.distinct(field, classType);
			distinctIterable.map(value -> valuesList.add(value));
			return valuesList;
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
		
		return null;
	}
	
	protected <R> List<R> retrieveProcessedData(List<Bson> pipeline, Class<R> classType) {
		MongoClient mongoClient = getMongoClient();
		
		try {
			List<R> processedDataList = new ArrayList<R>();
			MongoDatabase mongoDatabase = mongoClient.getDatabase(getDatabaseName());
			mongoDatabase = mongoDatabase.withCodecRegistry(this.pojoCodecRegistry);
			MongoCollection<T> mongoCollection = mongoDatabase.getCollection(this.collectionName, this.classType);
			AggregateIterable<R> aggregateIterable =  mongoCollection.aggregate(pipeline, classType);
			MongoCursor<R> cursor = aggregateIterable.iterator();
			while(cursor.hasNext())	processedDataList.add(cursor.next());
			return processedDataList;
		} catch(Exception e) {
			e.printStackTrace();
			mongoClient.close();
		} finally {
			mongoClient.close();
		}
		
		return null;
	}

	
	@SuppressWarnings("unchecked")
	private Document parseToDocumentUpdate(T object) {
		DBObject dbObject = this.morphiaMapper.toDBObject(object);
		Document documentObject = new Document(dbObject.toMap());
		if (documentObject.containsKey("className")) documentObject.remove("className");
		if (documentObject.containsKey("id")) documentObject.remove("id");
		if (documentObject.containsKey("_id")) documentObject.remove("_id");
		String jsonDocument = documentObject.toJson();
		String jsonUpdate = "{ $set: " + jsonDocument + " }";
		Document documentUpdate = Document.parse(jsonUpdate);
		return documentUpdate;
	}
}
