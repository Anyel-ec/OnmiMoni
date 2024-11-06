package ec.edu.espe.security.monitoring.repositories;

import ec.edu.espe.security.monitoring.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    // Finds an active user by their username
    UserInfo findByUsernameAndIsActiveTrue(String username);

    // Retrieves all active users
    List<UserInfo> findByIsActiveTrue();
}

