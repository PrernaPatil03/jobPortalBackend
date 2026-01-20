package com.jobportal.dto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.jobportal.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
	private  Long id;
	@NotBlank(message="{user.name.absent}")
	private String name;
	@NotBlank(message="{user.email.absent}")
	@Email(message="{user.email.invalid}")
	private String email;
	@NotBlank(message="{user.password.absent}")
	@Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}:;<>,.?~])[A-Za-z\\d!@#$%^&*()_+{}:;<>,.?~]{8,15}$",message="{user.password.invalid}")
	private String password;
	private AccountType accountType;
	private Long profileId;
	
	private boolean companyVerified;
	
	private String companyName;
	private String companyWebsite;
	private String companyEmail;
    private VerificationStatus verificationStatus;
    private String verificationToken;
    private LocalDateTime tokenExpiryTime;
    private List<String> appliedJobs; 

	
	public User toEntity() {
	return new User(this.id,this.name,this.email,this.password,this.accountType,this.profileId,this.companyVerified,this.companyName,
	this.companyWebsite,this.companyEmail,this.verificationStatus,this.verificationToken,
	this.tokenExpiryTime,this.appliedJobs);
	}


}
