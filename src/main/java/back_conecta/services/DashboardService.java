package back_conecta.services;

import back_conecta.dto.DashboardResponse;
import back_conecta.entities.DashboardDemo;
import back_conecta.repositories.UsuarioRepository;
import back_conecta.repositories.DashboardAlertaDemoRepository;
import back_conecta.repositories.DashboardDemoRepository;
import back_conecta.repositories.DashboardEntregaDemoRepository;
import back_conecta.repositories.DashboardLucroMensalDemoRepository;
import back_conecta.repositories.DashboardRecebidoDemoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DashboardService {
    private static final String DASHBOARD_PRINCIPAL = "principal";

    private final DashboardDemoRepository dashboardRepository;
    private final DashboardRecebidoDemoRepository recebidoRepository;
    private final DashboardAlertaDemoRepository alertaRepository;
    private final DashboardEntregaDemoRepository entregaRepository;
    private final DashboardLucroMensalDemoRepository lucroMensalRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardService(
            DashboardDemoRepository dashboardRepository,
            DashboardRecebidoDemoRepository recebidoRepository,
            DashboardAlertaDemoRepository alertaRepository,
            DashboardEntregaDemoRepository entregaRepository,
            DashboardLucroMensalDemoRepository lucroMensalRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.dashboardRepository = dashboardRepository;
        this.recebidoRepository = recebidoRepository;
        this.alertaRepository = alertaRepository;
        this.entregaRepository = entregaRepository;
        this.lucroMensalRepository = lucroMensalRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public DashboardResponse buscar(String periodo, String emailUsuario) {
        DashboardDemo dashboard = dashboardRepository.findByCodigo(DASHBOARD_PRINCIPAL)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dados de demonstração do dashboard não encontrados."
                ));

        var usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "A conta autenticada não foi encontrada."
                ));

        var recebido = recebidoRepository.findAllByDashboardIdOrderById(dashboard.getId()).stream()
                .filter(item -> item.getPeriodo().equals(periodo))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Período inválido: " + periodo
                ));

        return DashboardResponse.from(
                usuario.getNome(),
                usuario.getNomeNegocio(),
                dashboard,
                recebido,
                alertaRepository.findAllByDashboardIdOrderById(dashboard.getId()),
                entregaRepository.findAllByDashboardIdOrderById(dashboard.getId()),
                lucroMensalRepository.findAllByDashboardIdOrderByOrdem(dashboard.getId())
        );
    }
}
