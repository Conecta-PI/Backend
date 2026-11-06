package back_conecta.controllers;

import back_conecta.dto.RelatorioResumoResponse;
import back_conecta.services.RelatorioService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/resumo")
    public RelatorioResumoResponse resumo() {
        return relatorioService.resumo();
    }

    @GetMapping("/vendas")
    public List<Map<String, Object>> vendas() {
        return List.of(
                Map.of("mes", "Janeiro", "valor", 12500.00, "quantidade", 20),
                Map.of("mes", "Fevereiro", "valor", 13800.00, "quantidade", 22),
                Map.of("mes", "Março", "valor", 14900.00, "quantidade", 25)
        );
    }

    @GetMapping("/financeiro")
    public Map<String, Object> financeiro() {
        RelatorioResumoResponse resumo = relatorioService.resumo();
        return Map.of(
                "faturamento", resumo.faturamento(),
                "despesas", resumo.despesas(),
                "lucro", resumo.lucro(),
                "pedidosPendentes", resumo.pedidosPendentes()
        );
    }
}
