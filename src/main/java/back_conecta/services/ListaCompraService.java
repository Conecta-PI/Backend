package back_conecta.services;

import back_conecta.dto.ListaCompraItemRequest;
import back_conecta.dto.ListaCompraRequest;
import back_conecta.dto.ListaCompraResponse;
import back_conecta.entities.ListaCompra;
import back_conecta.entities.ListaCompraItem;
import back_conecta.repositories.ListaCompraRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ListaCompraService {
    private final ListaCompraRepository listaCompraRepository;

    public ListaCompraService(ListaCompraRepository listaCompraRepository) {
        this.listaCompraRepository = listaCompraRepository;
    }

    @Transactional(readOnly = true)
    public List<ListaCompraResponse> listar() {
        return listaCompraRepository.findAllByOrderByDataCriacaoDesc().stream()
                .map(ListaCompraResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListaCompraResponse buscar(Long id) {
        ListaCompra listaCompra = listaCompraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de compras não encontrada"));
        return ListaCompraResponse.from(listaCompra);
    }

    @Transactional
    public ListaCompraResponse criar(ListaCompraRequest request) {
        ListaCompra listaCompra = new ListaCompra();
        listaCompra.setTitulo(request.titulo() == null || request.titulo().isBlank() ? "Lista de compras" : request.titulo());
        listaCompra.setStatus(request.status() == null || request.status().isBlank() ? "aberta" : request.status().trim().toLowerCase());
        listaCompra.setPrioridade(request.prioridade() == null || request.prioridade().isBlank() ? "media" : request.prioridade().trim().toLowerCase());
        listaCompra.setTotalEstimado(request.totalEstimado() == null ? BigDecimal.ZERO : request.totalEstimado());

        List<ListaCompraItem> itens = new ArrayList<>();
        if (request.itens() != null) {
            for (ListaCompraItemRequest itemRequest : request.itens()) {
                if (itemRequest == null) {
                    continue;
                }
                itens.add(new ListaCompraItem(
                        itemRequest.nomeProduto(),
                        itemRequest.quantidade() == null ? 0 : itemRequest.quantidade(),
                        itemRequest.precoUnitario() == null ? BigDecimal.ZERO : itemRequest.precoUnitario(),
                        itemRequest.concluido() == null ? false : itemRequest.concluido()
                ));
            }
        }
        listaCompra.setItens(itens);

        if ("concluida".equalsIgnoreCase(listaCompra.getStatus())) {
            listaCompra.setDataConclusao(LocalDateTime.now());
        }

        return ListaCompraResponse.from(listaCompraRepository.save(listaCompra));
    }

    @Transactional
    public ListaCompraResponse atualizar(Long id, ListaCompraRequest request) {
        ListaCompra listaCompra = listaCompraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de compras não encontrada"));

        if (request.titulo() != null) {
            listaCompra.setTitulo(request.titulo());
        }
        if (request.status() != null) {
            listaCompra.setStatus(request.status().trim().toLowerCase());
        }
        if (request.prioridade() != null) {
            listaCompra.setPrioridade(request.prioridade().trim().toLowerCase());
        }
        if (request.totalEstimado() != null) {
            listaCompra.setTotalEstimado(request.totalEstimado());
        }
        if (request.itens() != null) {
            List<ListaCompraItem> itens = new ArrayList<>();
            for (ListaCompraItemRequest itemRequest : request.itens()) {
                if (itemRequest == null) {
                    continue;
                }
                itens.add(new ListaCompraItem(
                        itemRequest.nomeProduto(),
                        itemRequest.quantidade() == null ? 0 : itemRequest.quantidade(),
                        itemRequest.precoUnitario() == null ? BigDecimal.ZERO : itemRequest.precoUnitario(),
                        itemRequest.concluido() == null ? false : itemRequest.concluido()
                ));
            }
            listaCompra.setItens(itens);
        }

        if ("concluida".equalsIgnoreCase(listaCompra.getStatus())) {
            if (listaCompra.getDataConclusao() == null) {
                listaCompra.setDataConclusao(LocalDateTime.now());
            }
        } else {
            listaCompra.setDataConclusao(null);
        }

        return ListaCompraResponse.from(listaCompraRepository.save(listaCompra));
    }

    @Transactional
    public void excluir(Long id) {
        if (!listaCompraRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de compras não encontrada");
        }
        listaCompraRepository.deleteById(id);
    }
}
