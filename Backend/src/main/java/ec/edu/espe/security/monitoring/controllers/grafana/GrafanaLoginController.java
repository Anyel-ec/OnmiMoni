package ec.edu.espe.security.monitoring.controllers.grafana;

import ec.edu.espe.security.monitoring.dto.response.JsonResponseDto;
import ec.edu.espe.security.monitoring.services.interfaces.grafana.GrafanaLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/v1/grafana")
public class GrafanaLoginController {

    private final GrafanaLoginService grafanaLoginService;
    /**
     * Endpoint to log in to Grafana.
     * @return ResponseEntity with a string response from the login service.
     */
    @PostMapping("/grafana-login")
    public ResponseEntity<String> loginToGrafana() {
        return grafanaLoginService.loginToGrafana();
    }

    /**
     * Endpoint to access the Grafana dashboard with an existing session.
     * @param response HttpServletResponse to manage the response handling for dashboard access.
     */
    @GetMapping("/access-dashboard-with-session")
    public void accessDashboardWithSession(HttpServletResponse response) {
        grafanaLoginService.accessDashboardWithSession(response);
    }

    /**
     * Endpoint to log in to Grafana and redirect to the dashboard with session details.
     * @param response HttpServletResponse for managing cookies and redirection headers.
     * @return ResponseEntity containing a JSON response with the redirection URL or an error message.
     */
    @GetMapping("/grafana-login-and-access-dashboard")
    public ResponseEntity<JsonResponseDto> loginAndAccessDashboard(HttpServletResponse response) {
        log.info("Entró al login de Grafana");
        ResponseEntity<String> loginResponse = grafanaLoginService.loginToGrafana();

        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            String grafanaUrl = grafanaLoginService.getGrafanaDashboardUrlWithSession();
            HttpHeaders headers = new HttpHeaders();

            // Agrega las cookies a la respuesta
            grafanaLoginService.getGrafanaCookies().forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie));

            headers.add("Access-Control-Expose-Headers", "Set-Cookie");

            // Devuelve la URL de redirección en un JsonResponseDto
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new JsonResponseDto(true, 200, "Login successful, redirecting to dashboard", Map.of("redirectUrl", grafanaUrl)));
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new JsonResponseDto(false, 401, "Error en el inicio de sesión. No se puede acceder al dashboard.", null));
        }
    }



}
