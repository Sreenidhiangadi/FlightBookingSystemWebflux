package com.flightapp.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.repository.UserRepository;

import reactor.core.publisher.Mono;

import com.flightapp.entity.User; 
@Service
public class AuthService {
	@Autowired
private UserRepository userRepository;
private Map<String,String> userSessions=new HashMap<>();
public Mono<User> register(User user) {
    return userRepository.save(user);
}

public Mono<String> login( String email, String password) {
	return userRepository.findByEmail(email)
			.switchIfEmpty(Mono.error(new RuntimeException("user is not found")))
			.flatMap(user->{
				 if (!user.getPassword().equals(password)) {
                     return Mono.error(new RuntimeException("Invalid password"));
                 }
				 String sessionId=UUID.randomUUID().toString();
					userSessions.put(sessionId,email);
					return Mono.just(sessionId);
			});
	
	
}
public Mono<User> getAdmin(String email) {
	return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new RuntimeException("no user found")));
	
}
public Mono<User> getLoggedInUser(String sessionId) {
    String email = userSessions.get(sessionId);
    if (email == null) return Mono.error(new RuntimeException("Invalid session"));
    return userRepository.findByEmail(email);
}
}
