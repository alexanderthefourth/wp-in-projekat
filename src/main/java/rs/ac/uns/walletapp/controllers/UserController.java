package rs.ac.uns.walletapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.UserDTO;
import rs.ac.uns.walletapp.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userCount")
    public int getUserCount() {
        return userService.getUserCount();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestParam int userId) {
        UserDTO dto = userService.getUserProfile(userId);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestParam int userId, @RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUserProfile(userId, userDTO);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }
}
