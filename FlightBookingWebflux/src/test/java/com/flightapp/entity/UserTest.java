package com.flightapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UserTest {

	private static Validator validator;

	@BeforeAll
	static void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void idShouldBeNullBeforeSaving() {
		User user = new User();
		user.setName("Pooja");
		user.setEmail("pooja@gmail.com");
		user.setAge(25);
		user.setGender("Female");

		assertNull(user.getId());
	}

	@Test
	void idShouldPersistIfManuallySet() {
		User user = new User();
		user.setId(123L);
		assertEquals(123L, user.getId());
	}

	@Test
	void nameShouldFailIfNull() {
		User user = new User();
		user.setName(null);
		user.setEmail("pooja@gmail.com");
		user.setAge(25);
		user.setGender("Female");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
	void nameShouldFailIfEmpty() {
		User user = new User();
		user.setName("");
		user.setEmail("pooja@gmail.com");
		user.setAge(25);
		user.setGender("Female");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	

	@Test
	void emailShouldFailIfNull() {
		User user = new User();
		user.setName("Srikanth");
		user.setEmail(null);
		user.setAge(25);
		user.setGender("Male");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	void emailShouldFailIfEmpty() {
		User user = new User();
		user.setName("Srikanth");
		user.setEmail("");
		user.setAge(25);
		user.setGender("Male");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}



	@Test
	void ageShouldFailIfNull() {
		User user = new User();
		user.setName("Srikanth");
		user.setEmail("sri@gmail.com");
		user.setAge(null);
		user.setGender("Male");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("age")));
	}



	@Test
	void genderShouldFailIfNull() {
		User user = new User();
		user.setName("Pooja");
		user.setEmail("pooja@gmail.com");
		user.setAge(25);
		user.setGender(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gender")));
	}

	@Test
	void genderShouldFailIfEmpty() {
		User user = new User();
		user.setName("Pooja");
		user.setEmail("pooja@gmail.com");
		user.setAge(25);
		user.setGender("");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gender")));
	}

	


	
}