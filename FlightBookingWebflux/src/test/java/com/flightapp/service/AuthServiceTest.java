package com.flightapp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flightapp.entity.User;
import com.flightapp.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthService authService;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		user = new User();
		user.setEmail("sreenidhi@gmail.com");
		user.setPassword("password");
	}

	@Test
	void testRegister_Success() {
		when(userRepository.save(user)).thenReturn(Mono.just(user));

		StepVerifier.create(authService.register(user)).expectNext(user).verifyComplete();
	}

	@Test
	void testLogin_Success() {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));

		StepVerifier.create(authService.login("sreenidhi@gmail.com", "password"))
				.assertNext(sessionId -> assertNotNull(sessionId)).verifyComplete();
	}

	@Test
	void testLogin_UserNotFound() {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

		StepVerifier.create(authService.login("sreenidhi@gmail.com", "password"))
				.expectErrorMatches(t -> t.getMessage().contains("user is not found")).verify();
	}

	@Test
	void testLogin_InvalidPassword() {
		User wrongUser = new User();
		wrongUser.setEmail("sreenidhi@gmail.com");
		wrongUser.setPassword("wrongpass");

		when(userRepository.findByEmail(wrongUser.getEmail())).thenReturn(Mono.just(wrongUser));

		StepVerifier.create(authService.login("sreenidhi@gmail.com", "password"))
				.expectErrorMatches(t -> t.getMessage().contains("Invalid password")).verify();
	}

	@Test
	void testGetAdmin_Success() {
	    when(userRepository.findByEmail("sreenidhi@gmail.com"))
	            .thenReturn(Mono.just(user));

	    StepVerifier.create(authService.getAdmin("sreenidhi@gmail.com"))
	            .expectNext(user)
	            .verifyComplete();
	}

	@Test
	void testGetAdmin_NotFound() {
	    when(userRepository.findByEmail("sreenidhi@gmail.com"))
	            .thenReturn(Mono.empty());

	    StepVerifier.create(authService.getAdmin("sreenidhi@gmail.com"))
	            .expectErrorMatches(t -> t instanceof RuntimeException &&
	                    t.getMessage().contains("no user found"))
	            .verify();
	}

	@Test
	void testGetLoggedInUser_Success() {
		User user = new User();
		user.setEmail("sreenidhi@gmail.com.com");
		user.setPassword("password");

		when(userRepository.findByEmail("sreenidhi@gmail.com.com")).thenReturn(Mono.just(user));

		StepVerifier.create(authService.login("sreenidhi@gmail.com.com", "password")).assertNext(sessionId -> {
			when(userRepository.findByEmail("sreenidhi@gmail.com.com")).thenReturn(Mono.just(user));
			StepVerifier.create(authService.getLoggedInUser(sessionId)).expectNext(user).verifyComplete();
		}).verifyComplete();
	}

	@Test
	void testGetLoggedInUser_InvalidSession() {
		StepVerifier.create(authService.getLoggedInUser("bad-session")).expectErrorMatches(error -> {
			System.out.println("Actual error: " + error.getMessage());
			return error instanceof RuntimeException && error.getMessage().toLowerCase().contains("session");
		}).verify();
	}

}
