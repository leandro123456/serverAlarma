package serverAlarma.Persistence.Postgresql.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceType;
import serverAlarma.Persistence.Postgresql.Model.DeviceType;

@CrossOrigin(origins = "*")
@RestController
public class DeviceTypeController {

	@Autowired
	private IDeviceType itype;
	
	@GetMapping("/devicetype/getall")
	public List<DeviceType> ObtainAllDeviceType() {
		List<DeviceType> list= itype.getAllDeviceTypes();
		return list;
	}
	
	@GetMapping("/devicetype/createfirst")
	public String createFirstDeviceType() {
		try {
			DeviceType dev= itype.findByTipo("DSC01");
			if(dev ==null) {
				DeviceType type= new DeviceType();
				type.setTipo("DSC01");
				type.setContent("");
				type.setShowPublicity(false);
				type.setOwner("Coiaca");
				itype.saveDeviceType(type);
				return "type DSC01 was created success";
			}
			else
				return "ERROR- DSC01 already exist ";
		} catch (Exception e) {
			return e.getMessage();
		}
		
	}
	
}
