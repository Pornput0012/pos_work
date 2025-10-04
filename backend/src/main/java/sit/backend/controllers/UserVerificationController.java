package sit.backend.controllers;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.backend.dtos.UpdateUserRequestDto;
import sit.backend.dtos.UserProfileResponseDTO;
import sit.backend.services.FileService;
import sit.backend.services.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/v2/users")
public class UserVerificationController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;


    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        try {
            boolean success = userService.verifyEmail(token);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Account activated"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        boolean success = userService.verifyEmail(token);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
        }
    }

    @GetMapping()
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.getUserProfile(authorizationHeader));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById(@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.getUserProfileById(id, authorizationHeader));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfileById(@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody UpdateUserRequestDto updateUserRequest) throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.updateUserProfile(id, updateUserRequest, authorizationHeader));
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileService.loadFileAsResourceCard(fileName);

        String contentType;
        try {
            Path filePath = resource.getFile().toPath();
            contentType = Files.probeContentType(filePath); // ตรวจ MIME type
        } catch (Exception e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"") // inline = แสดงใน browser
                .body(resource);
    }


}

