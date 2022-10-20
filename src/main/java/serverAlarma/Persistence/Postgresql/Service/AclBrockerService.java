package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.AclBrockerRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IAclBrocker;
import serverAlarma.Persistence.Postgresql.Model.Acls;

@Service
public class AclBrockerService  implements IAclBrocker{
	@Autowired
	private AclBrockerRepository aclRepository;

	@Override
	public List<Acls> getAllAclsBrocker() {
		List<Acls> users= aclRepository.findAll();
		return users;
	}

	@Override
	public void saveAclsBrocker(Acls userBrocker) {
		aclRepository.save(userBrocker);
		
	}

	@Override
	public void deleteAclsBrocker(Long id) {
		aclRepository.deleteById(id);
		
	}

	@Override
	public Acls findAclsBrocker(Long id) {
		Acls user= aclRepository.findById(id).orElse(null);
		return user;
	}

	@Override
	public Acls findByUsername(String username) {
		Acls acl= aclRepository.findByUsername(username);
		return acl;
	}

}
