package back_conecta.dto;

import back_conecta.entities.DashboardDemo;
import back_conecta.entities.DashboardAlertaDemo;
import back_conecta.entities.DashboardEntregaDemo;
import back_conecta.entities.DashboardLucroMensalDemo;
import back_conecta.entities.DashboardRecebidoDemo;
import java.math.BigDecimal;
import java.util.List;

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
        return new DashboardResponse(
                new Usuario(nomeUsuario, nomeNegocio),
                new Recebido(recebido.getValor(), dashboard.getMeta(), recebido.getPedidos()),
                alertas.stream()
                        .map(alerta -> new Alerta(alerta.getTipo(), alerta.getQuantidade()))
                        .toList(),
                entregas.stream()
                        .map(entrega -> new Entrega(
                                entrega.getReferencia(),
                                entrega.getNome(),
                                entrega.getDia(),
                                entrega.getHora()
                        ))
                        .toList(),
                new Metricas(dashboard.getLucro(), dashboard.getCusto(), dashboard.getInvestido()),
                lucroMensal.stream()
                        .map(mes -> new LucroMensal(mes.getMes(), mes.getValor()))
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
