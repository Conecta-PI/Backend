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
        validarLista(request, true);
        ListaCompra listaCompra = new ListaCompra();
        listaCompra.setTitulo(request.titulo().trim());
        listaCompra.setStatus(request.status() == null || request.status().isBlank() ? "aberta" : request.status().trim().toLowerCase());
        listaCompra.setPrioridade(request.prioridade() == null || request.prioridade().isBlank() ? "media" : request.prioridade().trim().toLowerCase());
        listaCompra.setTotalEstimado(calcularTotal(request.itens()));

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
        validarLista(request, false);

        if (request.titulo() != null) {
            listaCompra.setTitulo(request.titulo().trim());
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
            listaCompra.setTotalEstimado(calcularTotal(request.itens()));
        } else {
            listaCompra.setTotalEstimado(calcularTotalItens(listaCompra.getItens()));
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

    private void validarLista(ListaCompraRequest request, boolean criacao) {
        if (criacao && (request.titulo() == null || request.titulo().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título da lista é obrigatório");
        }
        if (!criacao && request.titulo() != null && request.titulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título da lista é obrigatório");
        }
        if (criacao && (request.itens() == null || request.itens().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista deve possuir pelo menos um item");
        }
        if (request.itens() != null) {
            if (request.itens().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista deve possuir pelo menos um item");
            }
            for (ListaCompraItemRequest item : request.itens()) {
                validarItem(item);
            }
        }
        if (request.status() != null
                && !List.of("aberta", "concluida").contains(request.status().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da lista inválido");
        }
        if (request.prioridade() != null
                && !List.of("alta", "media", "baixa").contains(request.prioridade().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade da lista inválida");
        }
    }

    private void validarItem(ListaCompraItemRequest item) {
        if (item == null || item.nomeProduto() == null || item.nomeProduto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do produto é obrigatório");
        }
        if (item.quantidade() == null || item.quantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }
        if (item.precoUnitario() == null || item.precoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço unitário deve ser maior que zero");
        }
    }

    private BigDecimal calcularTotal(List<ListaCompraItemRequest> itens) {
        if (itens == null) {
            return BigDecimal.ZERO;
        }
        return itens.stream()
                .map(item -> item.precoUnitario().multiply(BigDecimal.valueOf(item.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal calcularTotalItens(List<ListaCompraItem> itens) {
        if (itens == null) {
            return BigDecimal.ZERO;
        }
        return itens.stream()
                .filter(item -> item.getPrecoUnitario() != null && item.getQuantidade() != null)
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
