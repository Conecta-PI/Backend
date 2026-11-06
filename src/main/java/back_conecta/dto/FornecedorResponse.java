package back_conecta.dto;

import back_conecta.entities.Fornecedor;

public record FornecedorResponse(
        Long id,
        String nome,
        String categoria,
        String contato,
        String telefone,
        String email,
        String observacao,
        Boolean ativo
) {
    public static FornecedorResponse from(Fornecedor fornecedor) {
        return new FornecedorResponse(
                fornecedor.getIdFornecedor(),
                fornecedor.getNome(),
                fornecedor.getCategoria(),
                fornecedor.getContato(),
                fornecedor.getTelefone(),
                fornecedor.getEmail(),
                fornecedor.getObservacao(),
                fornecedor.getAtivo()
        );
    }
}
