package sit.backend.dtos;


import lombok.Getter;
import lombok.Setter;
import sit.backend.entities.SaleItem;

@Getter
@Setter
public class SaleItemImageDto {
    private String fileName;
    private Integer imageViewOrder;
    private SaleItem saleItem;
}
