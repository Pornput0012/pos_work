package sit.integrated.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.integrated.entities.Brand;
import sit.integrated.repositories.BrandRepositoty;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepositoty brandRepositoty;

    public List<Brand> getAllBrands() {
        return brandRepositoty.findAll();
    }
}
