package back_conecta.services;

import back_conecta.dto.EncomendaItemRequest;
import back_conecta.dto.EncomendaRequest;
import back_conecta.dto.EncomendaResponse;
import back_conecta.entities.Cliente;
import back_conecta.entities.Encomenda;
import back_conecta.entities.EncomendaItem;
import back_conecta.entities.Produto;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.EncomendaRepository;
import back_conecta.repositories.ProdutoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public EncomendaService(
            EncomendaRepository encomendaRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository
    ) {
        this.encomendaRepository = encomendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<EncomendaResponse> listar() {
        return encomendaRepository.findAllByOrderByDataPedidoDesc()
                .stream()
                .map(EncomendaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EncomendaResponse buscar(Long id) {
        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Encomenda não encontrada"
                ));

        return EncomendaResponse.from(encomenda);
    }

    @Transactional
    public EncomendaResponse criar(EncomendaRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da encomenda não informados"
            );
        }

        if (request.clienteId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione um cliente"
            );
        }

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"
                ));

        if (request.itens() == null || request.itens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Adicione pelo menos um produto à encomenda"
            );
        }

        if (request.dataEntrega() == null || request.dataEntrega().isBlank()
                || request.horario() == null || request.horario().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe a data e o horário da entrega"
            );
        }

        LocalDateTime dataEntrega;

        try {
            dataEntrega = LocalDate.parse(request.dataEntrega())
                    .atTime(LocalTime.parse(request.horario()));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data ou horário inválido"
            );
        }

        String tipoEntrega = request.tipoEntrega() == null
                ? ""
                : request.tipoEntrega().trim().toLowerCase();

        if (!tipoEntrega.equals("retirada") && !tipoEntrega.equals("entrega")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de entrega inválido"
            );
        }

        List<EncomendaItem> itens = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (EncomendaItemRequest itemRequest : request.itens()) {

            if (itemRequest == null
                    || itemRequest.produtoId() == null
                    || itemRequest.quantidade() == null
                    || itemRequest.quantidade() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Produto ou quantidade inválida"
                );
            }

            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Produto não encontrado: " + itemRequest.produtoId()
                    ));

            if (!Boolean.TRUE.equals(produto.getAtivo())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O produto " + produto.getNomeProduto() + " está inativo"
                );
            }

            BigDecimal precoUnitario = produto.getPrecoVenda();

            if (precoUnitario == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O produto " + produto.getNomeProduto()
                                + " não possui preço cadastrado"
                );
            }

            BigDecimal subtotal = precoUnitario.multiply(
                    BigDecimal.valueOf(itemRequest.quantidade())
            );

            valorTotal = valorTotal.add(subtotal);

            EncomendaItem item = new EncomendaItem(
                    produto.getNomeProduto(),
                    itemRequest.quantidade(),
                    precoUnitario
            );

            itens.add(item);
        }

        BigDecimal sinalPago = request.sinalPago() == null
                ? BigDecimal.ZERO
                : request.sinalPago();

        if (sinalPago.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O sinal pago não pode ser negativo"
            );
        }

        Encomenda encomenda = new Encomenda();

        encomenda.setCodigo(
                "ENC-" + System.currentTimeMillis()
                        + "-" + UUID.randomUUID().toString().substring(0, 8)
        );

        encomenda.setClienteId(cliente.getIdCliente());
        encomenda.setClienteNome(cliente.getNome());
        encomenda.setStatus("pendente");
        encomenda.setDataPedido(LocalDateTime.now());
        encomenda.setDataEntrega(dataEntrega);
        encomenda.setTipoEntrega(tipoEntrega);
        encomenda.setSinalPago(sinalPago);
        encomenda.setValorTotal(valorTotal);
        encomenda.setObservacao(
                request.observacoes() == null
                        ? null
                        : request.observacoes().trim()
        );
        encomenda.setItens(itens);

        Encomenda salva = encomendaRepository.save(encomenda);

        return EncomendaResponse.from(salva);
    }

    @Transactional
    public EncomendaResponse atualizarStatus(Long id, String status) {

        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Encomenda não encontrada"
                ));

        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da encomenda é obrigatório");
        }

        String novoStatus = status.trim().toLowerCase();
        if (!List.of("pendente", "em_producao", "pronta", "entregue", "cancelado").contains(novoStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da encomenda inválido");
        }
        encomenda.setStatus(novoStatus);

        return EncomendaResponse.from(encomendaRepository.save(encomenda));
    }
}