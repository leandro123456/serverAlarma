package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.UserPhone;

public interface IUserPhone {

	public List<UserPhone> getAllDevices();
	
	public void saveUserPhone(UserPhone phone);
	
	public void deleteUserPhone(Long id);
	
	public UserPhone findUserPhone(Long id);
	
	public List<UserPhone> findAllByPhone(String phone);
	
	public List<UserPhone> findAllByEmail(String email);

	public List<String> findPhonesByUserMail(String userMail);
	
}