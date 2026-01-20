package com.jobportal.jwt;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jobportal.dto.UserDTO;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.UserService;


@Service
public class myUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		try {
			UserDTO dto =userService.getUserByEmail(email);
			return new CustomUserDetails(dto.getId(),email,dto.getName(),dto.getPassword(),dto.getProfileId(),dto.getAccountType(),new ArrayList<>());
		}  catch (JobPortalException e) {
        throw new UsernameNotFoundException("User not registered with email: " + email);
	    }
		//return null;
	}
//	@Override
//	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//	    try {
//	        UserDTO dto = userService.getUserByEmail(email);
//	        return new CustomUserDetails(
//	            dto.getId(),
//	            email,
//	            dto.getName(),
//	            dto.getPassword(),
//	            dto.getProfileId(),
//	            dto.getAccountType(),
//	            new ArrayList<>()
//	        );
//	    } catch (JobPortalException e) {
//	        throw new UsernameNotFoundException("User not registered with email: " + email);
//	    }
//	}


}
