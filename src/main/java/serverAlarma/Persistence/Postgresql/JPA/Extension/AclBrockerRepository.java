package serverAlarma.Persistence.Postgresql.JPA.Extension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.Acls;


@Repository
public interface AclBrockerRepository  extends JpaRepository<Acls, Long>{
	
	@Query(value="SELECT * FROM acls WHERE username IN (:username) order by desc limit 1",  nativeQuery=true)
	Acls findByUsername(@Param("username") String username);

}
