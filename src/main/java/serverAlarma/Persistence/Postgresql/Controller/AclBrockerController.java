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

	@GetMapping("/aclbrocker/createfirst/{name}/{rootdirection}")
	public String CreateAcl(@PathVariable("name") String name,
			@PathVariable("rootdirection") String rootdirection) {
		try {
			Acls acl= new Acls();
			acl.setUsername(name);
			acl.setTopic("homeassistant/#");
			acl.setRw(2);
			iAclBrocker.saveAclsBrocker(acl);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			CreateAcl(name,rootdirection);
		}
		Acls acldev= new Acls();
		acldev.setUsername(name);
		acldev.setTopic(rootdirection+"/#");
		acldev.setRw(4);
		iAclBrocker.saveAclsBrocker(acldev);
		
		System.out.println("termino de guardar acl");
		return "termino de guardar acl";
	}
	
	@GetMapping("/aclbrocker/update/{name}/{deviceid}")
	public String UpdateAcl(@PathVariable("name") String name,
							@PathVariable("deviceid") String rootDirection) {
		try {
			Acls acl= iAclBrocker.findByUsername(name);
			if(acl!=null) {
				System.out.println("el usuario ya existe: name-" +name+" rootDirection: "+ rootDirection);
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Acls acldev= new Acls();
			acldev.setUsername(name);
			acldev.setTopic(rootDirection+"/#");
			acldev.setRw(4);
			iAclBrocker.saveAclsBrocker(acldev);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			UpdateAcl(name,rootDirection);
		}
		
//		Acls aclmntr= new Acls();
//		aclmntr.setUsername(name);
//		aclmntr.setTopic("MNTR/"+deviceid);
//		aclmntr.setRw(1);
//		iAclBrocker.saveAclsBrocker(aclmntr);
//		
//		Acls aclmntrs= new Acls();
//		aclmntrs.setUsername(name);
//		aclmntrs.setTopic("MNTR/"+deviceid+"/#");
//		aclmntrs.setRw(2);
//		iAclBrocker.saveAclsBrocker(aclmntrs);
//		
//		Acls acltele= new Acls();
//		acltele.setUsername(name);
//		acltele.setTopic("teleM/"+deviceid);
//		acltele.setRw(2);
//		iAclBrocker.saveAclsBrocker(acltele);
//		
//		Acls aclrmgt= new Acls();
//		aclrmgt.setUsername(name);
//		aclrmgt.setTopic("RMgmt/"+deviceid);
//		aclrmgt.setRw(1);
//		iAclBrocker.saveAclsBrocker(aclrmgt);
//		
//		Acls aclrmgtr= new Acls();
//		aclrmgtr.setUsername(name);
//		aclrmgtr.setTopic("RMgmt/"+deviceid+"/#");
//		aclrmgtr.setRw(2);
//		iAclBrocker.saveAclsBrocker(aclrmgtr);
		
		System.out.println("termino de actualizar acl topicos de usaurio");
		return "termino de actualizar acl topicos";
	}
	
	@GetMapping("/aclbrocker/getall")
	public List<Acls> getallAcls(){
		return iAclBrocker.getAllAclsBrocker();
	}
	
	@GetMapping("/aclbrocker/reprovisioning")
	public String UpdateAcl() {
		
		String[] roots= {"lean","otro","leandro","cDashSVR"};
		for(String value:roots) {
			Acls acl= new Acls();
			acl.setUsername(value);
			acl.setTopic("#");
			acl.setRw(4);
			iAclBrocker.saveAclsBrocker(acl);
		}
		
		String[] others={"CLWP30000000001","DSC010000000001","DSC010000000002","DSC010000000003","DSC010000000004","DSC010000000005","DSC010000000006","DSC010000000007",
				"DSC010000000008","DSC010000000009","DSC010000000010","DSC010000000011","DSC010000000012","DSC010000000013","DSC010000000014","DSC010000000015",
				"DSC010000000016","DSC010000000017","DSC010000000018","DSC010000000019","DSC010000000020","DSC010000000050","MLD010000000001","PS3S1P120190323",
				"PSWS10000000001","PSWS10000000002","PSWS10000000003","PSWS10000000004","PSWS10000000005","PSWS10000000006","PSWS10000000007","PSWS10000000008",
				"PSWS10000000009","PSWS10000000010","PSWS10000000011","PSWS10000000012","PSWS10000000013","PSWS10000000014","PSWS1DIN0000001","PSWS20000000001",
				"PSWS20000000002","PSWS20000000003","PSWS20000000003","PSWS20000000004","PSWS20000000005","PSWS20000000006","PSWS20000000007","PSWS20000000008",
				"PSWS20000000009","WTHUSB000000001","WTHUSB000000002"};
		for(String value: others) {
			Acls acldev= new Acls();
			acldev.setUsername(value);
			acldev.setTopic(value+"/#");
			acldev.setRw(4);
			iAclBrocker.saveAclsBrocker(acldev);
			
			Acls aclmntr= new Acls();
			aclmntr.setUsername(value);
			aclmntr.setTopic("MNTR/"+value);
			aclmntr.setRw(2);
			iAclBrocker.saveAclsBrocker(aclmntr);
			
			Acls aclmntrs= new Acls();
			aclmntrs.setUsername(value);
			aclmntrs.setTopic("MNTR/"+value+"/#");
			aclmntrs.setRw(2);
			iAclBrocker.saveAclsBrocker(aclmntrs);
			
			Acls acltele= new Acls();
			acltele.setUsername(value);
			acltele.setTopic("teleM/"+value);
			acltele.setRw(2);
			iAclBrocker.saveAclsBrocker(acltele);
			
			Acls aclrmgt= new Acls();
			aclrmgt.setUsername(value);
			aclrmgt.setTopic("RMgmt/"+value);
			aclrmgt.setRw(1);
			iAclBrocker.saveAclsBrocker(aclrmgt);
			
			Acls aclrmgtr= new Acls();
			aclrmgtr.setUsername(value);
			aclrmgtr.setTopic("RMgmt/"+value+"/#");
			aclrmgtr.setRw(2);
			iAclBrocker.saveAclsBrocker(aclrmgtr);
		}
		return "reprovisioning completed";
	}
}
