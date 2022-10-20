package serverAlarma.Persistence.Postgresql.Controller;

import java.util.Base64;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserAlarm;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserPhone;
import serverAlarma.Persistence.Postgresql.Model.UserAlarm;
import serverAlarma.Persistence.Postgresql.Model.UserPhone;

@CrossOrigin(origins = "*")
@RestController
public class UserPhoneController {

	@Autowired
	private IUserPhone iuserphone;
	@Autowired
	private IUserAlarm iuser;
	
	@GetMapping(value="/userphone/createlist/{userMail}")
	public List<String> createListOfPhones(@PathVariable String userMail) {
		String userMaildedoded =new String(Base64.getDecoder().decode(userMail.getBytes()));
		System.out.println(userMaildedoded);
		List<String> listofPhones= iuserphone.findPhonesByUserMail(userMaildedoded);
		return listofPhones;
	}
	
	@PostMapping("/userphone/insertone")
	public String insertPhone(@RequestBody String jsoninfo) {
		JSONObject jsonResponse = new JSONObject();
		try {
			//System.out.println("insert phone: "+ jsoninfo);	
			JSONObject json = new JSONObject(jsoninfo);
			String email=json.getString("email");
			String phone= json.getString("phone");
			UserAlarm user=iuser.findUserAlarmByEmail(email);
			if(user!=null) {
				List<UserPhone> usersPhone=iuserphone.findAllByEmail(email);
				if(usersPhone.isEmpty()) {
					UserPhone newUser= new UserPhone();
					newUser.setEmail(email);
					newUser.setPhone(phone);
					iuserphone.saveUserPhone(newUser);
				}
				else {
					boolean agregar=false;
					for(UserPhone userphone : usersPhone) {
						if(userphone.getEmail().equals(email)&& userphone.getPhone().equals(phone)) {
							jsonResponse.put("result","empty");
							return jsonResponse.toString();
						}
						else if(userphone.getEmail().equals(email)&& !userphone.getPhone().equals(phone) ) {
							agregar=true;
						}
						else if(!userphone.getEmail().equals(email)&& userphone.getPhone().equals(phone) ){
							agregar=true;
						}
					}
					if(agregar) {
						UserPhone newUser= new UserPhone();
						newUser.setEmail(email);
						newUser.setPhone(phone);
						iuserphone.saveUserPhone(newUser);
					}
				}
				jsonResponse.put("result","ok");
			}
			else {
				jsonResponse.put("result","Failed - User dont exist");
			}
			return jsonResponse.toString();
		} catch (Exception e) {
			e.printStackTrace();
			jsonResponse.put("result", "Failed - Contact the support area support@coiaca.com");
			return jsonResponse.toString();
		}
	}
	
	
	
}
