package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.Users;

public interface IUserBrocker {

	public List<Users> getAllUserBrocker();
	
	public void saveUserBrocker(Users user);
	
	public void deleteUserBrocker(Long id);
	
	public Users findUserBrocker(Long id);
}
