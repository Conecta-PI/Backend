package back_conecta.services;

import back_conecta.dto.ProdutoRequest;
import back_conecta.dto.ProdutoResponse;
import back_conecta.dto.ProdutoUpdateRequest;
import back_conecta.entities.Produto;
import back_conecta.repositories.ProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAllByAtivoTrueOrderByNomeProdutoAsc()
                .stream()
                .map(ProdutoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        return ProdutoResponse.from(produto);
    }

    @Transactional(readOnly = true)
    public Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        return produtoRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .filter(produto -> Boolean.TRUE.equals(produto.getAtivo()))
                .map(produto -> produto.getCategoria() == null ? "" : produto.getCategoria())
                .filter(categoria -> categoria != null && !categoria.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNomeProduto(request.nome().trim());
        produto.setDescricao(request.descricao());
        produto.setPrecoCusto(request.precoCusto() == null ? BigDecimal.ZERO : request.precoCusto());
        produto.setPrecoVenda(request.preco());
        produto.setCategoria(request.categoria().trim());
        produto.setEstoque(request.estoque());
        produto.setFoto(request.foto());
        produto.setPreparo(request.preparo());
        produto.setRendimento(request.rendimento());
        produto.setIngredientes(serializarIngredientes(request.ingredientes()));
        produto.setAtivo(request.ativo() == null ? true : request.ativo());
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoUpdateRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        if (request.nome() != null) {
            if (request.nome().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do produto é obrigatório");
            }
            produto.setNomeProduto(request.nome().trim());
        }
        if (request.descricao() != null) produto.setDescricao(request.descricao());
        if (request.precoCusto() != null) produto.setPrecoCusto(request.precoCusto());
        if (request.preco() != null) produto.setPrecoVenda(request.preco());
        if (request.categoria() != null) {
            if (request.categoria().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria é obrigatória");
            }
            produto.setCategoria(request.categoria().trim());
        }
        if (request.estoque() != null) produto.setEstoque(request.estoque());
        if (request.foto() != null && !request.foto().isBlank()) produto.setFoto(request.foto());
        if (request.preparo() != null) produto.setPreparo(request.preparo());
        if (request.rendimento() != null) produto.setRendimento(request.rendimento());
        if (request.ingredientes() != null) produto.setIngredientes(serializarIngredientes(request.ingredientes()));
        if (request.ativo() != null) produto.setAtivo(request.ativo());

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    private String serializarIngredientes(List<?> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return null;
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < ingredientes.size(); i++) {
            Object item = ingredientes.get(i);
            if (i > 0) json.append(",");
            if (item instanceof back_conecta.dto.ProdutoIngredienteRequest ingrediente) {
                json.append("{\"nome\":\"").append(escapeJson(ingrediente.nome()))
                        .append("\",\"quantidade\":").append(ingrediente.quantidade() == null ? "null" : ingrediente.quantidade())
                        .append(",\"unidade\":\"").append(escapeJson(ingrediente.unidade())).append("\"}");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    @Transactional
    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}
