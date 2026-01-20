package com.jobportal.service;

import java.util.List;

import com.jobportal.dto.ApplicantDTO;
import com.jobportal.dto.Application;
import com.jobportal.dto.CandidateMatchDTO;
import com.jobportal.dto.JobDTO;
import com.jobportal.exception.JobPortalException;

public interface JobService {

	public JobDTO postJob(JobDTO jobDTO,Long userId) throws JobPortalException;
	   public JobDTO draftJob(JobDTO jobDTO) throws JobPortalException;

	public List<JobDTO> getAllJobs();

	public JobDTO getJob(Long id) throws JobPortalException;

	public void applytJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException;

	public List<JobDTO> getJobsPostedBy(Long id);

	public void changeAppStatus(Application application) throws JobPortalException;
	
	// public List<CandidateMatchDTO> getCandidatesWithMatch(Long jobId);

	//public double calculateSkillMatchPercentage(List<String> jobSkills, List<String> userSkills) ;

}
