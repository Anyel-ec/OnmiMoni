package ec.edu.espe.security.monitoring.services.impl;

import ec.edu.espe.security.monitoring.dto.request.GrafanaInstallRequestDto;
import ec.edu.espe.security.monitoring.dto.request.PrometheusInstallRequestDto;
import ec.edu.espe.security.monitoring.dto.request.UserInstallRequestDto;
import ec.edu.espe.security.monitoring.models.InstallationConfig;
import ec.edu.espe.security.monitoring.models.SystemParameters;
import ec.edu.espe.security.monitoring.repositories.InstallationConfigRepository;
import ec.edu.espe.security.monitoring.repositories.SystemParametersRepository;
import ec.edu.espe.security.monitoring.services.interfaces.InstallationConfigService;
import ec.edu.espe.security.monitoring.utils.AesEncryptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class InstallationConfigServiceImpl implements InstallationConfigService {

    private final InstallationConfigRepository installationConfigRepository;
    private final SystemParametersRepository systemParametersRepository;
    private final AesEncryptor aesEncryptor;

    // Save Grafana installation credentials with encrypted password
    @Override
    public InstallationConfig saveGrafanaInstall(GrafanaInstallRequestDto grafanaInstallRequestDto) {
        try {
            SystemParameters systemParameter = systemParametersRepository
                    .findByNameAndIsActiveTrue("GRAFANA_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("GRAFANA_INSTALL parameter not found"));

            // Encrypt the password
            String encryptedPassword = aesEncryptor.encrypt(grafanaInstallRequestDto.getPassword());

            InstallationConfig grafanaInstall = installationConfigRepository
                    .findFirstBySystemParameterAndIsActiveTrue(systemParameter)
                    .orElse(null);

            if (grafanaInstall != null) {
                grafanaInstall.setPassword(encryptedPassword);
                grafanaInstall.setInternalPort(grafanaInstallRequestDto.getInternalPort());
                grafanaInstall.setExternalPort(grafanaInstallRequestDto.getExternalPort());
                grafanaInstall.setSystemParameter(systemParameter);
                grafanaInstall.setIsActive(true);
                log.error("Se actualiza las credenciales de Grafana");
            } else {
                grafanaInstall = InstallationConfig.builder()
                        .usuario(grafanaInstallRequestDto.getUsuario())
                        .password(encryptedPassword)
                        .internalPort(grafanaInstallRequestDto.getInternalPort())
                        .externalPort(grafanaInstallRequestDto.getExternalPort())
                        .systemParameter(systemParameter)
                        .isActive(true)
                        .build();
            }
            log.info("Lo que se va ha instalar de grafana es: {}", grafanaInstall);
            // Save the InstallationConfig entity to the database
            return installationConfigRepository.save(grafanaInstall);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al guardar la instalación de Grafana", e);
            throw new IllegalStateException("Error interno del servidor al guardar la instalación de Grafana", e);
        }
    }

    @Override
    public InstallationConfig getGrafanaInstall() {
        try {
            // Search for the system parameter "GRAFANA_INSTALL"
            SystemParameters systemParameter = systemParametersRepository
                    .findByNameAndIsActiveTrue("GRAFANA_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("GRAFANA_INSTALL parameter not found"));

            // Search for the active installation associated with the found SystemParameter
            Optional<InstallationConfig> optionalInstall = installationConfigRepository
                    .findFirstBySystemParameterAndIsActiveTrue(systemParameter);

            // If no installation is found, throw an exception
            if (optionalInstall.isEmpty()) {
                throw new IllegalArgumentException("No se encontró una instalación activa para GRAFANA_INSTALL");
            }

            InstallationConfig grafanaInstall = optionalInstall.get();

            // Decrypt the password
            String decryptedPassword = aesEncryptor.decrypt(grafanaInstall.getPassword());
            grafanaInstall.setPassword(decryptedPassword);

            // Return the InstallationConfig object with the decrypted password
            return grafanaInstall;
        } catch (Exception e) {
            throw new IllegalStateException("Error al obtener la instalación de Grafana", e);
        }
    }



    @Override
    public InstallationConfig savePrometheusInstall(PrometheusInstallRequestDto prometheusInstallRequestDto) {
        try {
            // Fetch the PROMETHEUS_INSTALL system parameter
            SystemParameters systemParameter = systemParametersRepository
                    .findByNameAndIsActiveTrue("PROMETHEUS_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("El parámetro PROMETHEUS_INSTALL no fue encontrado"));

            // Check if a Prometheus installation with this parameter already exists
            InstallationConfig prometheusInstall = installationConfigRepository
                    .findFirstBySystemParameterAndIsActiveTrue(systemParameter)
                    .orElse(null);

            // If it exists, update the necessary fields
            if (prometheusInstall != null) {
                prometheusInstall.setInternalPort(prometheusInstallRequestDto.getInternalPort());
                prometheusInstall.setExternalPort(prometheusInstallRequestDto.getExternalPort());
            } else {
                // If it doesn't exist, create a new installation
                prometheusInstall = InstallationConfig.builder()
                        .internalPort(prometheusInstallRequestDto.getInternalPort())
                        .externalPort(prometheusInstallRequestDto.getExternalPort())
                        .systemParameter(systemParameter)
                        .isActive(true)
                        .build();
            }

            // Save or update the installation in the database
            return installationConfigRepository.save(prometheusInstall);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e; // Rethrow to handle specific exception (400 Bad Request)
        } catch (Exception e) {
            log.error("Error inesperado al guardar la instalación de Prometheus", e);
            throw new IllegalStateException("Error interno del servidor al guardar la instalación de Prometheus", e); // Handle unexpected errors (500)
        }
    }


    @Override
    public InstallationConfig getPrometheusInstall() {
        try {
            // Search for the active system parameter PROMETHEUS_INSTALL
            SystemParameters systemParameter = systemParametersRepository
                    .findByNameAndIsActiveTrue("PROMETHEUS_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("El parámetro PROMETHEUS_INSTALL no fue encontrado"));

            // Search for the Prometheus installation configuration using the system parameter
            return installationConfigRepository
                    .findFirstBySystemParameterAndIsActiveTrue(systemParameter)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la configuración de instalación de Prometheus"));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e; // Throw specific exception (400 Bad Request)
        } catch (Exception e) {
            log.error("Error inesperado al recuperar la instalación de Prometheus", e);
            throw new IllegalStateException("Error interno del servidor al recuperar la instalación de Prometheus", e); // Error 500
        }
    }



    public InstallationConfig saveUserInstall(UserInstallRequestDto userInstallRequestDto) {
        try {
            // Fetch the USERS_INSTALL system parameter
            SystemParameters systemParameter = systemParametersRepository
                    .findByNameAndIsActiveTrue("USERS_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("El parámetro USERS_INSTALL no fue encontrado"));

            // Encrypt the password
            String encryptedPassword = aesEncryptor.encrypt(userInstallRequestDto.getPassword());

            // Build the InstallationConfig for user registration
            InstallationConfig userInstall = InstallationConfig.builder()
                    .usuario(userInstallRequestDto.getUsuario())
                    .password(encryptedPassword)
                    .numberPhone(userInstallRequestDto.getNumberPhone())
                    .email(userInstallRequestDto.getEmail())
                    .systemParameter(systemParameter)
                    .isActive(true)
                    .build();

            // Save the InstallationConfig entity to the database
            return installationConfigRepository.save(userInstall);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e; // Rethrow to handle specific exception (400 Bad Request)
        } catch (Exception e) {
            log.error("Error inesperado al guardar el registro de usuario", e);
            throw new IllegalStateException("Error interno del servidor al guardar el registro de usuario", e); // Handle unexpected errors (500)
        }
    }

    @Override
    public List<InstallationConfig> getActiveInstallations() {
        return installationConfigRepository.findByIsActiveTrue();
    }

    @Override
    public boolean isInstallationComplete() {
        try {
            // Fetch the system parameter COMPLETE_INSTALL
            SystemParameters completeInstallParam = systemParametersRepository
                    .findByNameAndIsActiveTrue("COMPLETE_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("El parámetro COMPLETE_INSTALL no fue encontrado"));

            // Check if the installation is marked as complete (case-insensitive comparison)
            return completeInstallParam.getParamValue() != null && completeInstallParam.getParamValue().equalsIgnoreCase("1");

        } catch (Exception e) {
            log.error("Error al verificar el estado de la instalación", e);
            throw new IllegalStateException("Error interno del servidor al verificar el estado de la instalación", e);
        }
    }


    public SystemParameters updateCompleteInstallParameter() {
        try {
            SystemParameters completeInstallParam = systemParametersRepository
                    .findByNameAndIsActiveTrue("COMPLETE_INSTALL")
                    .orElseThrow(() -> new IllegalArgumentException("El parámetro COMPLETE_INSTALL no fue encontrado"));

            completeInstallParam.setParamValue("1");

            return systemParametersRepository.save(completeInstallParam);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("No se pudo actualizar el parámetro COMPLETE_INSTALL", e);
        }
    }



}
