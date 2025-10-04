package sit.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String nickName;
    private String fullName;
}