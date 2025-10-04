package sit.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.backend.dtos.CreateUserDto;
import sit.backend.dtos.UserResponseDto;
import sit.backend.services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/v1/registers")
public class UserRegisterController {

    @Autowired
    private UserService userService;

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<?> registerUser(@ModelAttribute CreateUserDto createUserDto) {
        try {
            UserResponseDto created = userService.createUser(createUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected server error: " + ex.getMessage()));
        }
    }

}
