package ec.edu.espe.security.monitoring.repositories;

import ec.edu.espe.security.monitoring.models.DatabaseCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatabaseCredentialRepository extends JpaRepository<DatabaseCredential, Long> {
    Optional<DatabaseCredential> findByHost(String host);
}