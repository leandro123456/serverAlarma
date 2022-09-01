package serverAlarma.Persistence.Postgresql.JPA.Extension;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;

@Repository
public interface DeviceParticularRepository extends JpaRepository<DeviceParticular, Long>{

	DeviceParticular findByUserowner(String userowner);
	
	DeviceParticular findByDevid(String devid);
	
	@Query(value="SELECT owner FROM device_particular WHERE devid IN (:devid)",  nativeQuery=true)
	String findByDeviceId(@Param("devid") String devid);
	
	@Query(value="SELECT * FROM device_particular WHERE userowner =:userowner",  nativeQuery=true)
	List<DeviceParticular> findAllByUserOwner(@Param("userowner") String owner);
	
	@Query(value="SELECT * FROM device_particular WHERE devid IN (:devid)",  nativeQuery=true)
	DeviceParticular findAllByDeviceId(@Param("devid") String devid);
}
