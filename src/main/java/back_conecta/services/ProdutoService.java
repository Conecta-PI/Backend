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
                .filter(item -> Boolean.TRUE.equals(item.getAtivo()))
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

        produto.setNomeProduto(request.nome().trim());
        produto.setDescricao(request.descricao());
        produto.setPrecoCusto(request.precoCusto() == null ? produto.getPrecoCusto() : request.precoCusto());
        produto.setPrecoVenda(request.preco());
        produto.setCategoria(request.categoria().trim());
        produto.setEstoque(request.estoque());
        produto.setFoto(request.foto());
        produto.setPreparo(request.preparo());
        produto.setRendimento(request.rendimento());
        produto.setIngredientes(serializarIngredientes(request.ingredientes()));
        produto.setAtivo(request.ativo() == null ? produto.getAtivo() : request.ativo());

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    private String serializarIngredientes(List<?> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return null;
        }

        return ingredientes.toString();
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
