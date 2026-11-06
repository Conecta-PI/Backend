package back_conecta.services;

import back_conecta.dto.FinanceiroLancamentoRequest;
import back_conecta.dto.FinanceiroLancamentoResponse;
import back_conecta.dto.FinanceiroResumoResponse;
import back_conecta.entities.MovimentacaoFinanceira;
import back_conecta.repositories.MovimentacaoFinanceiraRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceiroService {
    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;

    public FinanceiroService(MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository) {
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
    }

    @Transactional(readOnly = true)
    public FinanceiroResumoResponse buscarResumo() {
        List<MovimentacaoFinanceira> lancamentos = movimentacaoFinanceiraRepository.findAllByOrderByDataMovimentoDesc();
        BigDecimal entradas = lancamentos.stream()
                .filter(lancamento -> Boolean.TRUE.equals(lancamento.getTipoMovimento()))
                .map(lancamento -> lancamento.getValor() == null ? BigDecimal.ZERO : lancamento.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saidas = lancamentos.stream()
                .filter(lancamento -> Boolean.FALSE.equals(lancamento.getTipoMovimento()))
                .map(lancamento -> lancamento.getValor() == null ? BigDecimal.ZERO : lancamento.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinanceiroResumoResponse(
                entradas.setScale(2, RoundingMode.HALF_UP),
                saidas.setScale(2, RoundingMode.HALF_UP),
                entradas.subtract(saidas).setScale(2, RoundingMode.HALF_UP),
                lancamentos.size()
        );
    }

    @Transactional(readOnly = true)
    public List<FinanceiroLancamentoResponse> listarLancamentos() {
        return movimentacaoFinanceiraRepository.findAllByOrderByDataMovimentoDesc().stream()
                .filter(Objects::nonNull)
                .map(FinanceiroLancamentoResponse::from)
                .toList();
    }

    @Transactional
    public FinanceiroLancamentoResponse criarLancamento(FinanceiroLancamentoRequest request) {
        String tipo = request.tipo() == null ? "saida" : request.tipo().trim().toLowerCase();
        boolean tipoMovimento = "entrada".equals(tipo);

        MovimentacaoFinanceira lancamento = new MovimentacaoFinanceira();
        lancamento.setTipoMovimento(tipoMovimento);
        lancamento.setValor(request.valor().setScale(2, RoundingMode.HALF_UP));
        lancamento.setCategoriaSaida(request.categoria() == null || request.categoria().isBlank()
                ? "Geral"
                : request.categoria().trim());
        lancamento.setDataMovimento(LocalDateTime.now());
        lancamento.setVenda(null);

        return FinanceiroLancamentoResponse.from(movimentacaoFinanceiraRepository.save(lancamento));
    }
}
