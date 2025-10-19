package rs.ac.uns.walletapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.LoginUserDTO;
import rs.ac.uns.walletapp.dto.RegisterUserDTO;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO dto) {
        try{
            return ResponseEntity.ok(userService.registerUser(dto));
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDTO dto) {
        boolean success = userService.login(dto);
        if (!success) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
            return ResponseEntity.ok(userService.getAuthByUsername(dto.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Uspešno ste se odjavili.");
    }

}
