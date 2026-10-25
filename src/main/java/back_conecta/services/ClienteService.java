package back_conecta.services;

import back_conecta.dto.ClienteResponse;
import back_conecta.repositories.ClienteRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(cliente -> cliente.getNome().toLowerCase()))
                .map(ClienteResponse::from)
                .toList();
    }
}
