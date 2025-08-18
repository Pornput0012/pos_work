package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class SaleItemResponseDto {
    private Integer id;
    private String brandName;
    private String model;
    private BigDecimal price;
    private String description;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;

    private Instant createdOn;
    private Instant updatedOn;

}
