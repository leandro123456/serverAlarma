package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.UserAlarmRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserAlarm;
import serverAlarma.Persistence.Postgresql.Model.UserAlarm;

@Service
public class UserAlarmService implements IUserAlarm{

	@Autowired
	private UserAlarmRepository userRepo;

	@Override
	public List<UserAlarm> getAllUserAlarm() {
		List<UserAlarm> users= userRepo.findAll();
		return users;
	}

	@Override
	public void saveUserAlarm(UserAlarm user) {
		userRepo.save(user);
		
	}

	@Override
	public void deleteUserAlarm(Long id) {
		userRepo.deleteById(id);
		
	}

	@Override
	public UserAlarm findUserAlarm(Long id) {
		UserAlarm user=userRepo.findById(id).orElse(null);
		return user;
	}

	@Override
	public UserAlarm findUserAlarmByEmail(String email) {
		UserAlarm user=userRepo.findByEmail(email);
		return user;
	}

	@Override
	public UserAlarm findUserAlarmByDeviceID(String deviceId) {
		UserAlarm user=userRepo.findByDeviceId(deviceId);
		return user;
	}
}
