package back_conecta.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracoesService {
    private final Map<String, Object> configuracoes = new ConcurrentHashMap<>();

    public ConfiguracoesService() {
        configuracoes.put("empresa.nome", "ConectaPI");
        configuracoes.put("empresa.cidade", "São Paulo");
        configuracoes.put("notificacoes.estoque", true);
        configuracoes.put("notificacoes.financeiro", true);
        configuracoes.put("vendas.autoFechamento", true);
        configuracoes.put("dashboard.periodo", "30d");
    }

    public List<Map<String, Object>> listar() {
        return configuracoes.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("chave", entry.getKey());
                    item.put("valor", entry.getValue());
                    item.put("tipo", entry.getValue() instanceof Boolean ? "boolean" : "string");
                    return item;
                })
                .toList();
    }

    public Map<String, Object> atualizar(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return configuracoes;
        }

        payload.forEach(configuracoes::put);
        return configuracoes;
    }

    public List<Map<String, Object>> listarNotificacoes() {
        return List.of(
                Map.of("tipo", "estoque", "mensagem", "Produtos com estoque baixo", "ativa", true),
                Map.of("tipo", "financeiro", "mensagem", "Pagamentos pendentes", "ativa", true),
                Map.of("tipo", "vendas", "mensagem", "Novas vendas hoje", "ativa", false)
        );
    }
}
