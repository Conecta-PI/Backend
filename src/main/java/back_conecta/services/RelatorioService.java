package back_conecta.services;

import back_conecta.dto.RelatorioResumoResponse;
import back_conecta.entities.Venda;
import back_conecta.enums.StatusVenda;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.MovimentacaoFinanceiraRepository;
import back_conecta.repositories.ProdutoRepository;
import back_conecta.repositories.VendaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;

    public RelatorioService(
            VendaRepository vendaRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository
    ) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
    }

    @Transactional(readOnly = true)
    public RelatorioResumoResponse resumo() {
        List<Venda> vendas = vendaRepository.findAllByOrderByDataVendaDesc();
        BigDecimal faturamento = vendas.stream()
                .filter(Objects::nonNull)
                .map(venda -> venda.getValorTotal() == null ? BigDecimal.ZERO : venda.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long pedidosPendentes = vendas.stream()
                .filter(venda -> venda.getStatusVenda() == StatusVenda.PENDENTE)
                .count();

        Long clientesAtivos = (long) clienteRepository.findAll().size();

        Long produtosAtivos = produtoRepository.findAll().stream()
                .filter(produto -> Boolean.TRUE.equals(produto.getAtivo()))
                .count();

        BigDecimal despesas = movimentacaoFinanceiraRepository.findAll().stream()
                .filter(movimentacao -> Boolean.FALSE.equals(movimentacao.getTipoMovimento()))
                .map(movimentacao -> movimentacao.getValor() == null ? BigDecimal.ZERO : movimentacao.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receitas = movimentacaoFinanceiraRepository.findAll().stream()
                .filter(movimentacao -> Boolean.TRUE.equals(movimentacao.getTipoMovimento()))
                .map(movimentacao -> movimentacao.getValor() == null ? BigDecimal.ZERO : movimentacao.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioResumoResponse(faturamento, despesas, receitas.subtract(despesas), pedidosPendentes, clientesAtivos, produtosAtivos);
    }
}
