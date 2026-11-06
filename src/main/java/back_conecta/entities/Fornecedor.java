package back_conecta.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FORNECEDORES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private Long idFornecedor;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 80)
    private String categoria;

    @Column(length = 120)
    private String contato;

    @Column(length = 30)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @PrePersist
    @PreUpdate
    protected void aplicarValoresPadrao() {
        if (nome == null || nome.isBlank()) {
            nome = "Fornecedor";
        }
        if (categoria == null || categoria.isBlank()) {
            categoria = "Geral";
        }
        if (ativo == null) {
            ativo = true;
        }
    }
}
