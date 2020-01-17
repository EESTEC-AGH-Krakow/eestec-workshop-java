package pl.eestec.workshop.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.eestec.workshop.java.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
