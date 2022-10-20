package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.UserPhoneRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserPhone;
import serverAlarma.Persistence.Postgresql.Model.UserPhone;

@Service
public class UserPhoneService implements IUserPhone{

	@Autowired
	private UserPhoneRepository phoneRepo;

	@Override
	public void saveUserPhone(UserPhone user) {
		phoneRepo.save(user);
	}

	@Override
	public void deleteUserPhone(Long id) {
		phoneRepo.deleteById(id);
	}

	@Override
	public UserPhone findUserPhone(Long id) {
		UserPhone user=phoneRepo.findById(id).orElse(null);
		return user;
	}

	@Override
	public List<UserPhone> getAllDevices() {
		List<UserPhone> phones=phoneRepo.findAll();
		return phones;
	}

	@Override
	public List<UserPhone> findAllByEmail(String email) {
		List<UserPhone> phones=phoneRepo.findAllByEmail(email);
		return phones;
	}

	@Override
	public List<UserPhone> findAllByPhone(String phone) {
		List<UserPhone> phones=phoneRepo.findAllByPhone(phone);
		return phones;
	}

	@Override
	public List<String> findPhonesByUserMail(String userMail) {
		List<String> phones= phoneRepo.findPhonesByUserMail(userMail);
		return phones;
	}
}
