package serverAlarma.Persistence.Postgresql.JPA.Extension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.DeviceType;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long>{
	
	DeviceType findByTipo(String tipo);
}
