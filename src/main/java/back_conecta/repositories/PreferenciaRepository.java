package back_conecta.repositories;

import back_conecta.entities.Preferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaRepository extends JpaRepository<Preferencia, Long> {
    boolean existsByDescricao(String descricao);
}