package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.Device;

public interface IDevice {

	public List<Device> getAllDevices();
	
	public void saveDevice(Device acl);
	
	public void deleteDevice(Long id);
	
	public Device findDevice(Long id);
	
	public Device findDeviceByType(String type);
	
}