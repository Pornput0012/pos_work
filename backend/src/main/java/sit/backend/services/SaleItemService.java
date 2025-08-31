package sit.backend.services;

import jakarta.persistence.EntityManager;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sit.backend.ListMapper;
import sit.backend.dtos.*;
import sit.backend.entities.Brand;
import sit.backend.entities.SaleItem;
import sit.backend.entities.SaleItemImage;
import sit.backend.exception.BrandNotFound;
import sit.backend.exception.SaleItemNotFound;
import sit.backend.repositories.BrandRepository;
import sit.backend.repositories.SaleItemRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
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
    @Autowired
    private SaleItemImageService saleItemImageService;

    public List<SaleItem> getAllSaleItems() {
        return saleItemRepository.findAll();
    }

    public PageDto<SaleItemDto> getAllSaleItemsPage(
            Integer page,
            Integer size,
            List<String> filterBrands,
            String sortField,
            String sortDirection,
            List<Integer> filterStorages,
            Integer filterPriceLower,
            Integer filterPriceUpper) {

        boolean hasSortField = sortField != null && !sortField.isBlank();
        boolean sortDesc = "desc".equalsIgnoreCase(sortDirection);

        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "createdOn";
        }
        if (sortField.equals("brandName")) {
            sortField = "brand.name";
        }

        Sort sort;
        if (hasSortField) {
            sort = sortDesc
                ? Sort.by(Sort.Order.desc(sortField))
                : Sort.by(Sort.Order.asc(sortField));
            sort = sort.and(Sort.by(Sort.Order.asc("id")));
        } else {
            sort = sortDesc
                ? Sort.by(Sort.Order.desc("createdOn")).and(Sort.by(Sort.Order.desc("id")))
                : Sort.by(Sort.Order.asc("createdOn")).and(Sort.by(Sort.Order.asc("id")));
        }

        if (page == null || page < 0) page = 0;
        if (size == null || size <= 0) size = 10;
        Pageable pageable = PageRequest.of(page, size, sort);

        // Clean filterBrands
        List<String> cleanedBrands = null;
        if (filterBrands != null) {
            cleanedBrands = filterBrands.stream()
                .filter(brand -> brand != null && !brand.trim().isEmpty() && !brand.equals("[]"))
                .collect(Collectors.toList());
            if (cleanedBrands.isEmpty()) {
                cleanedBrands = null;
            }
        }

        // Convert price filters to BigDecimal
        BigDecimal priceLower = filterPriceLower != null ? BigDecimal.valueOf(filterPriceLower) : null;
        BigDecimal priceUpper = filterPriceUpper != null ? BigDecimal.valueOf(filterPriceUpper) : null;

        List<SaleItem> allItems = new java.util.ArrayList<>();
        if (filterStorages != null && !filterStorages.isEmpty()) {
            boolean hasZero = filterStorages.contains(-1);
            List<Integer> nonZeroStorages = filterStorages.stream().filter(ele -> ele != -1).toList();
            if (hasZero && !nonZeroStorages.isEmpty()) {
                // Query รอบแรก: เฉพาะเลขที่ไม่ใช่ 0
                Page<SaleItem> pageNonZero = saleItemRepository.findAllFilter(
                    pageable,
                    (cleanedBrands != null && !cleanedBrands.isEmpty()) ? cleanedBrands : null,
                    nonZeroStorages,
                    priceLower,
                    priceUpper
                );
                allItems.addAll(pageNonZero.getContent());
                // Query รอบสอง: เฉพาะ null
                Page<SaleItem> pageNull = saleItemRepository.findAllFilter(
                    pageable,
                    (cleanedBrands != null && !cleanedBrands.isEmpty()) ? cleanedBrands : null,
                    null,
                    priceLower,
                    priceUpper
                );
                allItems.addAll(pageNull.getContent());
                // สร้าง PageDto จาก allItems
                PageDto<SaleItemDto> result = new PageDto<SaleItemDto>();
                result.setContent(listMapper.mapList(allItems, SaleItemDto.class, modelMapper));
                result.setTotalPages(1); 
                result.setPage(page);
                result.setSize(size);
                return result;
            } else if (hasZero) {
                // มีแต่ 0: query เฉพาะ null
                Page<SaleItem> pageNull = saleItemRepository.findAllFilter(
                    pageable,
                    (cleanedBrands != null && !cleanedBrands.isEmpty()) ? cleanedBrands : null,
                    null,
                    priceLower,
                    priceUpper
                );
                return listMapper.toPageDTO(pageNull, SaleItemDto.class, modelMapper);
            } else {
                // มีแต่เลขอื่น: query ปกติ
                Page<SaleItem> pageNonZero = saleItemRepository.findAllFilter(
                    pageable,
                    (cleanedBrands != null && !cleanedBrands.isEmpty()) ? cleanedBrands : null,
                    filterStorages,
                    priceLower,
                    priceUpper
                );
                return listMapper.toPageDTO(pageNonZero, SaleItemDto.class, modelMapper);
            }
            } else if (filterStorages == null) {
                Page<SaleItem> pageDefault = saleItemRepository.findAllFilterAndNoFilterStorage(
                    pageable,
                    (cleanedBrands != null && !cleanedBrands.isEmpty()) ? cleanedBrands : null,
                    priceLower,
                    priceUpper
                );
                return listMapper.toPageDTO(pageDefault, SaleItemDto.class, modelMapper);
            }
        return null;
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id).orElseThrow(() -> new SaleItemNotFound(id));
    }

    public SaleItemResponseDtoV2 getProductByIdV2(Integer id) {
        SaleItem saleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new SaleItemNotFound(id));
        return modelMapper.map(saleItem, SaleItemResponseDtoV2.class);
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
    public SaleItemResponseDtoV2 createSaleItem(CreateSaleItemDtoV2 saleitem, List<MultipartFile> images)
            throws BadRequestException {

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
            List<MultipartFile> sortedImages = images.stream()
                    .sorted((img1, img2) -> {
                        int order1 = extractOrderFromFilename(img1.getOriginalFilename());
                        int order2 = extractOrderFromFilename(img2.getOriginalFilename());
                        return Integer.compare(order1, order2);
                    })
                    .collect(Collectors.toList());

            fileService.saveFile(sortedImages, prodRes);
        }

        saleItemRepository.flush();
        entityManager.refresh(prodRes);
        return modelMapper.map(prodRes, SaleItemResponseDtoV2.class);
    }

    private int extractOrderFromFilename(String filename) {
        if (filename == null) return 0;

        String[] parts = filename.split("\\.");
        if (parts.length >= 3) {
            try {
                return Integer.parseInt(parts[parts.length - 2]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
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

    @Transactional
    public SaleItemResponseDtoV2 updateProductV2(Integer id, CreateSaleItemDtoV2 product, String removeImage,
                                                   String orderImages, List<MultipartFile> newImages) {
        SaleItem existingProduct = saleItemRepository.findById(id)
                .orElseThrow(() -> new SaleItemNotFound(id));
        Brand existingBrand = brandRepository.findById(product.getBrandId())
                .orElseThrow(() -> new BrandNotFound(id));

        existingProduct.setModel(product.getModel());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setRamGb(product.getRamGb());
        existingProduct.setScreenSizeInch(product.getScreenSizeInch());
        existingProduct.setStorageGb(product.getStorageGb());
        existingProduct.setColor(product.getColor());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setBrand(existingBrand);

        // ลบรูปภาพที่ระบุ
        if (removeImage != null && !removeImage.trim().isEmpty()) {
            List<String> removeImageList = Arrays.asList(removeImage.split(","));
            fileService.removeFiles(existingProduct, removeImageList);
        }

        // เพิ่มรูปภาพใหม่ที่ส่งมา
        if (newImages != null && !newImages.isEmpty()) {
            if (newImages.size() > 4) {
                throw new IllegalArgumentException("Images Size Exceeded");
            }
            fileService.saveFile(newImages, existingProduct);
        }

        // จัดการ order ของรูปภาพ
        if (orderImages != null && !orderImages.trim().isEmpty()) {
            // รูปแบบ:
            // fileName_1,orderImage_1|fileName_2,orderImage_2|fileName_3,orderImage_3
            String[] imageOrderPairs = orderImages.split("\\|");
            for (String pair : imageOrderPairs) {
                if (pair.trim().isEmpty())
                    continue;
                String[] parts = pair.split(",");
                if (parts.length != 2)
                    continue;
                String fileNameRaw = parts[0].trim();
                try {
                    Integer newOrder = Integer.parseInt(parts[1].trim());
                    // ตัด order เดิมออก เช่น B.2.1.jpg -> B.2.jpg หรือ B.2.1.png -> B.2.png
                    String[] fileNameSplit = fileNameRaw.split("\\.");
                    if (fileNameSplit.length < 3)
                        continue; // ต้องมีอย่างน้อยชื่อ, order, นามสกุล
                    // สร้างชื่อไฟล์สำหรับ DB เช่น B.2.jpg
                    String fileNameForDb = fileNameSplit[0] + "." + fileNameSplit[1] + "." + fileNameSplit[2];
                    String extension = fileNameSplit[fileNameSplit.length - 1];
                    // หาใน DB ด้วยชื่อที่ตัดแล้ว
                    SaleItemImage existingImage = saleItemImageService.findBySaleItemAndFileName(existingProduct,
                            fileNameForDb);
                    if (existingImage != null) {
                        // สร้างชื่อไฟล์ใหม่ เช่น B.1.jpg หรือ B.1.png
                        String newFileName = fileNameSplit[0] + "." + newOrder + "." + extension;
                        // ถ้าชื่อไฟล์เปลี่ยน
                        if (!existingImage.getFileName().equals(newFileName)) {
                            // เปลี่ยนชื่อไฟล์ในโฟลเดอร์
                            fileService.renameFile( existingImage.getFileName(), newFileName);
                            // เปลี่ยนชื่อไฟล์ใน DB
                            saleItemImageService.updateFileName(existingImage.getId(), newFileName);
                        }
                        // อัปเดต order ใหม่ถ้าไม่ตรง
                        if (!newOrder.equals(existingImage.getImageViewOrder())) {
                            saleItemImageService.updateImageOrder(existingImage.getId(), newOrder);
                        }
                    } else {
                        // ไฟล์ไม่มีในฐานข้อมูล แสดงว่าถูกลบไปแล้ว
                        // ไม่ต้องทำอะไร เพราะไฟล์ถูกลบไปแล้ว
                    }
                } catch (NumberFormatException e) {
                    // ถ้า order ไม่ใช่ตัวเลข ข้ามไป
                    continue;
                }
            }
        }

        SaleItem updatedProduct = saleItemRepository.save(existingProduct);

        saleItemRepository.flush();
        entityManager.refresh(updatedProduct);

        return modelMapper.map(updatedProduct, SaleItemResponseDtoV2.class);
    }

    @Transactional
    public SaleItemResponseDtoV2 updateProductV2(Integer id, String removeImage, String orderImages,
                                                   List<MultipartFile> newImages) {
        SaleItem existingProduct = saleItemRepository.findById(id)
                .orElseThrow(() -> new SaleItemNotFound(id));

        // ลบรูปภาพที่ระบุ
        if (removeImage != null && !removeImage.trim().isEmpty()) {
            List<String> removeImageList = Arrays.asList(removeImage.split(","));
            fileService.removeFiles(existingProduct, removeImageList);
        }

        // เพิ่มรูปภาพใหม่ที่ส่งมา
        if (newImages != null && !newImages.isEmpty()) {
            if (newImages.size() > 4) {
                throw new IllegalArgumentException("Images Size Exceeded");
            }
            fileService.saveFile(newImages, existingProduct);
        }

        // อัปเดตลำดับและชื่อไฟล์ตาม orderImages ที่ส่งเข้ามา
        if (orderImages != null && !orderImages.trim().isEmpty()) {
            String[] imageOrderPairs = orderImages.split("\\|");
            for (String pair : imageOrderPairs) {
                if (pair.trim().isEmpty()) {
                    System.out.println("[DEBUG] Empty pair, skip");
                    continue;
                }
                String[] parts = pair.split(",");
                if (parts.length != 2) {
                    System.out.println("[DEBUG] Invalid pair format: " + pair);
                    continue;
                }
                String fileNameRaw = parts[0].trim();
                String orderStr = parts[1].trim();
                System.out.println("[DEBUG] Processing file: " + fileNameRaw + ", order: " + orderStr);
                try {
                    Integer newOrder = Integer.parseInt(orderStr);
                    String[] fileNameSplit = fileNameRaw.split("\\.");
                    if (fileNameSplit.length < 3) {
                        System.out.println("[DEBUG] fileNameSplit < 3: " + fileNameRaw);
                        continue;
                    }
                    String baseName = fileNameSplit[0];
                    String extension = fileNameSplit[fileNameSplit.length - 1];
                    String oldOrder = fileNameSplit[1];
                    String fileNameForDb = baseName + "." + oldOrder + "." + extension;
                    String newFileName = baseName + "." + newOrder + "." + extension;
                    SaleItemImage existingImage = saleItemImageService.findBySaleItemAndFileName(existingProduct,
                            fileNameForDb);
                    if (existingImage != null) {
                        System.out.println("[DEBUG] Found image in DB: " + fileNameForDb);
                        if (!existingImage.getFileName().equals(newFileName)) {
                            System.out.println(
                                    "[DEBUG] Rename file " + existingImage.getFileName() + " -> " + newFileName);
                            fileService.renameFile( existingImage.getFileName(), newFileName);
                            saleItemImageService.updateFileName(existingImage.getId(), newFileName);
                        }
                        if (!newOrder.equals(existingImage.getImageViewOrder())) {
                            System.out.println("[DEBUG] Update imageViewOrder for " + newFileName + " to " + newOrder);
                            saleItemImageService.updateImageOrder(existingImage.getId(), newOrder);
                        }
                    } else {
                        System.out.println("[DEBUG] Image not found in DB: " + fileNameForDb);
                        if (fileService.fileExists(existingProduct, fileNameRaw)) {
                            System.out.println("[DEBUG] File exists in folder, save to DB: " + fileNameRaw);
                            SaleItemImage newImage = new SaleItemImage();
                            newImage.setSaleItem(existingProduct);
                            newImage.setFileName(fileNameRaw);
                            newImage.setImageViewOrder(newOrder);
                            saleItemImageService.saveImage(newImage);
                        } else {
                            System.out.println("[DEBUG] File not found in folder: " + fileNameRaw);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[DEBUG] Invalid order number: " + orderStr);
                    continue;
                }
            }
        }

        SaleItem updatedProduct = saleItemRepository.save(existingProduct);

        saleItemRepository.flush();
        entityManager.refresh(updatedProduct);

        return modelMapper.map(updatedProduct, SaleItemResponseDtoV2.class);
    }

}
