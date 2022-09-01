package serverAlarma.Persistence.Postgresql.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class DeviceStatus {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String devid;
	private String tipo;
	private int actPartition;
	private String status;
	private int trouble;
	private String importData;
	private int showZones;
}
