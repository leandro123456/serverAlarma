package serverAlarma.Persistence.Postgresql.JPA.Extension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.UserAlarm;

@Repository
public interface UserAlarmRepository extends JpaRepository<UserAlarm, Long>{

	UserAlarm findByEmail(String email);
	
	@Query(value="SELECT userID FROM user_alarm WHERE device_ids IN (:devid)",  nativeQuery=true)
	UserAlarm findByDeviceId(@Param("devid") String devid);
}
