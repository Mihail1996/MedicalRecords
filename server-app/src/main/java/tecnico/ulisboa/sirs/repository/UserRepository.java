package tecnico.ulisboa.sirs.repository;

import tecnico.ulisboa.sirs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByCard(String cardId);
}