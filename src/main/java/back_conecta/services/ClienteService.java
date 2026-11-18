
package back_conecta.services;

import back_conecta.dto.ClienteDetalheResponse;
import back_conecta.dto.ClienteRequest;
import back_conecta.dto.ClienteResponse;
import back_conecta.dto.ClienteResumoResponse;
import back_conecta.entities.Cliente;
import back_conecta.entities.Encomenda;
import back_conecta.entities.Venda;
import back_conecta.enums.StatusVenda;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.EncomendaRepository;
import back_conecta.repositories.VendaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;
    private final EncomendaRepository encomendaRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            VendaRepository vendaRepository,
            EncomendaRepository encomendaRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
        this.encomendaRepository = encomendaRepository;
    }

    private record Agregado(
            BigDecimal total,
            int quantidade,
            LocalDateTime ultimo,
            boolean pendente
    ) {
    }

    private static StatusVenda converterStatusEncomenda(String status) {
        if (status == null || status.isBlank()) {
            return StatusVenda.PENDENTE;
        }

        try {
            return StatusVenda.fromValue(status);
        } catch (IllegalArgumentException e) {
            return StatusVenda.PENDENTE;
        }
    }

    private static Agregado agregar(
            List<Venda> vendas,
            List<Encomenda> encomendas
    ) {
        List<Venda> vendasValidas = vendas.stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getStatusVenda() != StatusVenda.CANCELADA)
                .toList();

        List<Encomenda> encomendasValidas = encomendas.stream()
                .filter(Objects::nonNull)
                .filter(e -> converterStatusEncomenda(e.getStatus()) != StatusVenda.CANCELADA)
                .toList();

        BigDecimal totalVendas = vendasValidas.stream()
                .filter(v -> v.getStatusVenda() == StatusVenda.CONCLUIDA)
                .map(v -> v.getValorTotal() == null ? BigDecimal.ZERO : v.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEncomendas = encomendasValidas.stream()
                .filter(e -> converterStatusEncomenda(e.getStatus()) == StatusVenda.CONCLUIDA)
                .map(e -> e.getValorTotal() == null ? BigDecimal.ZERO : e.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime ultimoVenda = vendasValidas.stream()
                .map(Venda::getDataVenda)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        LocalDateTime ultimaEncomenda = encomendasValidas.stream()
                .map(Encomenda::getDataPedido)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        LocalDateTime ultimo = ultimoVenda == null ? ultimaEncomenda
                : ultimaEncomenda == null ? ultimoVenda
                : ultimoVenda.isAfter(ultimaEncomenda) ? ultimoVenda : ultimaEncomenda;

        boolean pendente = vendasValidas.stream()
                .anyMatch(v -> v.getStatusVenda() == StatusVenda.PENDENTE)
                || encomendasValidas.stream()
                .anyMatch(e -> converterStatusEncomenda(e.getStatus()) == StatusVenda.PENDENTE);

        return new Agregado(
                totalVendas.add(totalEncomendas),
                vendasValidas.size() + encomendasValidas.size(),
                ultimo,
                pendente
        );
    }

    // Lista clientes ativos ou inativos, mantendo os mesmos dados e cálculos.
    @Transactional(readOnly = true)
    private List<ClienteResponse> listarPorSituacao(boolean ativo) {

        Map<Long, List<Venda>> vendasPorCliente = vendaRepository.findAll().stream()
                .filter(venda -> venda.getCliente() != null)
                .collect(Collectors.groupingBy(
                        venda -> venda.getCliente().getIdCliente()
                ));

        Map<Long, List<Encomenda>> encomendasPorCliente = encomendaRepository.findAll().stream()
                .filter(encomenda -> encomenda.getClienteId() != null)
                .collect(Collectors.groupingBy(Encomenda::getClienteId));

        return clienteRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .filter(cliente -> Boolean.valueOf(ativo).equals(cliente.getAtivo()))
                .sorted(Comparator.comparing(cliente ->
                        cliente.getNome() == null ? "" : cliente.getNome().toLowerCase()))
                .map(cliente -> {
                    Long clienteId = cliente.getIdCliente();

                    Agregado dados = agregar(
                            vendasPorCliente.getOrDefault(clienteId, List.of()),
                            encomendasPorCliente.getOrDefault(clienteId, List.of())
                    );

                    return ClienteResponse.from(
                            cliente,
                            dados.total(),
                            dados.quantidade(),
                            dados.ultimo(),
                            dados.pendente()
                    );
                })
                .toList();
    }

    // Lista somente clientes ativos.
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return listarPorSituacao(true);
    }

    // Lista somente clientes inativos.
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarInativos() {
        return listarPorSituacao(false);
    }

    @Transactional(readOnly = true)
    public ClienteDetalheResponse buscar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return ClienteDetalheResponse.from(cliente);
    }

    @Transactional
    public ClienteDetalheResponse criar(ClienteRequest request) {
        Cliente cliente = new Cliente();

        cliente.setAtivo(true);
        cliente.setNome(request.nome().trim());
        cliente.setTelefone(request.telefone().trim());
        cliente.setEmail(request.email() == null ? null : request.email().trim());
        cliente.setObservacoes(request.observacoes() == null ? null : request.observacoes().trim());

        if (request.email() != null && !request.email().isBlank()) {
            clienteRepository.findFirstByEmailIgnoreCase(request.email())
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Cliente já cadastrado com este e-mail"
                        );
                    });
        }

        return ClienteDetalheResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDetalheResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"
                ));

        if (request.email() != null && !request.email().isBlank()) {
            clienteRepository.findFirstByEmailIgnoreCase(request.email())
                    .filter(existing -> !existing.getIdCliente().equals(id))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "E-mail já cadastrado para outro cliente"
                        );
                    });
        }

        cliente.setNome(request.nome().trim());
        cliente.setTelefone(request.telefone().trim());
        cliente.setEmail(request.email() == null || request.email().isBlank()
                ? null : request.email().trim());

        if (request.observacoes() != null) {
            cliente.setObservacoes(request.observacoes().isBlank()
                    ? null : request.observacoes().trim());
        }

        return ClienteDetalheResponse.from(clienteRepository.save(cliente));
    }

    // Desativação lógica: preserva vendas, encomendas e histórico.
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"
                ));

        if (Boolean.FALSE.equals(cliente.getAtivo())) {
            return;
        }

        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    // Reativa um cliente sem alterar seu histórico.
    @Transactional
    public void reativar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"
                ));

        if (Boolean.TRUE.equals(cliente.getAtivo())) {
            return;
        }

        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }

    @Transactional
    public ClienteDetalheResponse atualizarObservacao(Long id, String observacao) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"));

        cliente.setObservacoes(observacao == null || observacao.isBlank()
                ? null : observacao.trim());

        return ClienteDetalheResponse.from(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResumoResponse resumo(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        List<Venda> vendas =
                vendaRepository.findAllByClienteIdClienteOrderByDataVendaDesc(id);

        List<Encomenda> encomendas =
                encomendaRepository.findAllByClienteIdOrderByDataPedidoDesc(id);

        Agregado dados = agregar(vendas, encomendas);

        List<ClienteResumoResponse.PedidoResumo> pedidos = new ArrayList<>();

        vendas.stream()
                .filter(venda -> venda.getStatusVenda() != StatusVenda.CANCELADA)
                .map(venda -> new ClienteResumoResponse.PedidoResumo(
                        venda.getIdVenda(),
                        venda.getDataVenda(),
                        venda.getValorTotal(),
                        venda.getStatusVenda()
                ))
                .forEach(pedidos::add);

        encomendas.stream()
                .filter(encomenda ->
                        converterStatusEncomenda(encomenda.getStatus()) != StatusVenda.CANCELADA)
                .map(encomenda -> new ClienteResumoResponse.PedidoResumo(
                        encomenda.getIdEncomenda(),
                        encomenda.getDataPedido(),
                        encomenda.getValorTotal(),
                        converterStatusEncomenda(encomenda.getStatus())
                ))
                .forEach(pedidos::add);

        pedidos.sort(Comparator.comparing(
                ClienteResumoResponse.PedidoResumo::data,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return new ClienteResumoResponse(
                cliente.getIdCliente(),
                cliente.getNome(),
                dados.total(),
                dados.quantidade(),
                dados.ultimo(),
                dados.pendente(),
                pedidos
        );
    }
}
