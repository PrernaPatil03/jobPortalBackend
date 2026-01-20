package com.jobportal.service;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.dto.AccountType;
import com.jobportal.dto.LoginDTO;
import com.jobportal.dto.NotificationDTO;
import com.jobportal.dto.ResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.VerificationStatus;
import com.jobportal.entity.OTP;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.NotificationRepository;
import com.jobportal.repository.OTPRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.utility.Data;
import com.jobportal.utility.Utilities;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service(value="userService")
public class UserServiceImpl implements UserService {
	@Autowired
private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private OTPRepository otpRepository;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	
	
//	@Override
//	public UserDTO registerUser(UserDTO userDTO) throws JobPortalException {
//		Optional<User> optional =userRepository.findByEmail(userDTO.getEmail());
//		if(optional.isPresent()) throw new JobPortalException("USER_FOUND");
//		userDTO.setProfileId(profileService.createProfile(userDTO.getEmail()));
//		userDTO.setId(Utilities.getNextSequence("users"));
//		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//		
//		User user =userDTO.toEntity();
//		user =userRepository.save(user);
//		return user.toDTO();
//		
//	}
	
	@Override
	public UserDTO registerUser(UserDTO userDTO) throws JobPortalException {
	    Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
	    if(optional.isPresent()) throw new JobPortalException("USER_FOUND");

	    userDTO.setProfileId(profileService.createProfile(userDTO.getEmail()));
	    userDTO.setId(Utilities.getNextSequenceId("users"));
	    userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

	    // ---------------- Employer specific setup ----------------
	    if(userDTO.getAccountType() != null 
	            && userDTO.getAccountType().name().equalsIgnoreCase("EMPLOYER")) {

	    	userDTO.setCompanyVerified(false);  // default not verified
	        userDTO.setVerificationStatus(VerificationStatus.NEW); // enum used
	        userDTO.setCompanyName(userDTO.getCompanyName());       
	        userDTO.setCompanyWebsite(userDTO.getCompanyWebsite());
	        userDTO.setVerificationToken(null);
	        userDTO.setTokenExpiryTime(null);
	    }

	    User user = userDTO.toEntity();
	    user = userRepository.save(user);

	    return user.toDTO();
	}

	@Override
	public UserDTO loginUser(LoginDTO loginDTO) throws JobPortalException {
		User user =userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()-> new JobPortalException("USER_NOT_FOUND"));
		if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) throw new JobPortalException("INVALID_CREDENTIALS");
		return user.toDTO();
	}
	
	
	
	@Override
	public Boolean sendOtp(String email) throws Exception {
	User user =userRepository.findByEmail(email).orElseThrow(()-> new JobPortalException("USER_NOT_FOUND"));
	MimeMessage mm=mailSender.createMimeMessage();
	MimeMessageHelper message = new MimeMessageHelper(mm,true);
	message.setTo(email);
	message.setSubject("Your OTP code");
	String genOtp =Utilities.generateOTP();
	OTP otp = new OTP(email,genOtp,LocalDateTime.now());
	otpRepository.save(otp);
	message.setText(Data.getMessageBody(genOtp,user.getName()),true);
	mailSender.send(mm);
		return true;
	}
	@Override
	public Boolean verifyOtp(String email ,String otp) throws JobPortalException {
		OTP otpEntity =otpRepository.findById(email).orElseThrow(()-> new JobPortalException("OTP_NOT_FOUND"));
		if(!otpEntity.getOtpCode().equals(otp)) throw new JobPortalException("OTP_INCORRECT") ;
		return true;
	}
	@Override
	public ResponseDTO changePassword(LoginDTO loginDTO) throws JobPortalException {
		User user =userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()-> new JobPortalException("USER_NOT_FOUND"));
		user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
		userRepository.save(user);
		NotificationDTO  noti =new NotificationDTO();
		noti.setUserId(user.getId());
		noti.setMessage("Password Reset Successfull");
		noti.setAction("Password Reset");
		notificationService.sendNotification(noti);
		return new ResponseDTO("Password Changed Successfully");
	
	}
	
	
	@Scheduled(fixedRate = 60000)
	public void removeExpiredOTPs() {
		LocalDateTime expiry =LocalDateTime.now().minusMinutes(5);
		List <OTP>expiredOTPs =otpRepository.findByCreationTimeBefore(expiry);
		if(!expiredOTPs.isEmpty()) {
			otpRepository.deleteAll(expiredOTPs);
			System.out.println("Removed " +expiredOTPs.size() +" expired OTPs.");
		}
		
	}
	@Override
	public UserDTO getUserByEmail(String email) throws JobPortalException {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email).orElseThrow(()-> new JobPortalException("USER_NOT_FOUND")).toDTO();
		
	}

	
