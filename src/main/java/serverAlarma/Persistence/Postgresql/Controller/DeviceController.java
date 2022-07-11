package serverAlarma.Persistence.Postgresql.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IDevice;
import serverAlarma.Persistence.Postgresql.Model.Device;

@CrossOrigin(origins = "*")
@RestController
public class DeviceController {

	@Autowired
	private IDevice idevice;
	
	@GetMapping("/device/deviceid/{type}")
	public String ObtainDeviceID(@PathVariable("type") String type) {
		Device device=idevice.findDeviceByType(type);
		if(device==null) {
			device=new Device();
			device.setName("primero");
			device.setSerialnumber(type+"0000000100");
			device.setUserowner(type+"usuario-inicial");
			device.setType(type);
			idevice.saveDevice(device);
		}
		Integer number= Integer.parseInt(device.getSerialnumber().replace(type, ""));
		String newDeviceId= (number+1)+"";
		newDeviceId=device.getSerialnumber().substring(0,device.getSerialnumber().length()-newDeviceId.length())+newDeviceId;
		System.out.println("New Device id: "+newDeviceId);
		device.setSerialnumber(newDeviceId);
		idevice.saveDevice(device);
		return newDeviceId;
	}
	
}
