package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.DeviceStatus;

public interface IDeviceStatus {

	public List<DeviceStatus> getAllDeviceStatus();
	
	public void saveDeviceStatus(DeviceStatus acl);
	
	public void deleteDeviceStatus(Long id);
	
	public DeviceStatus findByDevid(String deviceid);
}