package serverAlarma.Persistence.Postgresql.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class DeviceType {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String tipo;
	private String owner;
	private String content;
	private Boolean showPublicity;
}
