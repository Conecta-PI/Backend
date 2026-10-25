package back_conecta.config;

import back_conecta.entities.Cliente;
import back_conecta.entities.ItemVenda;
import back_conecta.entities.MovimentacaoFinanceira;
import back_conecta.entities.Preferencia;
import back_conecta.entities.Produto;
import back_conecta.entities.Venda;
import back_conecta.enums.FormaPagamento;
import back_conecta.enums.StatusVenda;
import back_conecta.enums.TipoRegistro;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.ItemVendaRepository;
import back_conecta.repositories.MovimentacaoFinanceiraRepository;
import back_conecta.repositories.PreferenciaRepository;
import back_conecta.repositories.ProdutoRepository;
import back_conecta.repositories.VendaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"h2", "mysql"})
@Order(1)
public class DemoDataInitializer implements ApplicationRunner {
    private static final String PREFERENCIA_DEMO =
            "DADO DE TESTE: cliente prefere doces de morango.";

    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final PreferenciaRepository preferenciaRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoRepository;

    public DemoDataInitializer(
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            PreferenciaRepository preferenciaRepository,
            VendaRepository vendaRepository,
            ItemVendaRepository itemVendaRepository,
            MovimentacaoFinanceiraRepository movimentacaoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.preferenciaRepository = preferenciaRepository;
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedProdutos();
        seedClientes();
        seedHistoricoDeTeste();
    }

    private void seedProdutos() {
        List<Produto> produtos = List.of(
                produto("Bolo de Pote Choc", "Camadas de bolo de chocolate, brigadeiro cremoso e cobertura de morango.", "12.00", "Bolo de pote", 8),
                produto("Bolo de Pote Morango", "Bolo branco, creme de baunilha e morangos frescos.", "12.00", "Bolo de pote", 5),
                produto("Bolo de Pote Churros", "Bolo de canela com doce de leite e açúcar com canela.", "14.00", "Bolo de pote", 2),
                produto("Lanche Nat. Frango", "Pão integral, frango desfiado, cenoura e alface.", "15.00", "Lanche", 10),
                produto("Lanche Nat. Atum", "Pão italiano, atum, tomate e alface.", "15.00", "Lanche", 0),
                produto("Brigadeiro Gourmet", "Brigadeiro com chocolate belga e granulado.", "5.00", "Doce", 15)
        );

        for (Produto produto : produtos) {
            if (produtoRepository.findFirstByNomeProdutoIgnoreCase(produto.getNomeProduto()).isEmpty()) {
                produtoRepository.save(produto);
            }
        }
    }

    private void seedClientes() {
        List<Cliente> clientes = List.of(
                cliente("Maria Silva", "00000000001", "maria.silva@example.test"),
                cliente("Pedro Carvalho", "00000000002", "pedro.carvalho@example.test"),
                cliente("Jonas Oliveira", "00000000003", "jonas.oliveira@example.test"),
                cliente("Ana Costa", "00000000004", "ana.costa@example.test")
        );

        for (Cliente cliente : clientes) {
            if (clienteRepository.findFirstByEmailIgnoreCase(cliente.getEmail()).isEmpty()) {
                clienteRepository.save(cliente);
            }
        }
    }

    private void seedHistoricoDeTeste() {
        if (preferenciaRepository.existsByDescricao(PREFERENCIA_DEMO)) {
            return;
        }

        Cliente cliente = clienteRepository.findFirstByEmailIgnoreCase("ana.costa@example.test")
                .orElseThrow();
        Produto brigadeiro = produtoRepository.findFirstByNomeProdutoIgnoreCase("Brigadeiro Gourmet")
                .orElseThrow();
        Produto bolo = produtoRepository.findFirstByNomeProdutoIgnoreCase("Bolo de Pote Choc")
                .orElseThrow();

        Preferencia preferencia = new Preferencia(
                null,
                cliente,
                LocalDateTime.now(),
                TipoRegistro.PREFERENCIA,
                "WhatsApp",
                PREFERENCIA_DEMO
        );
        preferenciaRepository.save(preferencia);

        BigDecimal total = new BigDecimal("22.00");
        Venda venda = new Venda(
                null,
                cliente,
                LocalDateTime.now(),
                total,
                StatusVenda.CONCLUIDA,
                FormaPagamento.PIX,
                null
        );
        vendaRepository.save(venda);

        itemVendaRepository.saveAll(List.of(
                new ItemVenda(null, venda, brigadeiro, 2, brigadeiro.getPrecoVenda()),
                new ItemVenda(null, venda, bolo, 1, bolo.getPrecoVenda())
        ));

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira(
                null,
                true,
                total,
                "Venda de teste",
                LocalDateTime.now(),
                venda
        );
        movimentacaoRepository.save(movimentacao);
    }

    private Produto produto(String nome, String descricao, String preco, String categoria, int estoque) {
        Produto produto = new Produto();
        produto.setNomeProduto(nome);
        produto.setDescricao(descricao);
        produto.setPrecoVenda(new BigDecimal(preco));
        produto.setPrecoCusto(null);
        produto.setCategoria(categoria);
        produto.setEstoque(estoque);
        produto.setAtivo(true);
        return produto;
    }

    private Cliente cliente(String nome, String telefone, String email) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setObservacoes("Contato fictício usado somente para demonstração.");
        return cliente;
    }
}
