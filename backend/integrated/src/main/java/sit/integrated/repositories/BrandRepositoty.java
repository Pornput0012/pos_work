package sit.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.integrated.entities.Brand;


public interface BrandRepositoty extends JpaRepository<Brand, Integer> {
}
