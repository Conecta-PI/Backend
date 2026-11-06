package back_conecta.dto;

import back_conecta.entities.DashboardAlertaDemo;
import back_conecta.entities.DashboardDemo;
import back_conecta.entities.DashboardEntregaDemo;
import back_conecta.entities.DashboardLucroMensalDemo;
import back_conecta.entities.DashboardRecebidoDemo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record DashboardResponse(
        Usuario usuario,
        Recebido recebido,
        List<Alerta> alertas,
        List<Entrega> proximasEntregas,
        Metricas metricas,
        List<LucroMensal> lucroMensal
) {
    public static DashboardResponse from(
            String nomeUsuario,
            String nomeNegocio,
            DashboardDemo dashboard,
            DashboardRecebidoDemo recebido,
            List<DashboardAlertaDemo> alertas,
            List<DashboardEntregaDemo> entregas,
            List<DashboardLucroMensalDemo> lucroMensal
    ) {
        Objects.requireNonNull(dashboard, "Dashboard principal não pode ser nulo");
        Objects.requireNonNull(recebido, "Dados de recebimento do dashboard não podem ser nulos");

        return new DashboardResponse(
                new Usuario(nomeUsuario, nomeNegocio),
                new Recebido(
                        recebido.getValor() == null ? BigDecimal.ZERO : recebido.getValor(),
                        dashboard.getMeta() == null ? BigDecimal.ZERO : dashboard.getMeta(),
                        recebido.getPedidos() == null ? 0 : recebido.getPedidos()
                ),
                alertas == null ? List.of() : alertas.stream()
                        .filter(Objects::nonNull)
                        .map(alerta -> new Alerta(alerta.getTipo(), alerta.getQuantidade() == null ? 0 : alerta.getQuantidade()))
                        .toList(),
                entregas == null ? List.of() : entregas.stream()
                        .filter(Objects::nonNull)
                        .map(entrega -> new Entrega(
                                entrega.getReferencia(),
                                entrega.getNome(),
                                entrega.getDia(),
                                entrega.getHora()
                        ))
                        .toList(),
                new Metricas(
                        dashboard.getLucro() == null ? BigDecimal.ZERO : dashboard.getLucro(),
                        dashboard.getCusto() == null ? BigDecimal.ZERO : dashboard.getCusto(),
                        dashboard.getInvestido() == null ? BigDecimal.ZERO : dashboard.getInvestido()
                ),
                lucroMensal == null ? List.of() : lucroMensal.stream()
                        .filter(Objects::nonNull)
                        .map(mes -> new LucroMensal(mes.getMes(), mes.getValor() == null ? BigDecimal.ZERO : mes.getValor()))
                        .toList()
        );
    }

    public record Usuario(String nome, String negocio) {}

    public record Recebido(BigDecimal valor, BigDecimal meta, Integer pedidos) {}

    public record Alerta(String tipo, Integer quantidade) {}

    public record Entrega(String id, String nome, String dia, String hora) {}

    public record Metricas(BigDecimal lucro, BigDecimal custo, BigDecimal investido) {}

    public record LucroMensal(String mes, BigDecimal valor) {}
}
