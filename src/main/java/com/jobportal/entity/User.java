package com.jobportal.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.jobportal.dto.AccountType;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.VerificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="users")
public class User {
	@Id
private  Long id;
private String name;
  
@Indexed(unique=true)
private String email;
private String password;
private AccountType accountType;
private Long profileId; 

private boolean companyVerified = false;   // default false

private String companyName;
private String companyWebsite;
private String companyEmail;
@Field(targetType = FieldType.STRING)
private VerificationStatus verificationStatus = VerificationStatus.PENDING;// PENDING / APPROVED / REJECTED

private String verificationToken;       // UUID token for email verification
private LocalDateTime tokenExpiryTime; 
private List<String> appliedJobs;  // stores job IDs user applied to

public UserDTO toDTO() {
	return new UserDTO(this.id,this.name,this.email,this.password,this.accountType,this.profileId,this.companyVerified,this.companyName,
			this.companyWebsite,this.companyEmail,this.verificationStatus,this.verificationToken,
			this.tokenExpiryTime,this.appliedJobs);
}
}