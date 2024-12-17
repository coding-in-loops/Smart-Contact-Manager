package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.entities.UserProfileUpdateRequest;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;
	
	//user profile page
		@GetMapping("/profile")
		public String userProfile(Model model,Authentication authentication) {
			return "user/profile";
		}
		
		@PostMapping("/update-profile")
		public String updateProfile(@ModelAttribute UserProfileUpdateRequest userProfileUpdateRequest, 
				@AuthenticationPrincipal User loggedInUser) {
		    // Logic to update the user profile
		    userService.updateUserProfile(loggedInUser, userProfileUpdateRequest);
		    return "redirect:/user/profile";
		}
	
	//user add contact page
	
	
	//user view contacts page
	
	
	//user edit contact page
	
	
	///user delete contact page
	
	//user search contact
}
