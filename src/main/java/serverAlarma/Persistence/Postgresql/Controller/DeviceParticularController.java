package serverAlarma.Persistence.Postgresql.Controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;

@CrossOrigin(origins = "*")
@RestController
public class DeviceParticularController {
	@Autowired
	private IDeviceParticular idevice;
	
	//, consumes = "application/json"
	@PostMapping(value="/useralarm/getdevices")
	public String AllDevicesByuser(@RequestBody String jsoninfo) {
		JSONObject jsonRequest = new JSONObject(jsoninfo);
		String owner= jsonRequest.getString("mail");
		List<DeviceParticular> devices= idevice.findAllByOwner(owner);
		String resp="";
		if(!devices.isEmpty()) {
			for(DeviceParticular dev: devices) {
				resp=resp+"-"+dev.getDeviceid();
			}
			return resp;
		}
		else
			return "List empty";
		}
}
