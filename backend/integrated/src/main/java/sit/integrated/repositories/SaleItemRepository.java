package sit.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.integrated.entities.SaleItem;

import java.util.List;


public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {

}
