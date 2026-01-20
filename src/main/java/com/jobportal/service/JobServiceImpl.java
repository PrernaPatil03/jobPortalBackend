package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.ApplicantDTO;
import com.jobportal.dto.Application;
import com.jobportal.dto.ApplicationStatus;
import com.jobportal.dto.CandidateMatchDTO;
import com.jobportal.dto.JobDTO;
import com.jobportal.dto.JobStatus;
import com.jobportal.dto.NotificationDTO;
import com.jobportal.dto.VerificationStatus;
import com.jobportal.entity.Applicant;
import com.jobportal.entity.Job;
import com.jobportal.entity.Profile;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.utility.Utilities;

import lombok.RequiredArgsConstructor;

@Service("jobService")
@RequiredArgsConstructor
public class JobServiceImpl  implements JobService{
	
	@Autowired
	private NotificationService  notificationService;
//	@Autowired
//	private UserRepository userRepository;
	

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
	
//	@Override
//	public JobDTO postJob(JobDTO jobDTO) throws JobPortalException {
//	    System.out.println("postJob called with ID: " + jobDTO.getPostedBy());
//	    System.out.println("Job Title: " + jobDTO.getJobTitle());
//
//	    if (jobDTO.getId() == null || jobDTO.getId() == 0) {
//	        jobDTO.setId(Utilities.getNextSequenceId("jobs"));
//	        jobDTO.setPostTime(LocalDateTime.now());
//
//	        NotificationDTO notiDTO = new NotificationDTO();
//	        notiDTO.setAction("Job Posted Successfully");
//	        notiDTO.setMessage("Job Posted Successfully for job: " + jobDTO.getJobTitle() + " at " + jobDTO.getCompany());
//	        notiDTO.setUserId(jobDTO.getPostedBy());
//	        notiDTO.setRoute("/posted-job/" + jobDTO.getId());
//
//	        try {
//	            notificationService.sendNotification(notiDTO);
//	        } catch (JobPortalException e) {
//	            e.printStackTrace();
//	        }
//	    } else {
//	        Job job = jobRepository.findById(jobDTO.getId())
//	                .orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));
//
//	        if (job.getJobStatus().equals(JobStatus.DRAFT) || jobDTO.getJobStatus().equals(JobStatus.CLOSED)) {
//	            jobDTO.setPostTime(LocalDateTime.now());
//	        }
//	    }
//
//	    Job savedJob = jobRepository.save(jobDTO.toEntity());
//	    System.out.println("Job saved successfully with ID: " + savedJob.getId());
//
//	    return savedJob.toDTO();
//	}
//
// 
    
    @Override
    public JobDTO postJob(JobDTO jobDTO,Long userId) throws JobPortalException {
        System.out.println("postJob called with ID: " + jobDTO.getPostedBy());
        System.out.println("Job Title: " + jobDTO.getJobTitle());
        if (userId == null) {
            throw new JobPortalException("USER_ID_REQUIRED");
        }

        // 2️⃣ Fetch user details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));

        // 🔍 Debug log
        System.out.println("User verification status: " + user.getVerificationStatus());

        // 3️⃣ Check verification status safely
        VerificationStatus status = user.getVerificationStatus();
        if (status == null || !VerificationStatus.APPROVED.name().equalsIgnoreCase(status.name())) {
            throw new JobPortalException("USER_NOT_VERIFIED");
        	//System.out.println("status:" + status );
            
        }

        // 4️⃣ Continue posting job only if verified
        if (jobDTO.getId() == null || jobDTO.getId() == 0) {
            jobDTO.setId(Utilities.getNextSequenceId("jobs"));
            jobDTO.setPostTime(LocalDateTime.now());

            NotificationDTO notiDTO = new NotificationDTO();
            notiDTO.setAction("Job Posted Successfully");
            notiDTO.setMessage("Job Posted Successfully for job: "
                    + jobDTO.getJobTitle() + " at " + jobDTO.getCompany());
            notiDTO.setUserId(jobDTO.getPostedBy());
            notiDTO.setRoute("/posted-job/" + jobDTO.getId());

            try {
                notificationService.sendNotification(notiDTO);
            } catch (JobPortalException e) {
                e.printStackTrace();
            }
        } else {
            Job job = jobRepository.findById(jobDTO.getId())
                    .orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));

