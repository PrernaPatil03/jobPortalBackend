package com.jobportal.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.EmailRequest;
import com.jobportal.dto.EmailRequestDTO;
import com.jobportal.dto.LoginDTO;
import com.jobportal.dto.ResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.VerificationStatus;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/users")
public class UserAPI {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	
	
	
	@PostMapping("/register")
	public ResponseEntity<UserDTO>registerUser(@RequestBody @Valid UserDTO userDTO) throws JobPortalException{
		userDTO =userService.registerUser(userDTO);
		return new ResponseEntity<>(userDTO,HttpStatus.CREATED);
	}
	
	@PostMapping("/changePass")
	public ResponseEntity<ResponseDTO>changePassword(@RequestBody @Valid LoginDTO loginDTO) throws JobPortalException{
		return new ResponseEntity<>(userService.changePassword(loginDTO),HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserDTO>loginUser(@RequestBody @Valid LoginDTO loginDTO) throws JobPortalException{
		return new ResponseEntity<>(userService.loginUser(loginDTO),HttpStatus.OK);
	}
	
	@PostMapping("/sendOtp/{email}")
	public ResponseEntity<ResponseDTO>sendOtp(@PathVariable String email) throws Exception{
		userService.sendOtp(email);
		return new ResponseEntity<>(new ResponseDTO("OTP sent successfully."),HttpStatus.OK);
	}
	
	@GetMapping("/verifyOtp/{email}/{otp}")
	public ResponseEntity<ResponseDTO>verifyOtp(@PathVariable @Email(message= "{user.email.invalid}") String email,@PathVariable @Pattern(regexp ="^[0-9]{6}$", message="{otp.invalid}") String otp) throws JobPortalException{
		userService.verifyOtp(email,otp);
		return new ResponseEntity<>(new ResponseDTO("OTP has been verified."),HttpStatus.ACCEPTED);
	}

//	 @PostMapping("/{userId}/request-verification")
//	    public String requestVerification(@PathVariable Long userId,  @RequestBody UserDTO userDTO) throws JobPortalException {
//	        userService.requestCompanyVerification(userId,userDTO);
//	        return "Verification email sent to company email. Please verify within 2 days.";
//	    }

	
//	@PostMapping("/{userId}/request-verification")
//	public String requestVerification(@PathVariable Long userId) throws JobPortalException {
//	    userService.requestCompanyVerification(userId);
//	    return "Verification email sent to company email. Please verify within 2 days.";
//	}
	@PostMapping("/{userId}/request-verification")
	public Map<String, String> requestVerification(@PathVariable Long userId) throws JobPortalException {
	    userService.requestCompanyVerification(userId);
	    Map<String, String> response = new HashMap<>();
	    response.put("message", "Verification email sent successfully!");
	    return response;
	}


	    @GetMapping("/verify")
	    public String verifyToken(@RequestParam String token) throws JobPortalException {
	        return userService.verifyCompanyToken(token);
	        
	    }

	    @PutMapping("/{userId}/update-company")
	    public User updateEmployerCompany(@PathVariable Long userId, @RequestBody UserDTO dto) throws JobPortalException {
	        return userService.updateEmployerProfile(userId, dto);
	    }

	    // 2️⃣ Send verification email using your existing method
//	    @PostMapping("/{userId}/send-verification")
//	    public String sendVerificationEmail(@PathVariable Long userId) throws JobPortalException {
//	        userService.requestCompanyVerification(userId);  // ✅ use your existing method
//	        return "Verification email sent successfully";
//	    }
	    
//	    @PostMapping("/{userId}/send-verification")
//	    public String sendVerificationEmail(
//	            @PathVariable Long userId,
//	            @RequestBody UserDTO userDTO
//	    ) throws JobPortalException {
//	        userService.requestCompanyVerification(userId, userDTO);  // pass DTO to update fields
//	        return "Verification email sent successfully";
//	    }

	    @GetMapping("/{id}/status")
	    public ResponseEntity<Map<String, String>> checkVerificationStatus(@PathVariable Long id) {
	        User user = userRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        VerificationStatus status = user.getVerificationStatus();

	        if (status == null) {
	            // fallback if verificationStatus is missing in DB
	            status = user.isCompanyVerified() ? VerificationStatus.APPROVED : VerificationStatus.PENDING;
	        }

	        // Return as simple JSON
	        return ResponseEntity.ok(Map.of("status", status.name()));
	    }



//
//	    @GetMapping("/users/{id}")
//	    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//	        return userRepository.findById(id)
//	                .map(user -> ResponseEntity.ok(user))              // user found → 200 OK
//	                .orElseGet(() -> ResponseEntity.notFound().build()); // user not found → 404
//	    }
	    
	    
	    @GetMapping("/{id}")
	    public ResponseEntity<Map<String, String>> getUserById(@PathVariable Long id) {
	        return userRepository.findById(id)
	                .map(user -> {
	                    Map<String, String> companyData = new HashMap<>();
	                    companyData.put("companyName", user.getCompanyName());
	                    companyData.put("companyWebsite", user.getCompanyWebsite());
	                    companyData.put("companyEmail", user.getCompanyEmail());
	                    return ResponseEntity.ok(companyData);
	                })
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }


	    
	    
//	    @GetMapping("/users/{id}")
//	    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
//	        return userRepository.findById(id)
//	                .map(user -> {
//	                    UserDTO dto = new UserDTO(
//	                            user.getId(),
//	                            user.getCompanyName(),
//	                            user.getCompanyWebsite(),
//	                            user.getCompanyEmail()
//	                    );
//	                    return ResponseEntity.ok(dto);
//	                })
//	                .orElseGet(() -> ResponseEntity.notFound().build());
//	    }
}
