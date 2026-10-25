package back_conecta;

import back_conecta.repositories.ProdutoRepository;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.ItemVendaRepository;
import back_conecta.repositories.MovimentacaoFinanceiraRepository;
import back_conecta.repositories.PreferenciaRepository;
import back_conecta.repositories.VendaRepository;
import back_conecta.repositories.DashboardAlertaDemoRepository;
import back_conecta.repositories.DashboardDemoRepository;
import back_conecta.repositories.DashboardEntregaDemoRepository;
import back_conecta.repositories.DashboardLucroMensalDemoRepository;
import back_conecta.repositories.DashboardRecebidoDemoRepository;
import back_conecta.repositories.UsuarioRepository;
import back_conecta.config.DemoDataInitializer;
import back_conecta.config.DashboardDemoInitializer;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.DefaultApplicationArguments;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiJavaApplicationTests {
    @Autowired
    private MockMvc mockMvc;
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
    @Autowired
    private DemoDataInitializer demoDataInitializer;
    @Autowired
    private DashboardDemoInitializer dashboardDemoInitializer;
    @Autowired
    private DashboardDemoRepository dashboardRepository;
    @Autowired
    private DashboardRecebidoDemoRepository recebidoRepository;
    @Autowired
    private DashboardAlertaDemoRepository alertaRepository;
    @Autowired
    private DashboardEntregaDemoRepository entregaRepository;
    @Autowired
    private DashboardLucroMensalDemoRepository lucroMensalRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

	@Test
	void contextLoads() {
	}

    @Test
    void fluxoDeCadastroDeProdutoEDeVenda() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isUnauthorized());

        String respostaCadastro = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nome": "Responsável cadastrado",
                                  "email": "paulo@conecta.test",
                                  "telefone": "(11) 99999-0000",
                                  "senha": "senha-segura-123",
                                  "negocio": "Negócio cadastrado"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.nome").value("Responsável cadastrado"))
                .andExpect(jsonPath("$.usuario.negocio").value("Negócio cadastrado"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"PAULO@CONECTA.TEST","senha":"senha-segura-123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value("paulo@conecta.test"));
        Matcher tokenMatcher = Pattern.compile("\"accessToken\":\"([^\"]+)\"").matcher(respostaCadastro);
        Assertions.assertTrue(tokenMatcher.find());
        String token = tokenMatcher.group(1);
        String autorizacao = "Bearer " + token;

        Assertions.assertNotEquals(
                "senha-segura-123",
                usuarioRepository.findByEmailIgnoreCase("paulo@conecta.test").orElseThrow().getSenhaHash()
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"paulo@conecta.test","senha":"senha-errada"}
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nome": "Outra pessoa",
                                  "email": "outra@conecta.test",
                                  "telefone": "(11) 99999-1111",
                                  "senha": "senha-segura-456",
                                  "negocio": "Outro negócio"
                                }
                                """))
                .andExpect(status().isConflict());
        Assertions.assertEquals(1, usuarioRepository.count());

        mockMvc.perform(get("/api/produtos").header("Authorization", autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$.length()").value(6));
        mockMvc.perform(get("/api/clientes").header("Authorization", autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$.length()").value(4));

        demoDataInitializer.run(new DefaultApplicationArguments(new String[0]));
        dashboardDemoInitializer.run(new DefaultApplicationArguments(new String[0]));
        Assertions.assertEquals(6, produtoRepository.count());
        Assertions.assertEquals(4, clienteRepository.count());
        Assertions.assertEquals(1, preferenciaRepository.count());
        Assertions.assertEquals(1, vendaRepository.count());
        Assertions.assertEquals(2, itemVendaRepository.count());
        Assertions.assertEquals(1, movimentacaoRepository.count());
        Assertions.assertEquals(1, dashboardRepository.count());
        Assertions.assertEquals(3, recebidoRepository.count());
        Assertions.assertEquals(3, alertaRepository.count());
        Assertions.assertEquals(2, entregaRepository.count());
        Assertions.assertEquals(6, lucroMensalRepository.count());

        mockMvc.perform(get("/api/dashboard").param("periodo", "hoje").header("Authorization", autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.nome").value("Responsável cadastrado"))
                .andExpect(jsonPath("$.usuario.negocio").value("Negócio cadastrado"))
                .andExpect(jsonPath("$.recebido.valor").value(120.0))
                .andExpect(jsonPath("$.recebido.meta").value(2500.0))
                .andExpect(jsonPath("$.alertas.length()").value(3))
                .andExpect(jsonPath("$.proximasEntregas.length()").value(2))
                .andExpect(jsonPath("$.metricas.lucro").value(1847.5))
                .andExpect(jsonPath("$.lucroMensal.length()").value(6));

        mockMvc.perform(get("/api/dashboard").param("periodo", "semana").header("Authorization", autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recebido.valor").value(870.0))
                .andExpect(jsonPath("$.recebido.pedidos").value(42));

        mockMvc.perform(get("/api/dashboard").param("periodo", "mes").header("Authorization", autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recebido.valor").value(2150.0))
                .andExpect(jsonPath("$.recebido.pedidos").value(118));

        mockMvc.perform(get("/api/dashboard").param("periodo", "ano").header("Authorization", autorizacao))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", autorizacao)
                        .contentType("application/json")
                        .content("""
                                {
                                  "nome": "Produto de teste",
                                  "descricao": "Teste de API",
                                  "precoCusto": 2.00,
                                  "preco": 5.00,
                                  "categoria": "Teste",
                                  "estoque": 4
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Produto de teste"));

        var produto = produtoRepository.findFirstByNomeProdutoIgnoreCase("Brigadeiro Gourmet").orElseThrow();
        var cliente = clienteRepository.findFirstByEmailIgnoreCase("ana.costa@example.test").orElseThrow();
        int estoqueInicial = produto.getEstoque();
        mockMvc.perform(post("/api/vendas")
                        .header("Authorization", autorizacao)
                        .contentType("application/json")
                        .content("""
                                {
                                  "clienteId": %d,
                                  "formaPagamento": "PIX",
                                  "itens": [{"produtoId": %d, "quantidade": 1}]
                                }
                                """.formatted(cliente.getIdCliente(), produto.getIdProduto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(new BigDecimal("5.00").doubleValue()));

        Assertions.assertEquals(
                estoqueInicial - 1,
                produtoRepository.findById(produto.getIdProduto()).orElseThrow().getEstoque()
        );
        mockMvc.perform(get("/api/vendas").header("Authorization", autorizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CONCLUIDA"));
    }
}
