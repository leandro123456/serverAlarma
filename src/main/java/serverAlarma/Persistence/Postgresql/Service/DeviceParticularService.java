package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.DeviceParticularRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;

@Service
public class DeviceParticularService implements IDeviceParticular{

	@Autowired
	private DeviceParticularRepository devRepo;

	@Override
	public List<DeviceParticular> getAllDevicesParcular() {
		List<DeviceParticular> devices= devRepo.findAll();
		return devices;
	}

	@Override
	public void saveDeviceParticular(DeviceParticular device) {
		devRepo.save(device);
		
	}

	@Override
	public void deleteDeviceParticular(Long id) {
		devRepo.deleteById(id);
		
	}

	@Override
	public DeviceParticular findDeviceParticular(Long id) {
		DeviceParticular dev= devRepo.findById(id).orElse(null);
		return dev;
	}


	@Override
	public DeviceParticular findDeviceByOwner(String owner) {
		DeviceParticular dev=devRepo.findByUserowner(owner);
		return dev;
	}

	@Override
	public List<DeviceParticular> findAllByOwner(String owner) {
		List<DeviceParticular> devices= devRepo.findAllByUserOwner(owner);
		return devices;
	}

	@Override
	public DeviceParticular findByDeviceid(String deviceid) {
		DeviceParticular dev= devRepo.findByDeviceid(deviceid);
		return dev;
	}
	
}
