package back_conecta.services;

import back_conecta.dto.ProdutoRequest;
import back_conecta.dto.ProdutoResponse;
import back_conecta.entities.Produto;
import back_conecta.repositories.ProdutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNomeProduto(request.nome().trim());
        produto.setDescricao(request.descricao());
        produto.setPrecoCusto(request.precoCusto());
        produto.setPrecoVenda(request.preco());
        produto.setCategoria(request.categoria().trim());
        produto.setEstoque(request.estoque());
        produto.setAtivo(true);
        return ProdutoResponse.from(produtoRepository.save(produto));
    }
}
