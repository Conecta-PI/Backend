package back_conecta.controllers;

import back_conecta.dto.FichaTecnicaRequest;
import back_conecta.dto.ProdutoRequest;
import back_conecta.dto.ProdutoResponse;
import back_conecta.dto.ProdutoUpdateRequest;
import back_conecta.entities.Produto;
import back_conecta.services.ProdutoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoResponse> listar() {
        return produtoService.listar();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return produtoService.buscar(id);
    }

    @GetMapping("/categorias")
    public List<String> listarCategorias() {
        return produtoService.listarCategorias();
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse criado = produtoService.criar(request);
        return ResponseEntity.created(URI.create("/api/produtos/" + criado.id())).body(criado);
    }

    @PatchMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoUpdateRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/ficha-tecnica")
    public Map<String, Object> buscarFichaTecnica(@PathVariable Long id) {
        Produto produto = produtoService.buscarEntidade(id);
        Map<String, Object> ficha = new LinkedHashMap<>();
        ficha.put("id", produto.getIdProduto());
        ficha.put("nome", produto.getNomeProduto());
        ficha.put("foto", produto.getFoto());
        ficha.put("preparo", produto.getPreparo());
        ficha.put("rendimento", produto.getRendimento());
        ficha.put("ingredientes", produto.getIngredientes() == null ? List.of() : produto.getIngredientes());
        return ficha;
    }

    @PatchMapping("/{id}/ficha-tecnica")
    public ProdutoResponse atualizarFichaTecnica(@PathVariable Long id, @Valid @RequestBody FichaTecnicaRequest request) {
        Produto produto = produtoService.buscarEntidade(id);
        produto.setFoto(request.foto());
        produto.setPreparo(request.preparo());
        produto.setRendimento(request.rendimento());
        produto.setIngredientes(request.ingredientes() == null ? produto.getIngredientes() : request.ingredientes().toString());
        produtoService.salvar(produto);
        return ProdutoResponse.from(produto);
    }
}
