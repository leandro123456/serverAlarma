package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.UserAlarm;

public interface IUserAlarm {
	public List<UserAlarm> getAllUserAlarm();
	
	public void saveUserAlarm(UserAlarm acl);
	
	public void deleteUserAlarm(Long id);
	
	public UserAlarm findUserAlarm(Long id);
	
	public UserAlarm findUserAlarmByEmail(String email);
	
	public UserAlarm findUserAlarmByDeviceID(String deviceId);
}
