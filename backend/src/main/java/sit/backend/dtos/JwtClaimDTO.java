package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtClaimDTO {
    private String email;
    private String role;
    private Integer userId;
}
