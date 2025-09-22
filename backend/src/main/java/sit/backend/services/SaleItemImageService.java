package sit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.backend.entities.SaleItem;
import sit.backend.entities.SaleItemImage;
import sit.backend.repositories.SaleItemImageRepository;
import sit.backend.repositories.SaleItemRepository;

import java.util.List;

@Service
public class SaleItemImageService {

    @Autowired
    private SaleItemImageRepository saleItemImageRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;

    public void updateFileName(Integer imageId, String newFileName) {
        SaleItemImage image = saleItemImageRepository.findById(imageId).orElse(null);
        if (image != null) {
            image.setFileName(newFileName);
            saleItemImageRepository.save(image);
        }
    }

    public SaleItemImage saveInfoImage(SaleItemImage saleItemImage) {
        return saleItemImageRepository.save(saleItemImage);
    }

    public List<SaleItemImage> findAllBySaleItem(Integer saleItemId) {
        return saleItemImageRepository.findAllBySaleItem(saleItemRepository.findById(saleItemId).orElse(null));
    }

    public void deleteImage(Integer imageId) {
         saleItemImageRepository.deleteById(imageId);
    }

    public List<SaleItemImage> findSaleItemImageByName(String saleItemImageName) {
        return saleItemImageRepository.findSaleItemImageByFileName(saleItemImageName);
    }


    public Boolean imageExists(String fileName) {
        return findSaleItemImageByName(fileName).size() > 0;
    }

    public SaleItemImage findBySaleItemAndFileName(SaleItem saleItem, String fileName) {
        return saleItemImageRepository.findBySaleItemAndFileName(saleItem, fileName).orElse(null);
    }

    public boolean existsBySaleItem(SaleItem saleItem) {
        return saleItemImageRepository.existsBySaleItem(saleItem);
    }

    public void updateImageOrder(Integer imageId, Integer newOrder) {
        SaleItemImage image = saleItemImageRepository.findById(imageId).orElse(null);
        if (image != null) {
            image.setImageViewOrder(newOrder);
            saleItemImageRepository.save(image);
        }
    }

    public SaleItemImage saveImage(SaleItemImage image) {
        return saleItemImageRepository.save(image);
    }
}
