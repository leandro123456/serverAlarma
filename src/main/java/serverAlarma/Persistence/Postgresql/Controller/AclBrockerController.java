package serverAlarma.Persistence.Postgresql.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IAclBrocker;
import serverAlarma.Persistence.Postgresql.Model.Acls;

@CrossOrigin(origins = "*")
@RestController
public class AclBrockerController {
	@Autowired
	private IAclBrocker iAclBrocker;

	@GetMapping("/aclbrocker/createfirst/{name}")
	public String CreateAcl(@PathVariable("name") String name) {
		try {
			Acls acl= new Acls();
			acl.setUsername(name);
			acl.setTopic("homeassistant/#");
			acl.setRw(2);
			iAclBrocker.saveAclsBrocker(acl);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			CreateAcl(name);
		}
		System.out.println("termino de guardar acl");
		return "termino de guardar acl";
	}
	
	@GetMapping("/aclbrocker/update/{name}/{deviceid}")
	public String UpdateAcl(@PathVariable("name") String name,
							@PathVariable("deviceid") String deviceid) {
		try {
			Acls acldev= new Acls();
			acldev.setUsername(name);
			acldev.setTopic(deviceid+"/#");
			acldev.setRw(4);
			iAclBrocker.saveAclsBrocker(acldev);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			UpdateAcl(name,deviceid);
		}
		
		Acls aclmntr= new Acls();
		aclmntr.setUsername(name);
		aclmntr.setTopic("MNTR/"+deviceid);
		aclmntr.setRw(1);
		iAclBrocker.saveAclsBrocker(aclmntr);
		
		Acls aclmntrs= new Acls();
		aclmntrs.setUsername(name);
		aclmntrs.setTopic("MNTR/"+deviceid+"/#");
		aclmntrs.setRw(2);
		iAclBrocker.saveAclsBrocker(aclmntrs);
		
		Acls acltele= new Acls();
		acltele.setUsername(name);
		acltele.setTopic("teleM/"+deviceid);
		acltele.setRw(2);
		iAclBrocker.saveAclsBrocker(acltele);
		
		Acls aclrmgt= new Acls();
		aclrmgt.setUsername(name);
		aclrmgt.setTopic("RMgmt/"+deviceid);
		aclrmgt.setRw(1);
		iAclBrocker.saveAclsBrocker(aclrmgt);
		
		Acls aclrmgtr= new Acls();
		aclrmgtr.setUsername(name);
		aclrmgtr.setTopic("RMgmt/"+deviceid+"/#");
		aclrmgtr.setRw(2);
		iAclBrocker.saveAclsBrocker(aclrmgtr);
		
		System.out.println("termino de actualizar acl topicos de usaurio");
		return "termino de actualizar acl topicos";
	}
	
	@GetMapping("/aclbrocker/getall")
	public List<Acls> getallAcls(){
		return iAclBrocker.getAllAclsBrocker();
	}
	
}
