package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface JobRepository extends MongoRepository<Job,Long> {
	public List<Job> findByPostedBy(Long postedBy);

//	Optional<User> findByEmail(String email);

	
	//Optional<List<Job>> findByPostedBy(String userId);

}
