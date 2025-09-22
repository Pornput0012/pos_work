package sit.backend.dtos;


import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;

import java.time.Instant;

@Getter
@Setter

public class BrandDto {
    @NotNull
    private Integer id;

    @NotNull
    private String brandName;

}