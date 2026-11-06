package back_conecta.controllers;

import back_conecta.dto.ClienteDetalheResponse;
import back_conecta.dto.ClienteRequest;
import back_conecta.dto.ClienteResponse;
import back_conecta.dto.ClienteResumoResponse;
import back_conecta.services.ClienteService;
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
import back_conecta.dto.ClienteObservacaoRequest;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteDetalheResponse buscar(@PathVariable Long id) {
        return clienteService.buscar(id);
    }

    @GetMapping("/{id}/resumo")
    public ClienteResumoResponse resumo(@PathVariable Long id) {
        return clienteService.resumo(id);
    }

    @PostMapping
    public ResponseEntity<ClienteDetalheResponse> criar(@Valid @RequestBody ClienteRequest request) {
        ClienteDetalheResponse cliente = clienteService.criar(request);
        return ResponseEntity.created(URI.create("/api/clientes/" + cliente.id())).body(cliente);
    }

    @PatchMapping("/{id}")
    public ClienteDetalheResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/observacao")
    public ClienteDetalheResponse atualizarObservacao(@PathVariable Long id, @Valid @RequestBody ClienteObservacaoRequest request) {
        return clienteService.atualizarObservacao(id, request.observacao());
    }
}
