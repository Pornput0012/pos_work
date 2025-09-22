package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateBrandDto {
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }

    private String countryOfOrigin;
}

