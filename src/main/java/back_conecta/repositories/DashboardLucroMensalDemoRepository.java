package back_conecta.repositories;

import back_conecta.entities.DashboardLucroMensalDemo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardLucroMensalDemoRepository extends JpaRepository<DashboardLucroMensalDemo, Long> {
    List<DashboardLucroMensalDemo> findAllByDashboardIdOrderByOrdem(Long dashboardId);
}
