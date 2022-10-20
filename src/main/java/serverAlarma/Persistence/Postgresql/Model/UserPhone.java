package serverAlarma.Persistence.Postgresql.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class UserPhone {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String email;
	private String info;
	private String description;
	private String phone;
}
