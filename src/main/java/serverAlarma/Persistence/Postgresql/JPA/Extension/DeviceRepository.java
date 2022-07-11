package serverAlarma.Persistence.Postgresql.JPA.Extension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>{

	Device findByType(String type);
}
