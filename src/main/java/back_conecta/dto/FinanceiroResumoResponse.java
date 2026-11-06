package back_conecta.dto;

import java.math.BigDecimal;

public record FinanceiroResumoResponse(
        BigDecimal entradas,
        BigDecimal saidas,
        BigDecimal saldo,
        long totalLancamentos
) {}
