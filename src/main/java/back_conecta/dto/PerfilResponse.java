package back_conecta.dto;

import back_conecta.entities.Usuario;

public record PerfilResponse(
        String nome,
        String negocio,
        Boolean premium,
        String telefone,
        String email,
        String cnpj,
        String chavePix,
        String foto,
        Resumo resumo
) {
    public static PerfilResponse from(Usuario usuario, long pedidos, long clientes, long produtos) {
        return new PerfilResponse(
                usuario.getNome(),
                usuario.getNomeNegocio(),
                usuario.getPremium() != null && usuario.getPremium(),
                usuario.getTelefone(),
                usuario.getEmail(),
                usuario.getCnpj(),
                usuario.getChavePix(),
                usuario.getFoto(),
                new Resumo((int) pedidos, (int) clientes, (int) produtos)
        );
    }

    public record Resumo(int pedidos, int clientes, int produtos) {}
}
