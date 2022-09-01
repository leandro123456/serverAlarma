package serverAlarma.Persistence.Postgresql.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import serverAlarma.Persistence.Postgresql.JPA.Extension.DeviceTypeRepository;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceType;
import serverAlarma.Persistence.Postgresql.Model.DeviceType;

@Service
public class DeviceTypeService implements IDeviceType{

	@Autowired
	private DeviceTypeRepository devRepo;

	@Override
	public List<DeviceType> getAllDeviceTypes() {
		List<DeviceType> devices= devRepo.findAll();
		return devices;
	}

	@Override
	public void saveDeviceType(DeviceType device) {
		devRepo.save(device);
		
	}

	@Override
	public void deleteDeviceType(Long id) {
		devRepo.deleteById(id);
		
	}
	
	@Override
	public DeviceType findByTipo(String devicetipo) {
		DeviceType deviceType=devRepo.findByTipo(devicetipo);
		return deviceType;
	}
	
}