//	@Override
//	public void initiateEmployerVerification(String email, String companyName, String companyWebsite) throws Exception {
//	    User user = userRepository.findByEmail(email)
//	            .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));
//
//	    if(!user.getAccountType().name().equalsIgnoreCase("EMPLOYER")) {
//	        throw new JobPortalException("NOT_AN_EMPLOYER");
//	    }
//
//	    user.setCompanyName(companyName);
//	    user.setCompanyWebsite(companyWebsite);
//	    user.setVerificationStatus(VerificationStatus.PENDING);
//
//	    // Generate unique token
//	    String token = UUID.randomUUID().toString();
//	    user.setVerificationToken(token);
//	    user.setTokenExpiryTime(LocalDateTime.now().plusHours(24));
//
//	    // Send email
//	    String link = "https://jobportal.com/api/users/employer/verify?token=" + token;
//	    MimeMessage mm = mailSender.createMimeMessage();
//	    MimeMessageHelper message = new MimeMessageHelper(mm, true);
//	    message.setTo(user.getEmail());
//	    message.setSubject("Verify your company");
//	    message.setText("Click the button to verify: <a href='" + link + "'>Verify Company</a>", true);
//	    mailSender.send(mm);
//
//	    userRepository.save(user);
//	}
//
//	@Override
//	public void completeEmployerVerification(String token) throws JobPortalException {
//	    User user = userRepository.findByVerificationToken(token)
//	            .orElseThrow(() -> new JobPortalException("INVALID_OR_EXPIRED_TOKEN"));
//
//	    if(user.getTokenExpiryTime().isBefore(LocalDateTime.now())) {
//	        user.setVerificationStatus(VerificationStatus.REJECTED);
//	        user.setVerificationToken(null);
//	        user.setTokenExpiryTime(null);
//	        userRepository.save(user);
//	        throw new JobPortalException("TOKEN_EXPIRED - Company verification rejected.");
//	    }
//
//	    user.setCompanyVerified(true);
//	    user.setVerificationStatus(VerificationStatus.APPROVED);
//	    user.setVerificationToken(null);
//	    user.setTokenExpiryTime(null);
//	    userRepository.save(user);
//	}
//
//	// Scheduler to automatically reject expired tokens
//	@Scheduled(fixedRate = 600000) // every 10 min
//	public void autoRejectExpiredVerifications() {
//	    LocalDateTime now = LocalDateTime.now();
//	    List<User> pendingUsers = userRepository.findByVerificationStatus(VerificationStatus.PENDING);
//
//	    for(User u : pendingUsers) {
//	        if(u.getTokenExpiryTime() != null && u.getTokenExpiryTime().isBefore(now)) {
//	            u.setVerificationStatus(VerificationStatus.REJECTED);
//	            u.setVerificationToken(null);
//	            u.setTokenExpiryTime(null);
//	            userRepository.save(u);
//	        }
//	    }
//	}
	
   
	@Override
	public void  requestCompanyVerification(Long userId) throws JobPortalException {
	    Optional<User> optional = userRepository.findById(userId);
	    if (optional.isEmpty()) throw new JobPortalException("USER_NOT_FOUND");

	    User user = optional.get();

	  //  if (user.isBlocked()) throw new JobPortalException("USER_BLOCKED");
	    if (user.getCompanyEmail() == null || user.getCompanyWebsite() == null) {
	        throw new JobPortalException("COMPANY_DETAILS_NOT_FOUND");
	    }
	    if (user.getCompanyEmail() == null || user.getCompanyWebsite() == null) {
	        throw new JobPortalException("COMPANY_INFO_INCOMPLETE");
	    }

//	    String emailDomain = user.getCompanyEmail().substring(user.getCompanyEmail().indexOf("@") + 1);
//	    if (!user.getCompanyWebsite().contains(emailDomain.split("\\.")[0])) {
//	        throw new JobPortalException("EMAIL_DOMAIN_MISMATCH");
//	    }


	    // Domain check
//	    String emailDomain = user.getCompanyEmail().substring(user.getCompanyEmail().indexOf("@") + 1);
//	    if (!user.getCompanyWebsite().contains(emailDomain.split("\\.")[0])) {
//	        throw new JobPortalException("EMAIL_DOMAIN_MISMATCH");
//	    }

	    // Monthly and total limits
//	    LocalDate now = LocalDate.now();
//	    LocalDate monthStart = now.withDayOfMonth(1);
//
//	    if (user.getMonthlyVerificationCount() >= 2 && user.getTokenExpiryTime() != null &&
//	        user.getTokenExpiryTime().toLocalDate().isAfter(monthStart.minusDays(1))) {
//	        throw new JobPortalException("MONTHLY_LIMIT_REACHED");
//	    }
//
//	    if (user.getTotalVerificationCount() >= 10) {
//	        user.setBlocked(true);
//	        userRepository.save(user);
//	        throw new JobPortalException("TOTAL_LIMIT_REACHED_USER_BLOCKED");
//	    }

	    // Generate token
	    String token = UUID.randomUUID().toString();
	    user.setVerificationToken(token);
	    user.setTokenExpiryTime(LocalDateTime.now().plusDays(2));
	    user.setVerificationStatus(VerificationStatus.PENDING);

	    // Increase counters
//	    user.setMonthlyVerificationCount(user.getMonthlyVerificationCount() + 1);
//	    user.setTotalVerificationCount(user.getTotalVerificationCount() + 1);

	    userRepository.save(user);

	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        helper.setTo(user.getCompanyEmail());
	        helper.setSubject("Verify Employer Account - FindJob");
	        helper.setText(Data.getVerificationEmailBody(
	            user.getCompanyName(),
	            "http://localhost:8080/users/verify?token=" + token
	        ), true);
	        mailSender.send(message);
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        throw new JobPortalException("FAILED_TO_SEND_EMAIL");
	    }
	    NotificationDTO notiDTO =new NotificationDTO();
		notiDTO.setAction("Verification Email sent");
		notiDTO.setMessage("Hii"+ user.getName() +", The verification link will be active for 48 hours.Please get verified by your company to post jobs . ");
		notiDTO.setUserId(user.getId());
		notiDTO.setRoute("/company-verification");
		try {
		notificationService.sendNotification(notiDTO);
		}catch(JobPortalException e) {
			e.printStackTrace();
		}
	  // <-- return token for frontend polling
	}

	@Override
	public String verifyCompanyToken(String token) throws JobPortalException {
	    Optional<User> optional = userRepository.findByVerificationToken(token);
	    if (optional.isEmpty()) throw new JobPortalException("INVALID_TOKEN");

	    User user = optional.get();
	    if (user.getTokenExpiryTime() == null || user.getTokenExpiryTime().isBefore(LocalDateTime.now())) {
	    	NotificationDTO notiDTO =new NotificationDTO();
			notiDTO.setAction("Token Expired");
			notiDTO.setMessage("You didnot get verified by the company.Please try again later.");
			notiDTO.setUserId(user.getId());
			notiDTO.setRoute("/verify/failure");
			try {
			notificationService.sendNotification(notiDTO);
			}catch(JobPortalException e) {
				e.printStackTrace();
			}
	    	throw new JobPortalException("TOKEN_EXPIRED");
	    }

	    user.setCompanyVerified(true);
	    user.setVerificationStatus(VerificationStatus.APPROVED);
	    user.setVerificationToken(null);
	    user.setTokenExpiryTime(null);
	    userRepository.save(user);
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        helper.setTo(user.getCompanyEmail());
	        helper.setSubject("Company Verified - FindJob");
	        helper.setText("Congratulations! Your company " + user.getCompanyName() + " has been verified successfully.", true);
	        mailSender.send(message);

//	        MimeMessage userMsg = mailSender.createMimeMessage();
//	        MimeMessageHelper userHelper = new MimeMessageHelper(userMsg, true);
//	        userHelper.setTo(user.getEmail());
//	        userHelper.setSubject("Your Employer Profile Verified");
//	        userHelper.setText("Your company profile is now verified. You can post jobs freely.", true);
//	        mailSender.send(userMsg);
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        throw new JobPortalException("FAILED_TO_SEND_CONFIRMATION_EMAIL");
	    }
	    NotificationDTO notiDTO =new NotificationDTO();
		notiDTO.setAction("Employer Verified By Company");
		notiDTO.setMessage("Hii"+ user.getName() +", you have been verified by the company .You can now post job on the behalf of the " + user.getCompanyName());
		notiDTO.setUserId(user.getId());
		notiDTO.setRoute("/verify/success");
		try {
		notificationService.sendNotification(notiDTO);
		}catch(JobPortalException e) {
			e.printStackTrace();
		}


	    return "EMPLOYER_VERIFIED_SUCCESSFULLY";
	}

	@Override
	public User updateEmployerProfile(Long userId, UserDTO dto) throws JobPortalException {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));

	    boolean requiresVerification = false;

	    if (!dto.getCompanyName().equals(user.getCompanyName())) {
	        user.setCompanyName(dto.getCompanyName());
	        requiresVerification = true;
	    }
	    if (!dto.getCompanyWebsite().equals(user.getCompanyWebsite())) {
	        user.setCompanyWebsite(dto.getCompanyWebsite());
	        requiresVerification = true;
	    }
	    if (!dto.getCompanyEmail().equals(user.getCompanyEmail())) {
	        user.setCompanyEmail(dto.getCompanyEmail());
	        requiresVerification = true;
	    }
	    boolean companyInfoEmpty =
	            (dto.getCompanyName() == null || dto.getCompanyName().trim().isEmpty()) &&
	            (dto.getCompanyWebsite() == null || dto.getCompanyWebsite().trim().isEmpty()) &&
	            (dto.getCompanyEmail() == null || dto.getCompanyEmail().trim().isEmpty());

	    if (companyInfoEmpty) {
	        user.setVerificationStatus(VerificationStatus.NEW);
	        user.setCompanyVerified(false);
	        user.setVerificationToken(null);
	        user.setTokenExpiryTime(null);
	    } else if (requiresVerification) {
	        // Only reset verification if company details changed
	        user.setCompanyVerified(false);
	        user.setVerificationStatus(VerificationStatus.PENDING);
	        user.setVerificationToken(null);
	        user.setTokenExpiryTime(null);
	    }
	    NotificationDTO notiDTO =new NotificationDTO();
		notiDTO.setAction("Company Details Updated");
		notiDTO.setMessage("Company Details Updated you need re-verification from the respected Company");
		notiDTO.setUserId(user.getId());
		notiDTO.setRoute("/company-verification");
		try {
		notificationService.sendNotification(notiDTO);
		}catch(JobPortalException e) {
			e.printStackTrace();
		}
	    return userRepository.save(user);
	}


	
	
    
}

//}
