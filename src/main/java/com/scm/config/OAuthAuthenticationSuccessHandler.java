package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	Logger logger=LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);
	
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		logger.info("Authentication successful for user: {}", authentication.getName());
		logger.info("OAuthAuthenticationSuccessHandler");
		
		//save in database
		
		//identify the account through which user loggedin (provider)
		
		var oauth2AuthenticationToken=(OAuth2AuthenticationToken)authentication;
		String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
		logger.info(authorizedClientRegistrationId);
		
		DefaultOAuth2User oauth2User=(DefaultOAuth2User)authentication.getPrincipal();
		
		oauth2User.getAttributes().forEach((key,value)->{
			logger.info("{}=>{}",key,value);
		});
		
		User user=new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setRoleList(List.of(AppConstants.ROLE_USER));
		user.setEmailVerified(true);
		user.setEnabled(true);
		user.setPassword("dummy");
		System.out.println(authorizedClientRegistrationId);
		if(authorizedClientRegistrationId.equalsIgnoreCase("google")) {
			user.setEmail(oauth2User.getAttribute("email").toString());
			user.setProfilePic(oauth2User.getAttribute("picture").toString());
			user.setName(oauth2User.getAttribute("name").toString());
			user.setProviderUserId(oauth2User.getName());
			user.setProvider(Providers.GOOGLE);
			user.setAbout("This account is created using google..");
			
		}else if(authorizedClientRegistrationId.equalsIgnoreCase("github")) {
			String email=oauth2User.getAttribute("email")!=null ?
					oauth2User.getAttribute("email").toString():
					oauth2User.getAttribute("login").toString()+"@gmail.com";
			String picture=oauth2User.getAttribute("avatar_url").toString();
			String name=oauth2User.getAttribute("login").toString();
			String providerUserId=oauth2User.getName();
			
			user.setEmail(email);
			user.setProfilePic(picture);
			user.setProviderUserId(providerUserId);
			user.setName(name);
			user.setProvider(Providers.GITHUB);
			user.setAbout("This account is created using github..");
			
		}else if(authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {
			
		}else {
			logger.info("Unknown Provider");
		}
		
		/*DefaultOAuth2User user=(DefaultOAuth2User)authentication.getPrincipal();
		
		
		
//		logger.info(user.getName());
//		
//		user.getAttributes().forEach((key,value)->{
//			logger.info("{}=>{}",key,value);
//		});
//		logger.info(user.getAuthorities().toString());
		
		String email=user.getAttribute("email").toString();
		String name=user.getAttribute("name").toString();
		String picture=user.getAttribute("picture").toString();
		
		//create user and save in database
		User user1=new User();
		user1.setName(name);
		user1.setEmail(email);
		user1.setProfilePic(picture);
		user1.setPassword("password");
		user1.setUserId(UUID.randomUUID().toString());
		user1.setProvider(Providers.GOOGLE);
		user1.setEnabled(true);
		user1.setEmailVerified(true);
		user1.setProviderUserId(user.getName());
		user1.setRoleList(List.of(AppConstants.ROLE_USER));
		user1.setAbout("This account is created using google..");
		
		User user2=userRepo.findByEmail(email).orElse(null);
		
		if(user2==null) {
			userRepo.save(user1);
			logger.info("User saved");
		}
		*/
		
		User existingUser = userRepo.findByEmail(user.getEmail().toLowerCase()).orElse(null);
		if (existingUser == null) {
		    userRepo.save(user);
		    logger.info("User saved: {}", user.getEmail());
		} else {
		    logger.info("User already exists: {}", existingUser.getEmail());
		}

		System.out.println("----------------------------------------------");
		new DefaultRedirectStrategy().sendRedirect(request,response,"/user/profile");
		System.out.println("Redirected to user profile");
	}

}
