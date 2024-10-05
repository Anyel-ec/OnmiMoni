package ec.edu.espe.security.monitoring.repositories;

import ec.edu.espe.security.monitoring.models.SystemParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemParametersRepository extends JpaRepository<SystemParameters, Long> {

    // Método para buscar por nombre
    Optional<SystemParameters> findByNameAndIsActiveTrue(String name);
}