            if (job.getJobStatus().equals(JobStatus.DRAFT)
                    || jobDTO.getJobStatus().equals(JobStatus.CLOSED)) {
                jobDTO.setPostTime(LocalDateTime.now());
            }
        }

        // 5️⃣ Save job
        Job savedJob = jobRepository.save(jobDTO.toEntity());
        System.out.println("Job saved successfully with ID: " + savedJob.getId());

        return savedJob.toDTO();
    }

    @Override
    public JobDTO draftJob(JobDTO jobDTO) throws JobPortalException {
        System.out.println("draftJob called with ID: " + jobDTO.getPostedBy());
        System.out.println("Job Title: " + jobDTO.getJobTitle());

        // Ensure the job status is set to DRAFT
        jobDTO.setJobStatus(JobStatus.DRAFT);

        if (jobDTO.getId() == null || jobDTO.getId() == 0) {
            // New draft
            jobDTO.setId(Utilities.getNextSequenceId("jobs"));
            jobDTO.setPostTime(LocalDateTime.now());

            // Notification (optional for draft)
            NotificationDTO notiDTO = new NotificationDTO();
            notiDTO.setAction("Job Drafted Successfully");
            notiDTO.setMessage("Job Drafted Successfully: " + jobDTO.getJobTitle() + " at " + jobDTO.getCompany());
            notiDTO.setUserId(jobDTO.getPostedBy());
            notiDTO.setRoute("/posted-job/" + jobDTO.getId());

            try {
                notificationService.sendNotification(notiDTO);
            } catch (JobPortalException e) {
                e.printStackTrace();
            }
        } else {
            // Update existing draft
            Job job = jobRepository.findById(jobDTO.getId())
                    .orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));

            // Update post time if draft
            if (job.getJobStatus().equals(JobStatus.DRAFT) || jobDTO.getJobStatus().equals(JobStatus.DRAFT)) {
                jobDTO.setPostTime(LocalDateTime.now());
            }
        }

        // Save draft in DB
        Job savedJob = jobRepository.save(jobDTO.toEntity());
        System.out.println("Draft saved successfully with ID: " + savedJob.getId());

        return savedJob.toDTO();
    }

	@Override
	public List<JobDTO> getAllJobs() {
		return jobRepository.findAll().stream().map((x)->x.toDTO()).toList();
	}

	@Override
	public JobDTO getJob(Long id) throws JobPortalException {
		return  jobRepository.findById(id).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND")).toDTO();
	}

	@Override
	public void applytJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException {
		Job job =  jobRepository.findById(id).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND"));
		List<Applicant>applicants=job.getApplicants();
		if(applicants==null)applicants =new ArrayList<>();
		if(applicants.stream().filter((x)->x.getApplicantId()==applicantDTO.getApplicantId()).toList().size()>0)throw new JobPortalException("JOB_APPLIED_ALREADY");
		applicantDTO.setApplicationStatus(ApplicationStatus.APPLIED);
		applicants.add(applicantDTO.toEntity());
		job.setApplicants(applicants);
		jobRepository.save(job);
		
		
	}

	@Override
	public List<JobDTO> getJobsPostedBy(Long id) {
		return jobRepository.findByPostedBy(id).stream().map((x)->x.toDTO()).toList();
	}

	@Override
	public void changeAppStatus(Application application) throws JobPortalException {
		Job job =  jobRepository.findById(application.getId()).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND"));
		List<Applicant>applicants=job.getApplicants().stream().map((x)->{
			if(application.getApplicantId()==x.getApplicantId()) {
				x.setApplicationStatus(application.getApplicationStatus());
				if(application.getApplicationStatus().equals(ApplicationStatus.INTERVIEWING)) {
					x.setInterviewTime(application.getInterviewTime());
					NotificationDTO notiDTO =new NotificationDTO();
					notiDTO.setAction("Interview Scheduled");
					notiDTO.setMessage("Interview Scheduled for job id :" +application.getId());
					notiDTO.setUserId(application.getApplicantId());
					notiDTO.setRoute("/job-history");
					try {
					notificationService.sendNotification(notiDTO);
					} catch(JobPortalException e) {
						e.printStackTrace();
					}
				}
			}
			return x;
		}).toList();
		job.setApplicants(applicants);
		jobRepository.save(job);
		
		
		
	}
	
	 // ✅ Skill match logic
    public double calculateSkillMatchPercentage(List<String> jobSkills, List<String> userSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) return 0;
        if (userSkills == null || userSkills.isEmpty()) return 0;

        long matchedCount = jobSkills.stream()
            .filter(skill -> userSkills.stream()
                .anyMatch(userSkill -> userSkill.equalsIgnoreCase(skill)))
            .count();

        return (matchedCount * 100.0) / jobSkills.size();
    }

    // ✅ Business logic to fetch candidates with skill match %
//    public List<CandidateMatchDTO> getCandidatesWithMatch(Long jobId) {
//        Job job = jobRepository.findById(jobId).orElseThrow();
//        List<User> applicants = userRepository.findByAppliedJobsContains(jobId);
//
//        return applicants.stream().map(user -> {
//            Profile profile = profileRepository.findByEmail(user.getEmail());
//            double match = 0;
//            if (profile != null) {
//                match = calculateSkillMatchPercentage(job.getSkillsRequired(), profile.getSkills());
//            }
//            return new CandidateMatchDTO(user.getName(), user.getEmail(), match);
//        }).collect(Collectors.toList());
//    }
//}

}