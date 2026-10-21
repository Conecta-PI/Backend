package back_conecta.config;

import back_conecta.entities.*;
import back_conecta.enums.*;
import back_conecta.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class TestDatabaseRunner implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PreferenciaRepository preferenciaRepository;
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ItemVendaRepository itemVendaRepository;
    @Autowired
    private MovimentacaoFinanceiraRepository movimentacaoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("⏳ Iniciando o teste de inserção no banco de dados...");

        // 1. Criar e Salvar Usuário (Proprietário)
        Usuario usuario = new Usuario(null, "Paulo Henrique Guidio", "paulo@email.com", "senha123", LocalDateTime.now());
        usuarioRepository.save(usuario);

        // 2. Criar e Salvar Produtos
        Produto brigadeiro = new Produto(null, "Brigadeiro Gourmet", "Chocolate belga", new BigDecimal("1.50"), new BigDecimal("3.00"), true);
        Produto bolo = new Produto(null, "Bolo no Pote", "Bolo de cenoura com chocolate", new BigDecimal("4.00"), new BigDecimal("10.00"), true);
        produtoRepository.saveAll(Arrays.asList(brigadeiro, bolo));

        // 3. Criar e Salvar Cliente
        Cliente cliente = new Cliente(null, "Ana Paula", "11999999999", "ana@email.com", "Sem restrições", null, null);
        clienteRepository.save(cliente);

        // 4. Criar e Salvar Preferência (Relacionamento com Cliente)
        Preferencia pref = new Preferencia(null, cliente, LocalDateTime.now(), TipoRegistro.PREFERENCIA, "WhatsApp", "Prefere doces de morango");
        preferenciaRepository.save(pref);

        // 5. Criar e Salvar Venda (Relacionamento com Cliente e Enums)
        Venda venda = new Venda(null, cliente, LocalDateTime.now(), new BigDecimal("16.00"), StatusVenda.CONCLUIDA, FormaPagamento.PIX, null);
        vendaRepository.save(venda);

        // 6. Criar e Salvar Itens da Venda (Relacionamento N:N - Venda e Produto)
        ItemVenda item1 = new ItemVenda(null, venda, brigadeiro, 2, new BigDecimal("3.00")); // 2 brigadeiros (R$ 6,00)
        ItemVenda item2 = new ItemVenda(null, venda, bolo, 1, new BigDecimal("10.00")); // 1 bolo (R$ 10,00)
        itemVendaRepository.saveAll(Arrays.asList(item1, item2));

        // 7. Criar e Salvar Movimentação Financeira (Entrada da Venda)
        MovimentacaoFinanceira entrada = new MovimentacaoFinanceira(null, true, new BigDecimal("16.00"), "Venda de doces", LocalDateTime.now(), venda);
        movimentacaoRepository.save(entrada);

        System.out.println("✅ Banco de dados populado com sucesso! Tabelas e relacionamentos OK.");
    }
}