package com.emt.dms1.Controller;

import com.emt.dms1.Models.AuthenticationRequest;
import com.emt.dms1.Services.AdminService;
import com.emt.dms1.Services.RegisterRequest;
import com.emt.dms1.utils.EntityResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/auth")
public class AuthController {

    private  AdminService adminService;

    public AuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public EntityResponse register(@RequestBody RegisterRequest request) {
        return adminService.register(request);
    }

    @PostMapping("/authenticate")
    public EntityResponse authenticate(@RequestBody AuthenticationRequest request) {
        return adminService.authenticate(request);
    }
    @GetMapping("/getallusers")
    public  EntityResponse getallusers(){return adminService.getAllUsers();
    }
    @GetMapping("/getbyname")
   public EntityResponse getbyusername(@RequestParam String username){
        return adminService.findUserByUsername(username);
    }
    @PutMapping("/updateuserrole")
    public EntityResponse updateUserRoleToAdmin(String email){
        return adminService.updateUserRoleToAdmin(email);
    }


@PostMapping("/change-password")
public ResponseEntity<EntityResponse<String>> changePassword(
        @RequestParam("oldPassword") String oldPassword,
        @RequestParam("newPassword") String newPassword) {

    EntityResponse<String> response = adminService.changePassword(oldPassword, newPassword);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
}

// Endpoint to request a password reset (when the user forgets the password)
@PostMapping("/forgot-password")
public ResponseEntity<EntityResponse<String>> requestPasswordReset(@RequestParam("email") String email) {
    EntityResponse<String> response = adminService.requestPasswordReset(email);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
}

// Endpoint to reset password using the token sent to the user's email
@PostMapping("/reset-password")
public ResponseEntity<EntityResponse<String>> resetPassword(
        @RequestParam("token") String token,
        @RequestParam("newPassword") String newPassword) {

    EntityResponse<String> response = adminService.resetPassword(token, newPassword);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
}

// Endpoint to log the user out
@PostMapping("/logout")
public ResponseEntity<EntityResponse<String>> logout(HttpServletRequest request, HttpServletResponse response) {
    EntityResponse<String> logoutResponse = adminService.logout(request, response);
    return new ResponseEntity<>(logoutResponse, HttpStatus.valueOf(logoutResponse.getStatusCode()));
}
}