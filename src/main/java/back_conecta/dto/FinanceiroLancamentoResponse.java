package back_conecta.dto;

import back_conecta.entities.MovimentacaoFinanceira;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroLancamentoResponse(
        Long id,
        String tipo,
        BigDecimal valor,
        String categoria,
        String descricao,
        LocalDateTime data
) {
    public static FinanceiroLancamentoResponse from(MovimentacaoFinanceira lancamento) {
        return new FinanceiroLancamentoResponse(
                lancamento.getIdMovimentacao(),
                Boolean.TRUE.equals(lancamento.getTipoMovimento()) ? "entrada" : "saida",
                lancamento.getValor(),
                lancamento.getCategoriaSaida() == null || lancamento.getCategoriaSaida().isBlank()
                        ? "Geral"
                        : lancamento.getCategoriaSaida(),
                lancamento.getCategoriaSaida() == null || lancamento.getCategoriaSaida().isBlank()
                        ? "Movimentação de caixa"
                        : lancamento.getCategoriaSaida(),
                lancamento.getDataMovimento()
        );
    }
}
