package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.dto.VerificationStatus;
import com.jobportal.entity.User;

public interface UserRepository extends MongoRepository<User,Long>{
	
public  Optional <User> findByEmail(String email);
public Optional<User> findByVerificationToken(String token);
public List<User> findByVerificationStatus(VerificationStatus status);

 public List<User> findByAppliedJobsContains(Long jobId);


}
