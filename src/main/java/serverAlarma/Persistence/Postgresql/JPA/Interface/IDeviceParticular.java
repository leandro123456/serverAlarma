package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;

public interface IDeviceParticular {

	public List<DeviceParticular> getAllDevicesParcular();
	
	public void saveDeviceParticular(DeviceParticular acl);
	
	public void deleteDeviceParticular(Long id);
	
	public DeviceParticular findDeviceParticular(Long id);
	
	public DeviceParticular findDeviceByOwner(String owner);
	
	public List<DeviceParticular> findAllByOwner(String owner);
	
	public DeviceParticular findByDevid(String deviceid);
	
	public DeviceParticular findAllByDeviceId(String deviceid);
}