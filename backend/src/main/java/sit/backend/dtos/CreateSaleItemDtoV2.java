package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class CreateSaleItemDtoV2 {
    private Integer brandId;
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