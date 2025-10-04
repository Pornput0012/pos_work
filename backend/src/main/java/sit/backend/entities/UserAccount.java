package sit.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.Instant;

@Entity
@Table(name = "user_accounts", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_nickname", columnNames = {"nickname"}),
        @UniqueConstraint(name = "uc_user_email", columnNames = {"email"})
})
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType;

    @JsonIgnore
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @Column(name = "created_on", insertable = false, updatable = false)
    private Instant createdOn;

    @Column(length = 200)
    private String fullname;

    @Column(length = 20)
    private String mobile;

    @Column(name = "bank_account_no", length = 50)
    private String bankAccountNo;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "card_no", length = 50)
    private String cardNo;

    @Column(name = "front_card_photo_path", length = 300)
    private String frontCardPhotoPath;

    @Column(name = "back_card_photo_path", length = 300)
    private String backCardPhotoPath;

    @Column(nullable = false, length = 20)
    private String status = "INACTIVE";   // ค่าเริ่มต้น

    @Column(name = "verification_token", length = 300)
    private String verificationToken;



    // constructors
    public UserAccount() { this.createdOn = Instant.now(); }

    // getters / setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public Instant getCreatedOn() { return createdOn; }
    public void setCreatedOn(Instant createdOn) { this.createdOn = createdOn; }

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

    public String getFrontCardPhotoPath() { return frontCardPhotoPath; }
    public void setFrontCardPhotoPath(String frontCardPhotoPath) { this.frontCardPhotoPath = frontCardPhotoPath; }

    public String getBackCardPhotoPath() { return backCardPhotoPath; }
    public void setBackCardPhotoPath(String backCardPhotoPath) { this.backCardPhotoPath = backCardPhotoPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
}
