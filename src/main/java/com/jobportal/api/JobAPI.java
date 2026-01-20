package com.jobportal.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.ApplicantDTO;
import com.jobportal.dto.Application;
import com.jobportal.dto.CandidateMatchDTO;
import com.jobportal.dto.JobDTO;
import com.jobportal.dto.ProfileDTO;
import com.jobportal.dto.ResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.VerificationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.Profile;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.jwt.JwtHelper;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobService;
import com.jobportal.service.ProfileService;
import com.jobportal.service.UserService;

import jakarta.validation.Valid;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/jobs")
public class JobAPI {
	@Autowired
	private JobService  jobService ;

	 @Autowired
	    private UserService userService;
	 
	 @Autowired
	 private UserRepository userRepository;
	 @Autowired
	 private JobRepository jobRepository;
	 @Autowired
	 private ProfileRepository profileRepository;
	 
	 @Autowired
	 private JwtHelper jwtHelper;
	
	@PostMapping("/post")
	public ResponseEntity<JobDTO>postJob(@RequestBody @Valid JobDTO jobDTO, @RequestHeader("Authorization") String authHeader) throws JobPortalException{
		String token = authHeader.substring(7); // Remove "Bearer "
       Long userId = jwtHelper.getUserIdFromToken(token);
		
		return new ResponseEntity<>(jobService.postJob(jobDTO,userId),HttpStatus.CREATED);
	}
	
	 @PostMapping("/draft")
	    public ResponseEntity<JobDTO> saveDraft(@RequestBody @Valid JobDTO jobDTO) throws JobPortalException {
	        JobDTO savedDraft = jobService.draftJob(jobDTO);
	        return new ResponseEntity<>(savedDraft, HttpStatus.CREATED);
	    }
//	@GetMapping("/post")
//	public ResponseEntity<?> postJob(@RequestBody Job job, @RequestHeader("Authorization") String authHeader) {
//	    try {
//	        String token = authHeader.substring(7); // Remove "Bearer "
//	        Long userId = jwtHelper.getUserIdFromToken(token);
//	        return ResponseEntity.ok(job);
//
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//	    }
//	}

	
	
	
//	
//	@PostMapping("/post")
//	public ResponseEntity<JobDTO> postJob(@RequestBody @Valid JobDTO jobDTO) throws JobPortalException {
//	    User employer = userRepository.findById(jobDTO.getPostedBy())
//	            .orElseThrow(() -> new JobPortalException("EMPLOYER_NOT_FOUND"));
//
//	    if(!employer.isCompanyVerified()) {
//	        throw new JobPortalException("Please complete company verification first.");
//	    }
//
//	    JobDTO postedJob = jobService.postJob(jobDTO);
//	    return new ResponseEntity<>(postedJob, HttpStatus.CREATED);
//	}
//	
//	 @PostMapping("/post")
//	 public ResponseEntity<JobDTO> postJob(@RequestBody @Valid JobDTO jobDTO) throws JobPortalException {
//	     UserDTO employer = userService.getUserByEmail(jobDTO.getPostedByEmail());
//
//	     if (employer.getAccountType() == null || !employer.getAccountType().name().equalsIgnoreCase("EMPLOYER")) {
//	         throw new JobPortalException("ONLY_EMPLOYERS_CAN_POST_JOBS");
//	     }
//
//	     if (!employer.isCompanyVerified()) {
//	         throw new JobPortalException("COMPANY_NOT_VERIFIED: Please verify your company first.");
//	     }
//
//	     JobDTO savedJob = jobService.postJob(jobDTO);
//	     return new ResponseEntity<>(savedJob, HttpStatus.CREATED);
//	 }
	

	@GetMapping("/getAll")
	public ResponseEntity<List<JobDTO>>getAllJobs() throws JobPortalException{
		return new ResponseEntity<>(jobService.getAllJobs(),HttpStatus.OK);
	}


	@GetMapping("/get/{id}")
	public ResponseEntity<JobDTO>getJob(@PathVariable Long id) throws JobPortalException{
		return new ResponseEntity<>(jobService.getJob(id),HttpStatus.OK);
	}
	
	
	@PostMapping("/apply/{id}")
	public ResponseEntity<ResponseDTO>applyJob(@PathVariable Long id,@RequestBody ApplicantDTO applicantDTO) throws JobPortalException{
		jobService.applytJob(id,applicantDTO);
		return new ResponseEntity<>(new ResponseDTO("Applied Successfully"),HttpStatus.OK);
	}

	@GetMapping("/postedBy/{id}")
	public ResponseEntity<List<JobDTO>>getJobsPostedBy(@PathVariable Long id) throws JobPortalException{
		return new ResponseEntity<>(jobService.getJobsPostedBy(id),HttpStatus.OK);	}
		

	@PostMapping("/changeAppStatus")
	public ResponseEntity<ResponseDTO>changeAppStatus(@RequestBody Application application) throws JobPortalException{
		jobService.changeAppStatus(application);
		return new ResponseEntity<>(new ResponseDTO("Application Status Changed Successfully"),HttpStatus.OK);
	}
	
	
	
//	
//	  @GetMapping("/{jobId}/candidates")
//	    public ResponseEntity<List<CandidateMatchDTO>> getCandidatesWithMatch(@PathVariable Long jobId) {
//	        List<CandidateMatchDTO> candidates =jobService.getCandidatesWithMatch(jobId);
//	        return ResponseEntity.ok(candidates);
//	    }

	@GetMapping("/jobs/{jobId}/applicants")
	public ResponseEntity<?> getApplicantsWithMatchScore(@PathVariable Long jobId) {
	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() -> new RuntimeException("Job not found"));

	    List<User> applicants = userRepository.findByAppliedJobsContains(jobId);
	    List<String> requiredSkills = job.getSkillsRequired();

	    List<Map<String, Object>> response = new ArrayList<>();

	    for (User applicant : applicants) {
	        // Get Profile for this applicant
	        Profile profile = profileRepository.findByEmail(applicant.getEmail())
	                .orElseThrow(()-> new RuntimeException("Profile not found for user " + applicant.getEmail()));

	        List<String> userSkills = profile.getSkills();

	        long matchedCount = userSkills.stream()
	                .filter(requiredSkills::contains)
	                .count();

	        double matchPercentage = ((double) matchedCount / requiredSkills.size()) * 100;

	        Map<String, Object> applicantData = new HashMap<>();
	        applicantData.put("applicantId", applicant.getId());
	        applicantData.put("matchPercentage", Math.round(matchPercentage));

	        response.add(applicantData);
	    }

	    return ResponseEntity.ok(response);
	}

	
}
