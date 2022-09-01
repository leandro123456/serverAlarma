package serverAlarma.Persistence.Postgresql.JPA.Interface;

import java.util.List;

import serverAlarma.Persistence.Postgresql.Model.DeviceType;

public interface IDeviceType {

	public List<DeviceType> getAllDeviceTypes();
	
	public void saveDeviceType(DeviceType acl);
	
	public void deleteDeviceType(Long id);
	
	public DeviceType findByTipo(String devicetipo);
}