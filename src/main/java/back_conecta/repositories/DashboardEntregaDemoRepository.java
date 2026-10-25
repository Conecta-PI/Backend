package back_conecta.repositories;

import back_conecta.entities.DashboardEntregaDemo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardEntregaDemoRepository extends JpaRepository<DashboardEntregaDemo, Long> {
    List<DashboardEntregaDemo> findAllByDashboardIdOrderById(Long dashboardId);
}
