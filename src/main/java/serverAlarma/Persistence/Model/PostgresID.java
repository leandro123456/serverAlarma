package serverAlarma.Persistence.Model;

import serverAlarma.Persistence.Mongo.MongoDBObject;



public class PostgresID extends MongoDBObject{

	private static final long serialVersionUID = -4346222511562336633L;
	
	private Integer idpotgres;
	private Integer idpotgresacl;

	
	public PostgresID() {
	}


	public Integer getIdpotgresacl() {
		return idpotgresacl;
	}


	public void setIdpotgresacl(Integer idpotgresacl) {
		this.idpotgresacl = idpotgresacl;
	}


	public Integer getIdpotgres() {
		return idpotgres;
	}


	public void setIdpotgres(Integer idpotgres) {
		this.idpotgres = idpotgres;
	}



}
