package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BrandDetailDto {
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;
}

