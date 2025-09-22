package sit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.backend.entities.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

}
