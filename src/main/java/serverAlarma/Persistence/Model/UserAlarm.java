package serverAlarma.Persistence.Model;

import serverAlarma.Persistence.Mongo.MongoDBObject;



public class UserAlarm extends MongoDBObject{
    
	private static final long serialVersionUID = -4346222511562336633L;
	static final public String ROLE_SUPERADMIN = "SUPERADMIN";
    static final public String ROLE_ADMIN = "ADMIN";
    static final public String ROLE_USER = "USER";
    

	private String firstname;
	private String lastname;
	private String password;
	private String email;
	private String deviceId;
	private String passCuenta;
	private Boolean cuenta_iniciada;
	private String role;
	private String fechaCreacion;
	private String fechaUltimoIngrego;
	private Integer ingresosSinLogin;
	private String mqttUser;
	private String mqttPassword;



	public String getMqttUser() {
		return mqttUser;
	}


	public void setMqttUser(String mqttUser) {
		this.mqttUser = mqttUser;
	}


	public String getMqttPassword() {
		return mqttPassword;
	}


	public void setMqttPassword(String mqttPassword) {
		this.mqttPassword = mqttPassword;
	}


	public String getFechaCreacion() {
		return fechaCreacion;
	}


	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}


	public String getFechaUltimoIngrego() {
		return fechaUltimoIngrego;
	}


	public void setFechaUltimoIngrego(String fechaUltimoIngrego) {
		this.fechaUltimoIngrego = fechaUltimoIngrego;
	}


	public Integer getIngresosSinLogin() {
		return ingresosSinLogin;
	}


	public void setIngresosSinLogin(Integer ingresosSinLogin) {
		this.ingresosSinLogin = ingresosSinLogin;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public UserAlarm(){
		cuenta_iniciada=false;
	}
	
	
	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getCuenta_iniciada() {
		return cuenta_iniciada;
	}

	public void setCuenta_iniciada(Boolean cuenta_iniciada) {
		this.cuenta_iniciada = cuenta_iniciada;
	}


	public String getPassCuenta() {
		return passCuenta;
	}


	public void setPassCuenta(String passCuenta) {
		this.passCuenta = passCuenta;
	}


	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	

}
