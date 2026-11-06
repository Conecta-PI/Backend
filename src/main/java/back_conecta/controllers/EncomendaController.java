package back_conecta.controllers;

import back_conecta.dto.EncomendaRequest;
import back_conecta.dto.EncomendaResponse;
import back_conecta.services.EncomendaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/encomendas")
public class EncomendaController {
    private final EncomendaService encomendaService;

    public EncomendaController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @GetMapping
    public List<EncomendaResponse> listar() {
        return encomendaService.listar();
    }

    @GetMapping("/{id}")
    public EncomendaResponse buscar(@PathVariable Long id) {
        return encomendaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<EncomendaResponse> criar(@Valid @RequestBody EncomendaRequest request) {
        EncomendaResponse encomenda = encomendaService.criar(request);
        return ResponseEntity.created(URI.create("/api/encomendas/" + encomenda.id())).body(encomenda);
    }

    @PatchMapping("/{id}/status")
    public EncomendaResponse atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload == null ? null : payload.get("status");
        return encomendaService.atualizarStatus(id, status);
    }
}
