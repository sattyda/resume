
package Resume.Resume.repository;

import Resume.Resume.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByEmail(String email);


    @Modifying(clearAutomatically = true)
    @Query(value = "update user set resume=?1 where id=?2" , nativeQuery=true)
    void update(String mylink , Long id);

    List<User> findAllByUsername(String username);
}
