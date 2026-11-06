package back_conecta.controllers;

import back_conecta.dto.FinanceiroLancamentoRequest;
import back_conecta.dto.FinanceiroLancamentoResponse;
import back_conecta.dto.FinanceiroResumoResponse;
import back_conecta.services.FinanceiroService;
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
@RequestMapping("/api/financeiro")
public class FinanceiroController {
    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping("/resumo")
    public FinanceiroResumoResponse resumo() {
        return financeiroService.buscarResumo();
    }

    @GetMapping("/lancamentos")
    public List<FinanceiroLancamentoResponse> listarLancamentos() {
        return financeiroService.listarLancamentos();
    }

    @PostMapping("/lancamentos")
    public ResponseEntity<FinanceiroLancamentoResponse> criarLancamento(
            @Valid @RequestBody FinanceiroLancamentoRequest request
    ) {
        FinanceiroLancamentoResponse criado = financeiroService.criarLancamento(request);
        return ResponseEntity.created(URI.create("/api/financeiro/lancamentos/" + criado.id())).body(criado);
    }
}
