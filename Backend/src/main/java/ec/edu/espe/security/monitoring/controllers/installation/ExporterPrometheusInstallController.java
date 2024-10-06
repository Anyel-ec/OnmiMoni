package ec.edu.espe.security.monitoring.controllers.installation;

import ec.edu.espe.security.monitoring.dto.request.ExporterPrometheusRequestDto;
import ec.edu.espe.security.monitoring.dto.response.JsonResponseDto;
import ec.edu.espe.security.monitoring.services.interfaces.installation.PrometheusExporterInstallService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/install/prometheus-exporters")
@AllArgsConstructor
@Slf4j
public class ExporterPrometheusInstallController {

    private final PrometheusExporterInstallService prometheusExporterInstallService;

    /**
     * Endpoint to save or update Prometheus exporter configurations.
     * @param requestDto DTO containing the port configurations for PostgreSQL, MariaDB, and MongoDB.
     * @return JsonResponseDto with success or error message.
     */
    @PutMapping()
    public ResponseEntity<JsonResponseDto> saveOrUpdatePrometheusExporters(
            @RequestBody ExporterPrometheusRequestDto requestDto) {
        try {
            // Llamamos al servicio para guardar o actualizar los exportadores
            prometheusExporterInstallService.saveOrUpdatePrometheusExporters(requestDto);

            // Si odo sale bien, respondemos con un éxito
            JsonResponseDto response = new JsonResponseDto(true, 200, "Exportadores de Prometheus actualizados correctamente", null);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // En caso de error con los parámetros
            JsonResponseDto response = new JsonResponseDto(false, 400, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            // En caso de error interno
            log.error("Error inesperado al actualizar exportadores de Prometheus", e);
            JsonResponseDto response = new JsonResponseDto(false, 500, "Error interno del servidor", null);
            return ResponseEntity.status(500).body(response);
        }
    }
}