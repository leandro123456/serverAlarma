package serverAlarma.Persistence.Postgresql.JPA.Extension;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.DeviceStatus;

@Repository
public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long>{
	
	DeviceStatus findByDevid(String devid);
}
