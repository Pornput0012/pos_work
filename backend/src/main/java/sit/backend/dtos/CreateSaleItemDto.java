package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateSaleItemDto {
    private BrandDto brand;
    private String model;


    private BigDecimal price;

    private String description;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    public String getColor() {
        return color == null || color.trim().isEmpty() ? null : color.trim();
    }
    private Integer quantity;
    public void setQuantity(Integer quantity) { {
        this.quantity = (quantity != null && quantity >= 0) ? quantity : 1 ;
    }
    }
    private Instant createdOn;
    private Instant updatedOn;

}