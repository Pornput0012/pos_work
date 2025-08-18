package sit.backend.services;

import jakarta.persistence.EntityManager;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sit.backend.ListMapper;
import sit.backend.dtos.*;
import sit.backend.entities.Brand;
import sit.backend.entities.SaleItem;
import sit.backend.exception.BrandNotFound;
import sit.backend.exception.SaleItemNotFound;
import sit.backend.repositories.BrandRepository;
import sit.backend.repositories.SaleItemRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private EntityManager entityManager;
    // เพิ่มบรรทัดที่ 20
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;
    @Autowired
    private FileService fileService;


    public List<SaleItem> getAllSaleItems() {
        return saleItemRepository.findAll();
    }

    public PageDto<SaleItemDto> getAllSaleItemsPage(
            Integer page,
            Integer size,
            List<String> filterBrands,
            String sortField,
            String sortDirection,
            List<Integer> filterStorage,
            Integer filterPriceLower,
            Integer filterPriceUpper) {

        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "createdOn";
        }

        if (sortField.equals("brandName")) {
            sortField = "brand.name";
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }

        // page และ size validation
        if (page == null || page < 0)
            page = 0;
        if (size == null || size <= 0)
            size = 10;

        Sort sort = Sort.by(new Sort.Order(direction, sortField))
                .and(Sort.by(Sort.Direction.ASC, "createdOn"));
        Pageable pageable = PageRequest.of(page, size, sort);

        // Clean filterBrands
        List<String> cleanedBrands = (filterBrands == null) ? null : filterBrands.stream()
                .filter(brand -> brand != null && !brand.trim().isEmpty() && !brand.equals("[]"))
                .collect(Collectors.toList());

        // Convert price filters to BigDecimal
        java.math.BigDecimal priceLower = filterPriceLower != null ? java.math.BigDecimal.valueOf(filterPriceLower) : null;
        java.math.BigDecimal priceUpper = filterPriceUpper != null ? java.math.BigDecimal.valueOf(filterPriceUpper) : null;

        // If all filters are null/empty, use findAll
        boolean noFilter = (cleanedBrands == null || cleanedBrands.isEmpty()) &&
                           (filterStorage == null || filterStorage.isEmpty()) &&
                           priceLower == null && priceUpper == null;

        Page<SaleItem> saleItemPage;
        if (noFilter) {
            saleItemPage = saleItemRepository.findAll(pageable);
        } else {
            saleItemPage = saleItemRepository.findAllFilter(
                pageable,
                cleanedBrands,
                filterStorage,
                priceLower,
                priceUpper
            );
        }

        return listMapper.toPageDTO(saleItemPage, SaleItemDto.class, modelMapper);
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id).orElseThrow(() -> new SaleItemNotFound(id));
    }


    // เพิ่มบรรทัดที่ 31 - 47
    public SaleItem createSaleItem(CreateSaleItemDto dto) {
        var brand = brandRepository.findById(dto.getBrand().getId())
                .orElseThrow(() -> new RuntimeException("Brand not found: " + dto.getBrand().getId()));

        SaleItem item = new SaleItem();
        item.setBrand(brand);
        item.setModel(dto.getModel().trim());
        item.setPrice(dto.getPrice());
        item.setDescription(dto.getDescription().trim());
        item.setRamGb(dto.getRamGb());
        item.setScreenSizeInch(dto.getScreenSizeInch());
        item.setStorageGb(dto.getStorageGb());
        item.setColor(dto.getColor());
        item.setQuantity(dto.getQuantity());

        return saleItemRepository.save(item);
    }

    @Transactional
    public SaleItemResponseDtoV2 createSaleItem(CreateSaleItemDtoV2 saleitem, List<MultipartFile> images) throws BadRequestException {

        if (images != null && images.size() > 4) {
            throw new BadRequestException("Images Size Exceeded");
        }
        Brand existingBrand = brandRepository.findById(saleitem.getBrandId())
                .orElseThrow(() -> new BrandNotFound(saleitem.getBrandId()));

        SaleItem creatingProduct = modelMapper.map(saleitem, SaleItem.class);
        creatingProduct.setBrand(existingBrand);
        creatingProduct.setId(null);

        SaleItem prodRes = saleItemRepository.save(creatingProduct);
        if (images != null && !images.isEmpty()) {
            fileService.saveFile(images, prodRes);
        }

        saleItemRepository.flush();

        entityManager.refresh(prodRes);
        return modelMapper.map(prodRes, SaleItemResponseDtoV2.class);
    }

    public SaleItem updateSaleItem(Integer id, CreateSaleItemDto dto) {
        SaleItem existing = saleItemRepository.findById(id)
                .orElseThrow(() -> new SaleItemNotFound(id));

        Brand brand;
        if (dto.getBrand() == null || dto.getBrand().getId() == null) {
            brand = existing.getBrand();
        } else {
            brand = brandRepository.findById(dto.getBrand().getId())
                    .orElseThrow(() -> new RuntimeException("Brand not found: " + dto.getBrand().getId()));
        }

        existing.setModel(dto.getModel().trim());
        existing.setDescription(dto.getDescription().trim());
        existing.setPrice(dto.getPrice());
        existing.setRamGb(dto.getRamGb());
        existing.setScreenSizeInch(dto.getScreenSizeInch());
        existing.setStorageGb(dto.getStorageGb());
        existing.setColor(dto.getColor() != null && !dto.getColor().trim().isEmpty()
                ? dto.getColor().trim()
                : null);
        existing.setQuantity(dto.getQuantity());
        existing.setBrand(brand);

        existing.setUpdatedOn(Instant.now());

        return saleItemRepository.save(existing);
    }

    public void deleteSaleItem(Integer id) {
        SaleItem item = saleItemRepository.findById(id)
                .orElseThrow(() -> new SaleItemNotFound(id));
        saleItemRepository.delete(item);
    }

    public List<SaleItem> getSaleItemsByBrandIds(List<Integer> brandIds) {
        if (brandIds == null || brandIds.isEmpty()) {
            return saleItemRepository.findAll();
        }
        return saleItemRepository.findByBrandIdIn(brandIds);
    }
}
