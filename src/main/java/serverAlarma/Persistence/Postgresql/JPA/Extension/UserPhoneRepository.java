package serverAlarma.Persistence.Postgresql.JPA.Extension;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.UserPhone;

@Repository
public interface UserPhoneRepository extends JpaRepository<UserPhone, Long>{

	@Query(value="SELECT * FROM user_phone WHERE email IN (:email)",  nativeQuery=true)
	List<UserPhone> findAllByEmail(@Param("email") String email);
	
	@Query(value="SELECT * FROM user_phone WHERE phone IN (:phone)",  nativeQuery=true)
	List<UserPhone> findAllByPhone(@Param("phone") String phone);
	
	@Query(value="SELECT phone FROM user_alarm join user_phone using(email) where email IN (:emailr)",nativeQuery=true)
	List<String> findPhonesByUserMail(@Param("emailr") String emailr);
}
