package sit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.backend.entities.SaleItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    List<SaleItem> findByBrandIdIn(List<Integer> brandIds);


    @Query("SELECT s FROM SaleItem s WHERE (:filterBrands IS NULL OR s.brand.name IN :filterBrands) " +
            "AND ( " +
            "      (:filterStorage IS NULL AND s.storageGb IS NULL) " +
            "   OR (:filterStorage IS NOT NULL AND s.storageGb IN :filterStorage) " +
            ") " +
            "AND (:filterPriceLower IS NULL OR s.price >= :filterPriceLower) " +
            "AND (:filterPriceUpper IS NULL OR s.price <= :filterPriceUpper) " +
            "AND (:searchKeyWord IS NULL OR " +
            "     LOWER(CAST(s.description AS string)) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(CAST(s.model AS string)) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(CAST(s.color AS string)) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) ) "
    )
    Page<SaleItem> findAllFilter(
            Pageable page,
            @Param("filterBrands") List<String> filterBrands,
            @Param("filterStorage") List<Integer> filterStorage,
            @Param("filterPriceLower") java.math.BigDecimal filterPriceLower,
            @Param("filterPriceUpper") java.math.BigDecimal filterPriceUpper,
            @Param("searchKeyWord") String searchKeyword
    );

    @Query("SELECT s FROM SaleItem s WHERE (:filterBrands IS NULL OR s.brand.name IN :filterBrands) " +
            "AND (:filterPriceLower IS NULL OR s.price >= :filterPriceLower) " +
            "AND (:filterPriceUpper IS NULL OR s.price <= :filterPriceUpper) " +
            "AND (:searchKeyWord IS NULL OR " +
            "     LOWER(CAST(s.description AS string)) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(CAST(s.model AS string)) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(CAST(s.color AS string)) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) ) "
    )
    Page<SaleItem> findAllFilterAndNoFilterStorage(
            Pageable page,
            @Param("filterBrands") List<String> filterBrands,
            @Param("filterPriceLower") java.math.BigDecimal filterPriceLower,
            @Param("filterPriceUpper") java.math.BigDecimal filterPriceUpper,
            @Param("searchKeyWord") String searchKeyWord
    );
}
