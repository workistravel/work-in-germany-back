package pl.dernovyi.workingermanyback.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.workingermanyback.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    void deleteByEmail(String email);
}
