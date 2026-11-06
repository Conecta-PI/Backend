package back_conecta.services;

import back_conecta.dto.ClienteDetalheResponse;
import back_conecta.dto.ClienteRequest;
import back_conecta.dto.ClienteResponse;
import back_conecta.dto.ClienteResumoResponse;
import back_conecta.entities.Cliente;
import back_conecta.entities.Venda;
import back_conecta.enums.StatusVenda;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.VendaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public ClienteService(ClienteRepository clienteRepository, VendaRepository vendaRepository) {
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
    }

    private record Agregado(BigDecimal total, int quantidade, LocalDateTime ultimo, boolean pendente) {
    }

    private static Agregado agregar(List<Venda> vendas) {
        List<Venda> validas = vendas.stream()
                .filter(Objects::nonNull)
                .filter(venda -> venda.getStatusVenda() != StatusVenda.CANCELADA)
                .toList();

        BigDecimal total = validas.stream()
                .filter(venda -> venda.getStatusVenda() == StatusVenda.CONCLUIDA)
                .map(venda -> venda.getValorTotal() == null ? BigDecimal.ZERO : venda.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime ultimo = validas.stream()
                .map(Venda::getDataVenda)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        boolean pendente = validas.stream().anyMatch(venda -> venda.getStatusVenda() == StatusVenda.PENDENTE);

        return new Agregado(total, validas.size(), ultimo, pendente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        Map<Long, List<Venda>> vendasPorCliente = vendaRepository.findAll().stream()
                .filter(venda -> venda.getCliente() != null)
                .collect(Collectors.groupingBy(venda -> venda.getCliente().getIdCliente()));

        return clienteRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(cliente -> cliente.getNome() == null ? "" : cliente.getNome().toLowerCase()))
                .map(cliente -> {
                    Agregado dados = agregar(vendasPorCliente.getOrDefault(cliente.getIdCliente(), List.of()));
                    return ClienteResponse.from(cliente, dados.total(), dados.quantidade(), dados.ultimo(), dados.pendente());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteDetalheResponse buscar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        return ClienteDetalheResponse.from(cliente);
    }

    @Transactional
    public ClienteDetalheResponse criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome().trim());
        cliente.setTelefone(request.telefone().trim());
        cliente.setEmail(request.email() == null ? null : request.email().trim());
        cliente.setObservacoes(request.observacoes() == null ? null : request.observacoes().trim());

        if (request.email() != null && !request.email().isBlank()) {
            clienteRepository.findFirstByEmailIgnoreCase(request.email())
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente já cadastrado com este e-mail");
                    });
        }

        return ClienteDetalheResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDetalheResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (request.email() != null && !request.email().isBlank()) {
            clienteRepository.findFirstByEmailIgnoreCase(request.email())
                    .filter(existing -> !existing.getIdCliente().equals(id))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado para outro cliente");
                    });
        }

        cliente.setNome(request.nome().trim());
        cliente.setTelefone(request.telefone().trim());
        cliente.setEmail(request.email() == null || request.email().isBlank() ? null : request.email().trim());
        if (request.observacoes() != null) {
            cliente.setObservacoes(request.observacoes().isBlank() ? null : request.observacoes().trim());
        }

        return ClienteDetalheResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (cliente.getVendas() != null && !cliente.getVendas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir um cliente com vendas registradas");
        }

        clienteRepository.delete(cliente);
    }

    @Transactional
    public ClienteDetalheResponse atualizarObservacao(Long id, String observacao) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        cliente.setObservacoes(observacao == null || observacao.isBlank() ? null : observacao.trim());
        return ClienteDetalheResponse.from(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResumoResponse resumo(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        List<Venda> vendas = vendaRepository.findAllByClienteIdClienteOrderByDataVendaDesc(id);
        Agregado dados = agregar(vendas);

        List<ClienteResumoResponse.PedidoResumo> pedidos = vendas.stream()
                .filter(venda -> venda.getStatusVenda() != StatusVenda.CANCELADA)
                .map(venda -> new ClienteResumoResponse.PedidoResumo(
                        venda.getIdVenda(),
                        venda.getDataVenda(),
                        venda.getValorTotal(),
                        venda.getStatusVenda()
                ))
                .toList();

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
