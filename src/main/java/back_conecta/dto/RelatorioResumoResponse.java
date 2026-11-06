package back_conecta.dto;

import java.math.BigDecimal;

public record RelatorioResumoResponse(
        BigDecimal faturamento,
        BigDecimal despesas,
        BigDecimal lucro,
        Long pedidosPendentes,
        Long clientesAtivos,
        Long produtosAtivos
) {
}
