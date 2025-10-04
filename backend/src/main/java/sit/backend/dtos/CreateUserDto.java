package sit.backend.dtos;

import org.springframework.web.multipart.MultipartFile;

public class CreateUserDto {
    private String accountType;
    private String nickname;;
    private String email;
    private String password;
    private String confirmPassword;

    // สำหรับ Seller
    private String fullname;
    private String mobile;
    private String bankAccountNo;
    private String bankName;
    private String cardNo;
    private MultipartFile frontCardPhoto;
    private MultipartFile backCardPhoto;

    // getters / setters
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getBankAccountNo() { return bankAccountNo; }
    public void setBankAccountNo(String bankAccountNo) { this.bankAccountNo = bankAccountNo; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }

    public MultipartFile getFrontCardPhoto() { return frontCardPhoto; }
    public void setFrontCardPhoto(MultipartFile frontCardPhoto) { this.frontCardPhoto = frontCardPhoto; }

    public MultipartFile getBackCardPhoto() { return backCardPhoto; }
    public void setBackCardPhoto(MultipartFile backCardPhoto) { this.backCardPhoto = backCardPhoto; }
}
