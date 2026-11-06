package back_conecta.dto;

import back_conecta.entities.Cliente;

public record ClienteDetalheResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String observacoes
) {
    public static ClienteDetalheResponse from(Cliente cliente) {
        return new ClienteDetalheResponse(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getObservacoes()
        );
    }
}
