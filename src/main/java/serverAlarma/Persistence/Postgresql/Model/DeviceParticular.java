package serverAlarma.Persistence.Postgresql.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Entity
@DynamicUpdate
@Table(name = "device_particular", schema="public")
public class DeviceParticular {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String  devid;
	private String  tipo;
	private String  userowner;
	private String  usershared;
	private String  armedAway;
	private String  armedNite;
	private String  disarm;
	private String  topicSignal;
	private String  topicStatus;
	private String  topicTrouble;
	private String  armedHome;
	private String  topicComands;
	private String  topicActPartition;
	private String  topicMsg;
	private String  activeOn;
	private String  activeOff;
	private String deviceName;
	private int activeZones;
	private String topicZones;
		
}
