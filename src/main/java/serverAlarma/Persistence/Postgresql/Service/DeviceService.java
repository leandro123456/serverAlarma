package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.DeviceRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDevice;
import serverAlarma.Persistence.Postgresql.Model.Device;

@Service
public class DeviceService implements IDevice{

	@Autowired
	private DeviceRepository devRepo;

	@Override
	public List<Device> getAllDevices() {
		List<Device> devices= devRepo.findAll();
		return devices;
	}

	@Override
	public void saveDevice(Device device) {
		devRepo.save(device);
		
	}

	@Override
	public void deleteDevice(Long id) {
		devRepo.deleteById(id);
		
	}

	@Override
	public Device findDevice(Long id) {
		Device dev= devRepo.findById(id).orElse(null);
		return dev;
	}

	@Override
	public Device findDeviceByType(String type) {
		Device dev= devRepo.findByType(type);
		return dev;
	}
	
}
