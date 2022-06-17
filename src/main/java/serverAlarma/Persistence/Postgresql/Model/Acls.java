package serverAlarma.Persistence.Postgresql.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Acls {
	@Id
	@SequenceGenerator(name = "mySeqGen", sequenceName = "mySeq", initialValue = 2000, allocationSize = 100)
	@GeneratedValue(generator = "mySeqGen")
	private long id;
	private String username;
	private String topic;
	private Integer rw;
}
