package com.jobportal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.ProfileDTO;
import com.jobportal.entity.Profile;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.utility.Utilities;

@Service("profileService")

public class ProfileServiceImpl  implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository ;
	@Autowired
	private UserRepository userRepository;

	
	@Override
	public Long createProfile(String email) throws JobPortalException {
		Profile profile = new Profile();
		profile.setId(Utilities.getNextSequenceId("profile"));
		 User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new JobPortalException("User not found"));
	    profile.setName(user.getName());
		profile.setEmail(email);
		profile.setSkills(new ArrayList<>());
		profile.setExperience(new ArrayList<>());
		profile.setCertifications(new ArrayList<>());
		profileRepository.save(profile);
		return profile.getId();
	}

	@Override
	public ProfileDTO getProfile(Long id) throws JobPortalException {
		System.out.println(profileRepository.findById(id));
//		string profileDetails = profileRepository.findById(id);
//		return { profileDetails ,  true} 
		return  profileRepository.findById(id).orElseThrow(()-> new JobPortalException("PROFILE_NOT_FOUND")).toDTO();
	}

	@Override
	public ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException {
	 profileRepository.findById(profileDTO.getId()).orElseThrow(()-> new JobPortalException("PROFILE_NOT_FOUND"));
		profileRepository.save(profileDTO.toEntity());
		return profileDTO;
		
	}

	@Override
	public List<ProfileDTO> getAllProfiles() {
		return  profileRepository.findAll().stream().map((x)->x.toDTO()).toList();
	}

}
