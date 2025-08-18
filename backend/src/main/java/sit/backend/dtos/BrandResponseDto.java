package sit.backend.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandResponseDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;
    private Integer noOfSaleItems;
}
