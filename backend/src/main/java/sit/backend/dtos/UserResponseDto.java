package sit.backend.dtos;

import java.time.Instant;

public class UserResponseDto {
    private Integer id;
    private String nickname;
    private String email;
    private String accountType;
    private String fullname;
    private Instant createdOn;

    // getters / setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String username) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public Instant getCreatedOn() { return createdOn; }
    public void setCreatedOn(Instant createdOn) { this.createdOn = createdOn; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

}
