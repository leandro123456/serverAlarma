package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.Acls;

public interface IAclBrocker {

	public List<Acls> getAllAclsBrocker();
	
	public void saveAclsBrocker(Acls acl);
	
	public void deleteAclsBrocker(Long id);
	
	public Acls findAclsBrocker(Long id);
}
