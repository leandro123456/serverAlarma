package serverAlarma.Persistence.Postgresql.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class UserAlarm {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String firstname;
	private String lastname;
	private String password;
	private String email;
	private String passCuenta;
	private Boolean cuenta_iniciada;
	private String role;
	private String fechaCreacion;
	private String fechaUltimoIngrego;
	private Integer ingresosSinLogin;
	private String mqttUser;
	private String mqttPassword;
	private String userID;
}
