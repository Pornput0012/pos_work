package sit.integrated.dtos;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaleItemDto {
    private Integer id;
    private String model;
    private String brandName;
    private String description;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    private BigDecimal price;
}
