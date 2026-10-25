package back_conecta.dto;

import back_conecta.entities.Cliente;

public record ClienteResponse(Long id, String nome, String telefone, String email) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail()
        );
    }
}
