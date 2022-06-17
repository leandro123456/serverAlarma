package serverAlarma.Persistence.Postgresql.JPA.Extension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import serverAlarma.Persistence.Postgresql.Model.Users;


@Repository
public interface UserBrockerRepository  extends JpaRepository<Users, Long>{

}
