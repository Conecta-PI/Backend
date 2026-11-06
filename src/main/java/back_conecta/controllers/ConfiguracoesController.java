package back_conecta.controllers;

import back_conecta.services.ConfiguracoesService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracoes")
public class ConfiguracoesController {
    private final ConfiguracoesService configuracoesService;

    public ConfiguracoesController(ConfiguracoesService configuracoesService) {
        this.configuracoesService = configuracoesService;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return configuracoesService.listar();
    }

    @PatchMapping
    public Map<String, Object> atualizar(@RequestBody Map<String, Object> payload) {
        return configuracoesService.atualizar(payload);
    }

    @GetMapping("/notificacoes")
    public List<Map<String, Object>> listarNotificacoes() {
        return configuracoesService.listarNotificacoes();
    }
}
