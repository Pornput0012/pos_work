package sit.backend.controllers;

import org.apache.coyote.BadRequestException;
import org.hibernate.annotations.Parameter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.backend.dtos.*;
import sit.backend.services.SaleItemService;


import java.util.List;

@RestController
@RequestMapping("/v2/sale-items")
public class SaleItemV2Controller {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SaleItemService saleItemService;

    @GetMapping("")
    public ResponseEntity<PageDto<SaleItemDto>> getAllSaleItemsPage(
            @RequestParam(required = true) Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(defaultValue = "[]") List<String> filterBrands,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) List<Integer> filterStorages,
            @RequestParam(required = false) Integer filterPriceLower,
            @RequestParam(required = false) Integer filterPriceUpper) throws MissingServletRequestParameterException {
        
        if (page == null || size == null) {
            throw new MissingServletRequestParameterException(page == null ? "page" : "size", "Integer");
        }
        PageDto<SaleItemDto> pagedResult = saleItemService.getAllSaleItemsPage(page, size, filterBrands, sortField,
                sortDirection, filterStorages, filterPriceLower, filterPriceUpper);
        return ResponseEntity.ok(pagedResult);
    }

    @PostMapping("")
    public ResponseEntity<SaleItemResponseDtoV2> createProduct(
            @ModelAttribute CreateSaleItemDtoV2 product,
            @RequestParam(required = false) List<MultipartFile> images) throws BadRequestException {

        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.createSaleItem(product, images));
    }


}
