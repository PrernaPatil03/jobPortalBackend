package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.entity.Profile;

public interface ProfileRepository  extends MongoRepository<Profile,Long>{
	
  // public  Profile findByEmail(String email);
   Optional<Profile> findByEmail(String email);

}
