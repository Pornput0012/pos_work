package sit.backend.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.backend.dtos.CreateSaleItemDto;
import sit.backend.dtos.SaleItemDetailDto;
import sit.backend.dtos.SaleItemDto;
import sit.backend.dtos.SaleItemResponseDto;
import sit.backend.services.SaleItemService;

import java.util.List;

@RestController
@RequestMapping("/v1/sale-items")
public class SaleItemController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SaleItemService saleItemService;

    @GetMapping("")
    public ResponseEntity<List<SaleItemDto>> getSaleItems(@RequestParam(required = false) List<Integer> brandIds) {
        var saleitems = (brandIds == null || brandIds.isEmpty())
                ? saleItemService.getAllSaleItems()
                : saleItemService.getSaleItemsByBrandIds(brandIds);

        if (saleitems.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        var saleItemDtos = saleitems.stream()
                .map(item -> modelMapper.map(item, SaleItemDto.class))
                .toList();
        return ResponseEntity.ok(saleItemDtos);
    }

    @GetMapping("{id}")
    public ResponseEntity<SaleItemDetailDto> getSaleItemDto(@PathVariable Integer id) {
        var saleitem = saleItemService.getSaleItemById(id);
        var saleItemDetailDto = modelMapper.map(saleitem, SaleItemDetailDto.class);
        return ResponseEntity.ok(saleItemDetailDto);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SaleItemResponseDto> createSaleItem(@RequestBody CreateSaleItemDto dto) {
        var saleItem = saleItemService.createSaleItem(dto);
        var saleItemResponseDto = modelMapper.map(saleItem, SaleItemResponseDto.class);

        return new ResponseEntity<>(saleItemResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<SaleItemResponseDto> updateSaleItem(@PathVariable Integer id, @RequestBody CreateSaleItemDto dto) {
        var updatedItem = saleItemService.updateSaleItem(id, dto);
        var responseDto = modelMapper.map(updatedItem, SaleItemResponseDto.class);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        saleItemService.deleteSaleItem(id);
        return ResponseEntity.noContent().build();
    }

}
