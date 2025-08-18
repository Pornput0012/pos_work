package sit.backend.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.backend.dtos.BrandDetailDto;
import sit.backend.dtos.BrandResponseDto;
import sit.backend.dtos.CreateBrandDto;
import sit.backend.entities.Brand;
import sit.backend.services.BrandService;

import java.util.List;

@RestController
@RequestMapping("/v1/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<List<BrandResponseDto>> getAllBrands() {
        var brands = brandService.getAllBrands();
        var brandResponseDtos = brands.stream()
                .map(brand -> {
                    BrandResponseDto dto = modelMapper.map(brand, BrandResponseDto.class);
                    dto.setNoOfSaleItems(brand.getSaleItems() != null ? brand.getSaleItems().size() : 0);
                    return dto;
                }).toList();
        return ResponseEntity.ok(brandResponseDtos);
    }

    @GetMapping("{id}")
    public ResponseEntity<BrandResponseDto> getBrandById(@PathVariable Integer id) {
        Brand brand = brandService.getBrandById(id);
        BrandResponseDto dto = modelMapper.map(brand, BrandResponseDto.class);
        dto.setNoOfSaleItems(brand.getSaleItems() != null ? brand.getSaleItems().size() : 0);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("")
    public ResponseEntity<BrandResponseDto> createBrand(@RequestBody CreateBrandDto createBrandDto) {
        Brand brand = modelMapper.map(createBrandDto, Brand.class);
        Brand createdBrand = brandService.createBrand(brand);
        BrandResponseDto responseDto = modelMapper.map(createdBrand, BrandResponseDto.class);
        responseDto.setNoOfSaleItems(0);
        return ResponseEntity.status(201).body(responseDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<BrandResponseDto> updateBrand(@PathVariable Integer id, @RequestBody BrandDetailDto dto) {
        Brand updated = brandService.updateBrand(id, dto);
        BrandResponseDto response = modelMapper.map(updated, BrandResponseDto.class);
        response.setNoOfSaleItems(updated.getSaleItems() != null ? updated.getSaleItems().size() : 0);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
