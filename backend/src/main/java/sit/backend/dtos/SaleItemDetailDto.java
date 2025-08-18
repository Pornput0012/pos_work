package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class SaleItemDetailDto {
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
    private Instant createdOn;
    private Instant updatedOn;

}