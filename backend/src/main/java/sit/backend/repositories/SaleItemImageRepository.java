package sit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.backend.entities.SaleItem;
import sit.backend.entities.SaleItemImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
    @Modifying
    @Query("DELETE FROM SaleItemImage s WHERE s.fileName = :fileName AND s.saleItem.id = :saleitemId")
    void deleteByFileNameAndSaleItemId(@Param("fileName") String fileName, @Param("saleitemId") Integer saleItemId);

    boolean existsByFileName(String fileName);
    boolean existsBySaleItem(SaleItem saleItem);
    List<SaleItemImage> findAllBySaleItem(SaleItem saleItem);

    List<SaleItemImage> findSaleItemImageByFileName(String saleItemImageName);

    Optional<SaleItemImage> findBySaleItemAndFileName(SaleItem saleItem, String fileName);
}
