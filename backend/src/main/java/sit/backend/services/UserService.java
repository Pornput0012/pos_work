package sit.backend.services;

import com.nimbusds.jose.JOSEException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.backend.dtos.*;
import sit.backend.entities.UserAccount;
import sit.backend.repositories.UserAccountRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtService jwtService;

    public boolean nicknameExists(String nickname) {
        return userAccountRepository.existsByNickname(nickname);
    }

    public boolean emailExists(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    @Transactional
    public UserResponseDto createUser(CreateUserDto dto) throws IOException {
        // basic validations
        if (dto.getAccountType() == null || dto.getAccountType().isBlank()) {
            throw new IllegalArgumentException("accountType is required");
        }
        if (dto.getNickname() == null || dto.getNickname().isBlank()) {
            throw new IllegalArgumentException("nickname is required");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("password and confirmPassword do not match");
        }
        if (nicknameExists(dto.getNickname())) {
            throw new IllegalArgumentException("nickname already exists");
        }
        if (emailExists(dto.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        if (dto.getFullname() == null || dto.getFullname().isBlank()) {
            throw new IllegalArgumentException("fullname is required");
        }

        // Seller-specific validations
        if ("Seller".equalsIgnoreCase(dto.getAccountType())) {
            if (dto.getMobile() == null || dto.getMobile().isBlank()) {
                throw new IllegalArgumentException("mobile is required");
            }
            if (dto.getBankAccountNo() == null || dto.getBankAccountNo().isBlank()) {
                throw new IllegalArgumentException("bankAccountNo is required");
            }
            if (dto.getBankName() == null || dto.getBankName().isBlank()) {
                throw new IllegalArgumentException("bankName is required");
            }
            if (dto.getCardNo() == null || dto.getCardNo().isBlank()) {
                throw new IllegalArgumentException("cardNo is required");
            }
            if (dto.getFrontCardPhoto() == null || dto.getBackCardPhoto() == null) {
                throw new IllegalArgumentException("Card photos are required");
            }
        }

        UserAccount user = new UserAccount();
        user.setNickname(dto.getNickname().trim());
        user.setFullname(dto.getFullname().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAccountType(dto.getAccountType().trim());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
//        user.setCreatedOn(Instant.now());
        user.setStatus("INACTIVE");

        // สร้าง token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        // Map Seller-specific fields
        if ("Seller".equalsIgnoreCase(dto.getAccountType())) {
            user.setMobile(dto.getMobile());
            user.setBankAccountNo(dto.getBankAccountNo());
            user.setBankName(dto.getBankName());
            user.setCardNo(dto.getCardNo());

            if (dto.getFrontCardPhoto() != null && !dto.getFrontCardPhoto().isEmpty()) {
                user.setFrontCardPhotoPath(saveFile(dto.getFrontCardPhoto()));
            }
            if (dto.getBackCardPhoto() != null && !dto.getBackCardPhoto().isEmpty()) {
                user.setBackCardPhotoPath(saveFile(dto.getBackCardPhoto()));
            }
        }

        UserAccount saved = userAccountRepository.save(user);

        // Mock ส่งอีเมล
        String link = "http://localhost:5173/verify-email/?token=" + token;
        System.out.println("📧 Verification email sent to: " + saved.getEmail());
        System.out.println("🔗 Click link: " + link);

        UserResponseDto res = new UserResponseDto();
        res.setId(saved.getId());
        res.setNickname(saved.getNickname());
        res.setEmail(saved.getEmail());
        res.setAccountType(saved.getAccountType());
        res.setCreatedOn(saved.getCreatedOn());
        res.setFullname(saved.getFullname());
        return res;
    }

    @Transactional
    public boolean verifyEmail(String token) {
        Optional<UserAccount> userOpt = userAccountRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) return false;

        UserAccount user = userOpt.get();
        user.setStatus("ACTIVE");
        user.setVerificationToken(null); // ใช้แล้วลบทิ้ง
        userAccountRepository.save(user);
        return true;
    }

    // Method to save uploaded file to server and return the path
    private String saveFile(MultipartFile file) throws IOException {
        String folder = "uploads/cards/"; // folder path on server
        Files.createDirectories(Paths.get(folder)); // create folder if not exists
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(folder + filename);
        Files.write(path, file.getBytes());
        return path.toString(); // save path in DB
    }

    public UserProfileResponseDTO getUserProfile(String token) throws ParseException, JOSEException {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or User id");
        }
        String tokenRaw = token.substring(7);

        jwtService.validateAccessToken(tokenRaw);

        String email = jwtService.extractUserClaims(tokenRaw).getEmail();
        UserAccount user = userAccountRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or User id");
        }
        if (user.getStatus().equalsIgnoreCase("inactive")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You need activate your account before signing in.");
        }
        return modelMapper.map(user, UserProfileResponseDTO.class);
    }

    public UserProfileResponseDTO getUserProfileById(Integer id, String token) throws ParseException, JOSEException {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or User id");
        }
        String tokenRaw = token.substring(7);
        jwtService.validateAccessToken(tokenRaw);

        JwtClaimDTO claims = jwtService.extractUserClaims(tokenRaw);
        if (id == null || !id.equals(claims.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        UserAccount user = userAccountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getStatus().equalsIgnoreCase("inactive")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not active");
        }

        return modelMapper.map(user, UserProfileResponseDTO.class);
    }

    public UserProfileResponseDTO updateUserProfile(Integer id, UpdateUserRequestDto updateUser, String token) throws ParseException, JOSEException {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or User id");
        }
        String tokenRaw = token.substring(7);
        jwtService.validateAccessToken(tokenRaw);
        JwtClaimDTO claims = jwtService.extractUserClaims(tokenRaw);
        if (id == null || !id.equals(claims.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        UserAccount user = userAccountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        user.setFullname(updateUser.getFullName());
        user.setNickname(updateUser.getNickName());

        return modelMapper.map(userAccountRepository.save(user), UserProfileResponseDTO.class);
    }

}
