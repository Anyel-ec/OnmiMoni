package ec.edu.espe.security.monitoring.feature.grafana.services.impl;

import ec.edu.espe.security.monitoring.feature.installation.models.InstallationConfig;
import ec.edu.espe.security.monitoring.common.system.models.SystemParameters;
import ec.edu.espe.security.monitoring.feature.installation.repositories.InstallationConfigRepository;
import ec.edu.espe.security.monitoring.common.system.repositories.SystemParametersRepository;
import ec.edu.espe.security.monitoring.feature.grafana.services.interfaces.GrafanaCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrafanaCredentialServiceImpl implements GrafanaCredentialService {

    private final InstallationConfigRepository installationConfigRepository;
    private final SystemParametersRepository systemParametersRepository;

    public SystemParameters getGrafanaInstallParameter() {
        return systemParametersRepository
                .findByNameAndIsActiveTrue("GRAFANA_INSTALL")
                .orElseThrow(() -> new IllegalArgumentException("GRAFANA_INSTALL parameter not found"));
    }

    public InstallationConfig getActiveInstallationConfig(SystemParameters systemParameter) {
        return installationConfigRepository
                .findFirstBySystemParameterAndIsActiveTrue(systemParameter)
                .orElseThrow(() -> new IllegalArgumentException("No active installation found for GRAFANA_INSTALL"));
    }
}