package sit.integrated.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.integrated.dtos.SaleItemDto;
import sit.integrated.entities.SaleItem;
import sit.integrated.services.SaleItemService;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/itb-mshop/v1/sale-items")
public class SaleItemController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SaleItemService saleItemService;

    @GetMapping("")
    public ResponseEntity<List<SaleItem>> getSaleItems() {
        var saleitems = saleItemService.getAllSaleItems();
        return ResponseEntity.ok(saleitems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemDto> getSaleItemDto(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        if (saleItem != null) { // เพิ่มการตรวจสอบว่าพบข้อมูลหรือไม่
            SaleItemDto saleItemDto = modelMapper.map(saleItem, SaleItemDto.class);
            return ResponseEntity.ok(saleItemDto);
        } else {
            return ResponseEntity.notFound().build(); // ส่ง 404 Not Found หากไม่พบข้อมูล
        }
    }
}