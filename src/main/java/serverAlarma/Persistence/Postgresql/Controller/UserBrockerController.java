package serverAlarma.Persistence.Postgresql.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserBrocker;
import serverAlarma.Persistence.Postgresql.Model.Users;
import serverAlarma.util.Utils;

@CrossOrigin(origins = "*")
@RestController
public class UserBrockerController {
	@Autowired
	private IUserBrocker iUserBrocker;

	@GetMapping("/userbrocker/create/{name}/{pass}")
	public String CreateUser(@PathVariable("name") String mqttName,
							@PathVariable("pass") String mqttPass) {
		try {
		String passHashed= Utils.hashPassword(mqttPass);
		Users user= new Users();
		user.setIs_admin(false);
		user.setUsername(mqttName);
		user.setPassword_hash(passHashed);
		iUserBrocker.saveUserBrocker(user);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			CreateUser(mqttName,mqttPass);
		}
		System.out.println("termino de guardar");
		return "termino de guardar";
	}
	
	@GetMapping("/userbrocker/getall")
	public List<Users> getallUsers(){
		return iUserBrocker.getAllUserBrocker();
	}
	
}
