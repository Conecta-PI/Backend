package back_conecta.controllers;

import back_conecta.dto.ListaCompraRequest;
import back_conecta.dto.ListaCompraResponse;
import back_conecta.services.ListaCompraService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
@RequestMapping("/api/lista-compras")
public class ListaCompraController {
    private final ListaCompraService listaCompraService;

    public ListaCompraController(ListaCompraService listaCompraService) {
        this.listaCompraService = listaCompraService;
    }

    @GetMapping
    public List<ListaCompraResponse> listar() {
        return listaCompraService.listar();
    }

    @GetMapping("/{id}")
    public ListaCompraResponse buscar(@PathVariable Long id) {
        return listaCompraService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ListaCompraResponse> criar(@Valid @RequestBody ListaCompraRequest request) {
        ListaCompraResponse listaCompra = listaCompraService.criar(request);
        return ResponseEntity.created(URI.create("/api/lista-compras/" + listaCompra.id())).body(listaCompra);
    }

    @PatchMapping("/{id}")
    public ListaCompraResponse atualizar(@PathVariable Long id, @Valid @RequestBody ListaCompraRequest request) {
        return listaCompraService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        listaCompraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
