package back_conecta.services;

import back_conecta.dto.ItemVendaRequest;
import back_conecta.dto.VendaRequest;
import back_conecta.dto.VendaResponse;
import back_conecta.entities.Cliente;
import back_conecta.entities.ItemVenda;
import back_conecta.entities.MovimentacaoFinanceira;
import back_conecta.entities.Produto;
import back_conecta.entities.Venda;
import back_conecta.enums.StatusVenda;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.MovimentacaoFinanceiraRepository;
import back_conecta.repositories.ProdutoRepository;
import back_conecta.repositories.VendaRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VendaService {
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoRepository;

    public VendaService(
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            VendaRepository vendaRepository,
            MovimentacaoFinanceiraRepository movimentacaoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listar() {
        return vendaRepository.findAllByOrderByDataVendaDesc()
                .stream()
                .map(VendaResponse::from)
                .toList();
    }

    @Transactional
    public VendaResponse criar(VendaRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Map<Long, Integer> quantidadesPorProduto = new LinkedHashMap<>();
        for (ItemVendaRequest item : request.itens()) {
            quantidadesPorProduto.merge(item.produtoId(), item.quantidade(), Integer::sum);
        }

        List<ItemVenda> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entrada : quantidadesPorProduto.entrySet()) {
            Produto produto = produtoRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
            if (!Boolean.TRUE.equals(produto.getAtivo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inativo: " + produto.getNomeProduto());
            }
            int estoqueDisponivel = produto.getEstoque() == null ? 0 : produto.getEstoque();
            if (estoqueDisponivel < entrada.getValue()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para: " + produto.getNomeProduto());
            }

            BigDecimal subtotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(entrada.getValue()));
            total = total.add(subtotal);
            produto.setEstoque(estoqueDisponivel - entrada.getValue());

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(entrada.getValue());
            item.setPrecoUnitario(produto.getPrecoVenda());
            itens.add(item);
        }

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setValorTotal(total);
        venda.setStatusVenda(StatusVenda.CONCLUIDA);
        venda.setFormaPagamento(request.formaPagamento());
        for (ItemVenda item : itens) {
            item.setVenda(venda);
        }
        venda.setItens(itens);
        venda = vendaRepository.save(venda);

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setTipoMovimento(true);
        movimentacao.setValor(total);
        movimentacao.setCategoriaSaida("Venda");
        movimentacao.setVenda(venda);
        movimentacaoRepository.save(movimentacao);

        return VendaResponse.from(venda);
    }
}
