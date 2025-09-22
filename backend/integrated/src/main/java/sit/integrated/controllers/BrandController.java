package sit.integrated.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.integrated.entities.Brand;
import sit.integrated.services.BrandService;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop/v1/brands")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("")
    public ResponseEntity<List<Brand>> getAllBrands() {
    var brands = brandService.getAllBrands();
    return ResponseEntity.ok(brandService.getAllBrands());
    }
}
