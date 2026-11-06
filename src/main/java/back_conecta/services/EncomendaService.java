package back_conecta.services;

import back_conecta.dto.EncomendaItemRequest;
import back_conecta.dto.EncomendaRequest;
import back_conecta.dto.EncomendaResponse;
import back_conecta.entities.Encomenda;
import back_conecta.entities.EncomendaItem;
import back_conecta.repositories.EncomendaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EncomendaService {
    private final EncomendaRepository encomendaRepository;

    public EncomendaService(EncomendaRepository encomendaRepository) {
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional(readOnly = true)
    public List<EncomendaResponse> listar() {
        return encomendaRepository.findAllByOrderByDataPedidoDesc().stream()
                .map(EncomendaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EncomendaResponse buscar(Long id) {
        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encomenda não encontrada"));
        return EncomendaResponse.from(encomenda);
    }

    @Transactional
    public EncomendaResponse criar(EncomendaRequest request) {
        Encomenda encomenda = new Encomenda();
        encomenda.setCodigo(request.codigo() == null || request.codigo().isBlank() ? "ENC-" + System.currentTimeMillis() : request.codigo());
        encomenda.setClienteNome(request.clienteNome() == null || request.clienteNome().isBlank() ? "Cliente" : request.clienteNome());
        encomenda.setStatus(request.status() == null || request.status().isBlank() ? "pendente" : request.status().trim().toLowerCase());
        encomenda.setDataEntrega(request.dataEntrega());
        encomenda.setObservacao(request.observacao());
        encomenda.setValorTotal(request.valorTotal() == null ? BigDecimal.ZERO : request.valorTotal());

        List<EncomendaItem> itens = new ArrayList<>();
        if (request.itens() != null) {
            for (EncomendaItemRequest itemRequest : request.itens()) {
                if (itemRequest == null) {
                    continue;
                }
                EncomendaItem item = new EncomendaItem(
                        itemRequest.nomeProduto(),
                        itemRequest.quantidade() == null ? 0 : itemRequest.quantidade(),
                        itemRequest.precoUnitario() == null ? BigDecimal.ZERO : itemRequest.precoUnitario()
                );
                itens.add(item);
            }
        }
        encomenda.setItens(itens);
        if (encomenda.getDataPedido() == null) {
            encomenda.setDataPedido(LocalDateTime.now());
        }

        return EncomendaResponse.from(encomendaRepository.save(encomenda));
    }

    @Transactional
    public EncomendaResponse atualizarStatus(Long id, String status) {
        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encomenda não encontrada"));

        if (status != null && !status.isBlank()) {
            encomenda.setStatus(status.trim().toLowerCase());
        }

        return EncomendaResponse.from(encomendaRepository.save(encomenda));
    }
}
