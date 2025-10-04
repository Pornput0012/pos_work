package sit.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserProfileResponseDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String fullName;
    private String userType;
    private String mobile;
    private String bankAccount;
    private String bankName;
    private String cardNo;
    private String frontCardPhotoPath;
    private String backCardPhotoPath;
}