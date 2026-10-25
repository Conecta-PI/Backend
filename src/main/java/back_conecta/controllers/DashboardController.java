package back_conecta.controllers;

import back_conecta.dto.DashboardResponse;
import back_conecta.services.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse buscar(
            @RequestParam String periodo,
            @AuthenticationPrincipal Jwt token
    ) {
        return dashboardService.buscar(periodo, token.getSubject());
    }
}
