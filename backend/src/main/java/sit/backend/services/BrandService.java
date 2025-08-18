package sit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.backend.dtos.BrandDetailDto;
import sit.backend.entities.Brand;
import sit.backend.exception.BrandNotFound;
import sit.backend.repositories.BrandRepository;

import java.time.Instant;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;


    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound(id));
    }

    public Brand createBrand(Brand brand) {

        validateBrand(brand);
        validateDuplicateName(brand.getName(), null);
        Instant now = Instant.now();
        brand.setCreatedOn(now);
        brand.setUpdatedOn(now);
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Integer id, BrandDetailDto dto) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound(id));

        String newName = dto.getName().trim();
        validateDuplicateName(newName, id);

        existing.setName(newName);
        existing.setWebsiteUrl(trimIfNotNull(dto.getWebsiteUrl()));
        existing.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        existing.setCountryOfOrigin(trimIfNotNull(dto.getCountryOfOrigin()));
        existing.setUpdatedOn(Instant.now());

        validateBrand(existing);
        return brandRepository.save(existing);
    }

    private String trimIfNotNull(String value) {
        return value != null ? value.trim() : null;
    }

    private void validateBrand(Brand brand) {
        if (brand.getName() == null || brand.getName().trim().isEmpty() || brand.getName().trim().length() > 30) {
            throw new IllegalArgumentException("Invalid brand name");
        }
        if (brand.getWebsiteUrl() != null && (brand.getWebsiteUrl().trim().isEmpty() || brand.getWebsiteUrl().trim().length() > 40)) {
            throw new IllegalArgumentException("Invalid website URL");
        }
        if (brand.getCountryOfOrigin() != null && (brand.getCountryOfOrigin().trim().isEmpty() || brand.getCountryOfOrigin().trim().length() > 80)) {
            throw new IllegalArgumentException("Invalid country of origin");
        }
        brand.setName(brand.getName().trim());
        brand.setWebsiteUrl(trimIfNotNull(brand.getWebsiteUrl()));
        brand.setCountryOfOrigin(trimIfNotNull(brand.getCountryOfOrigin()));
    }

    private void validateDuplicateName(String name, Integer excludeId) {
        List<Brand> allBrands = brandRepository.findAll();
        for (Brand b : allBrands) {
            if (b.getName().trim().equalsIgnoreCase(name.trim()) &&
                    (excludeId == null || !b.getId().equals(excludeId))) {
                throw new IllegalArgumentException("Duplicated brand name");
            }
        }
    }


    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound(id));

        if (brand.getSaleItems() != null && !brand.getSaleItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete brand with associated sale items.");
        }

        brandRepository.delete(brand);
    }
}
