package com.jobportal.service;

import com.jobportal.dto.LoginDTO;
import com.jobportal.dto.ResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;


public interface UserService {
	public UserDTO registerUser(UserDTO userDTO) throws JobPortalException;

	public UserDTO loginUser( LoginDTO loginDTO) throws JobPortalException;

	public Boolean sendOtp(String email) throws Exception ;

	public Boolean verifyOtp(String email,String otp) throws JobPortalException;

	public ResponseDTO changePassword( LoginDTO loginDTO) throws JobPortalException;
	
	public UserDTO getUserByEmail(String email) throws JobPortalException;

    // ✅ New methods for employer verification
   // void initiateEmployerVerification(String email, String companyName, String companyWebsite) throws Exception;

   // void completeEmployerVerification(String email, String otp) throws JobPortalException;

    // Token-based verification
	// void initiateEmployerVerification(String email, String companyName, String companyWebsite, String companyEmail) throws Exception;
	 //   void completeEmployerVerification(String token) throws JobPortalException;

		public void requestCompanyVerification(Long userId) throws JobPortalException;
		 public String verifyCompanyToken(String token) throws JobPortalException;

		public User updateEmployerProfile(Long userId, UserDTO dto)  throws JobPortalException;
}
