package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.UserBrockerRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserBrocker;
import serverAlarma.Persistence.Postgresql.Model.Users;

@Service
public class UserBrockerService  implements IUserBrocker{
	@Autowired
	private UserBrockerRepository userRepository;

	@Override
	public List<Users> getAllUserBrocker() {
		List<Users> users= userRepository.findAll();
		return users;
	}

	@Override
	public void saveUserBrocker(Users userBrocker) {
		userRepository.save(userBrocker);
		
	}

	@Override
	public void deleteUserBrocker(Long id) {
		userRepository.deleteById(id);
		
	}

	@Override
	public Users findUserBrocker(Long id) {
		Users user= userRepository.findById(id).orElse(null);
		return user;
	}

}
