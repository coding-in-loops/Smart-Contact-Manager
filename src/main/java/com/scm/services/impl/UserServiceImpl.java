package com.scm.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.entities.UserProfileUpdateRequest;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Override
	public User saveUser(User user) {
		//dynamically generate userId
		String userID=UUID.randomUUID().toString();
		user.setUserId(userID);
		logger.info("Saving user with ID: {}", user.getUserId());

		//password encode
		//user.setPassword(userID);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		//set user role
		user.setRoleList(List.of(AppConstants.ROLE_USER));
		
//		String emailToken=UUID.randomUUID().toString();
//		user.setEmailToken(emailToken);
//		User savedUser=userRepo.save(user);
//		//createAndAssignVerificationToken(savedUser, emailToken);
//		 try {
//		        emailService.sendVerificationEmail(savedUser);
//		        logger.info("Email sent successfully to {}", savedUser.getEmail());
//		    } catch (Exception e) {
//		        logger.error("Failed to send email to {}: {}", savedUser.getEmail(), e.getMessage());
//		    }
//		return savedUser;
		
		 logger.info(user.getProvider().toString());
		 System.out.println(1);
	        String emailToken = UUID.randomUUID().toString();
	        System.out.println(2);
	        user.setEmailToken(emailToken);
	        System.out.println(3);
	        System.err.println("The user is " + user);
	        User savedUser = userRepo.save(user);
	        System.out.println(4);
	        String emailLink = Helper.getEmailVerificationLink(emailToken);
	        System.out.println(5);
	        emailService.sendEmail(savedUser.getEmail(), "Verify Account : Smart  Contact Manager", emailLink);
	        System.out.println(6);
	        return savedUser;
	}

	@Override
	public Optional<User> getUserById(String id) {
		return userRepo.findById(id);
	}

	@Override
	public Optional<User> updateUser(User user) {
		User user2=userRepo.findById(user.getUserId())
				.orElseThrow(()-> new ResourceNotFoundException("User not found"));
		//update user
		user2.setName(user.getName());
		user2.setEmail(user.getEmail());
		user2.setPassword(user.getPassword());
		user2.setAbout(user.getAbout());
		user2.setPhoneNumber(user.getPhoneNumber());
		user2.setProfilePic(user.getProfilePic());
		user2.setEnabled(user.isEnabled());
		user2.setEmailVerified(user.isEmailVerified());
		user2.setPhoneVerified(user.isPhoneVerified());
		user2.setProvider(user.getProvider());
		user2.setProviderUserId(user.getProviderUserId());
		//save user in database
		User save=userRepo.save(user2);
		return Optional.ofNullable(save);
	}
	
	@Override
	public User saveVerifiedUser(User user) {
		userRepo.save(user);
		return user;
	}

	@Override
	public void deleteUser(String id) {
		User user2=userRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
		userRepo.delete(user2);
	}

	@Override
	public boolean isUserExist(String userId) {
		User user2=userRepo.findById(userId).orElse(null);
		return user2 != null ? true:false;
	}

	@Override
	public boolean isUserExistByEmail(String email) {
		User user=userRepo.findByEmail(email).orElse(null);
		return user != null ? true:false;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepo.findByEmail(email).orElse(null);
	}

	@Override
	public User getUserByEmailToken(String token) {
		if (token == null || token.isEmpty()) {
	        System.out.println("Invalid token: " + token);
	        return null;
	    }
	    User user = userRepo.findByEmailToken(token).orElse(null);
	    System.out.println("Retrieved user: " + user);
	    return user;
	}

	@Override
	public void updateUserProfile(User loggedInUser, UserProfileUpdateRequest userProfileUpdateRequest) {
		
		loggedInUser.setName(userProfileUpdateRequest.getName());
	    loggedInUser.setAbout(userProfileUpdateRequest.getAbout());
	    // Set any other fields you want to update

	    // Save the updated user object to the database
	    userRepo.save(loggedInUser);
	}


/*	@Transactional
    public void createAndAssignVerificationToken(User user, String tokenValue) {
        // Create new verification token
        VerificationToken token = VerificationToken.builder()
                .token(tokenValue)
                .expiryDate(LocalDateTime.now().plusHours(24)) // Set expiry date
                .user(user) // Link the token to the user
                .build();

        // Add the token to the user's list of verification tokens
        user.addVerificationToken(token);

        // Save the user, which also saves the associated verification token due to CascadeType.ALL
        userRepo.save(user);
    }*/
}
