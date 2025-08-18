package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaleItemDto {
    private Integer id;
    private String model;
    private String brandName;
    private BigDecimal price;
    private Integer storageGb;
    private Integer ramGb;
    private String color;

}