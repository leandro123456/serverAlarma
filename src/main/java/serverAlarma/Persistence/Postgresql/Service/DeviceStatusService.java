package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.DeviceStatusRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceStatus;
import serverAlarma.Persistence.Postgresql.Model.DeviceStatus;

@Service
public class DeviceStatusService implements IDeviceStatus{

	@Autowired
	private DeviceStatusRepository devRepo;

	@Override
	public List<DeviceStatus> getAllDeviceStatus() {
		List<DeviceStatus> devices= devRepo.findAll();
		return devices;
	}

	@Override
	public void saveDeviceStatus(DeviceStatus device) {
		devRepo.save(device);
		
	}

	@Override
	public void deleteDeviceStatus(Long id) {
		devRepo.deleteById(id);
		
	}

	@Override
	public DeviceStatus findByDevid(String deviceid) {
		DeviceStatus dev= devRepo.findByDevid(deviceid);
		return dev;
	}
	
}
