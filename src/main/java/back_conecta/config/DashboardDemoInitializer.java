package back_conecta.config;

import back_conecta.entities.DashboardAlertaDemo;
import back_conecta.entities.DashboardDemo;
import back_conecta.entities.DashboardEntregaDemo;
import back_conecta.entities.DashboardLucroMensalDemo;
import back_conecta.entities.DashboardRecebidoDemo;
import back_conecta.repositories.DashboardAlertaDemoRepository;
import back_conecta.repositories.DashboardDemoRepository;
import back_conecta.repositories.DashboardEntregaDemoRepository;
import back_conecta.repositories.DashboardLucroMensalDemoRepository;
import back_conecta.repositories.DashboardRecebidoDemoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"h2", "mysql"})
@Order(2)
public class DashboardDemoInitializer implements ApplicationRunner {
    private static final String DASHBOARD_PRINCIPAL = "principal";

    private final DashboardDemoRepository dashboardRepository;
    private final DashboardRecebidoDemoRepository recebidoRepository;
    private final DashboardAlertaDemoRepository alertaRepository;
    private final DashboardEntregaDemoRepository entregaRepository;
    private final DashboardLucroMensalDemoRepository lucroMensalRepository;

    public DashboardDemoInitializer(
            DashboardDemoRepository dashboardRepository,
            DashboardRecebidoDemoRepository recebidoRepository,
            DashboardAlertaDemoRepository alertaRepository,
            DashboardEntregaDemoRepository entregaRepository,
            DashboardLucroMensalDemoRepository lucroMensalRepository
    ) {
        this.dashboardRepository = dashboardRepository;
        this.recebidoRepository = recebidoRepository;
        this.alertaRepository = alertaRepository;
        this.entregaRepository = entregaRepository;
        this.lucroMensalRepository = lucroMensalRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (dashboardRepository.findByCodigo(DASHBOARD_PRINCIPAL).isPresent()) {
            return;
        }

        DashboardDemo dashboard = new DashboardDemo();
        dashboard.setCodigo(DASHBOARD_PRINCIPAL);
        dashboard.setNomeUsuario("Paulo");
        dashboard.setNomeNegocio("Conecta Doces & Naturais");
        dashboard.setMeta(new BigDecimal("2500.00"));
        dashboard.setLucro(new BigDecimal("1847.50"));
        dashboard.setCusto(new BigDecimal("623.00"));
        dashboard.setInvestido(new BigDecimal("2470.50"));
        dashboard = dashboardRepository.save(dashboard);

        recebidoRepository.saveAll(List.of(
                recebido(dashboard, "hoje", "120.00", 10),
                recebido(dashboard, "semana", "870.00", 42),
                recebido(dashboard, "mes", "2150.00", 118)
        ));

        alertaRepository.saveAll(List.of(
                alerta(dashboard, "aviso", 3),
                alerta(dashboard, "critico", 2),
                alerta(dashboard, "informativo", 1)
        ));

        entregaRepository.saveAll(List.of(
                entrega(dashboard, "1042", "Marina", "Hoje", "16:30"),
                entrega(dashboard, "1043", "João", "Amanhã", "09:00")
        ));

        lucroMensalRepository.saveAll(List.of(
                lucro(dashboard, 1, "Ago", "320.00"),
                lucro(dashboard, 2, "Set", "480.00"),
                lucro(dashboard, 3, "Out", "400.00"),
                lucro(dashboard, 4, "Nov", "760.00"),
                lucro(dashboard, 5, "Dez", "1000.00"),
                lucro(dashboard, 6, "Jan", "590.00")
        ));
    }

    private DashboardRecebidoDemo recebido(
            DashboardDemo dashboard,
            String periodo,
            String valor,
            int pedidos
    ) {
        DashboardRecebidoDemo recebido = new DashboardRecebidoDemo();
        recebido.setDashboard(dashboard);
        recebido.setPeriodo(periodo);
        recebido.setValor(new BigDecimal(valor));
        recebido.setMeta(dashboard.getMeta());
        recebido.setPedidos(pedidos);
        return recebido;
    }

    private DashboardAlertaDemo alerta(DashboardDemo dashboard, String tipo, int quantidade) {
        DashboardAlertaDemo alerta = new DashboardAlertaDemo();
        alerta.setDashboard(dashboard);
        alerta.setTipo(tipo);
        alerta.setQuantidade(quantidade);
        return alerta;
    }

    private DashboardEntregaDemo entrega(
            DashboardDemo dashboard,
            String referencia,
            String nome,
            String dia,
            String hora
    ) {
        DashboardEntregaDemo entrega = new DashboardEntregaDemo();
        entrega.setDashboard(dashboard);
        entrega.setReferencia(referencia);
        entrega.setNome(nome);
        entrega.setDia(dia);
        entrega.setHora(hora);
        return entrega;
    }

    private DashboardLucroMensalDemo lucro(
            DashboardDemo dashboard,
            int ordem,
            String mes,
            String valor
    ) {
        DashboardLucroMensalDemo lucro = new DashboardLucroMensalDemo();
        lucro.setDashboard(dashboard);
        lucro.setOrdem(ordem);
        lucro.setMes(mes);
        lucro.setValor(new BigDecimal(valor));
        return lucro;
    }
}
