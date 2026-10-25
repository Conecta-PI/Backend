package back_conecta.controllers;

import back_conecta.dto.VendaRequest;
import back_conecta.dto.VendaResponse;
import back_conecta.services.VendaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {
    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public List<VendaResponse> listar() {
        return vendaService.listar();
    }

    @PostMapping
    public ResponseEntity<VendaResponse> criar(@Valid @RequestBody VendaRequest request) {
        VendaResponse criada = vendaService.criar(request);
        return ResponseEntity.created(URI.create("/api/vendas/" + criada.id())).body(criada);
    }
}
