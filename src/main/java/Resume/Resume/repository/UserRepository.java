package Resume.Resume.repository;

import Resume.Resume.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByEmail(String email);
}
