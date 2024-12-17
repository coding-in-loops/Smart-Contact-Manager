package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class PageController {

	@Autowired
	UserService userService;
	
	@GetMapping("/")
	public String index() {
		return "redirect:/home";
	}
	
	@RequestMapping("/home")
	public String home(Model model) {
		//Sending data to view
		model.addAttribute("name","Simran");
		return "home";
	}
	
	//about route
	@RequestMapping("/about")
	public String aboutPage(Model model) {
		model.addAttribute("isLogin",true);
		return "about";
	}
	
	//services route
	@RequestMapping("/services")
	public String servicesPage() {
		return "services";
	}
	
	//contact page
	@RequestMapping("/contact")
	public String contact() {
		return new String("contact");
	}
	
	//login page
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	//register
	@GetMapping("/register")
	public String register(Model model) {
		UserForm userForm=new UserForm();
		model.addAttribute("userForm",userForm);
		return "register";
	}
	
	//processing register
	 @RequestMapping(value = "/do-register", method = RequestMethod.POST)
	public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult ,HttpSession session) {
		 System.out.println("Processing registration");
		 //fetch form data
		//validate the data
		 System.out.println(userForm);
		if(bindingResult.hasErrors()) {
			return "register";
		}
		
		//save to database
		
//		User user=User.builder()
//				.name(userForm.getName())
//				.email(userForm.getEmail())
//				.password(userForm.getPassword())
//				.phoneNumber(userForm.getPhoneNumber())
//				.profilePic("/images/profile.png")
//				.about(userForm.getAbout())
//				.build();
		User user=new User();
		user.setName(userForm.getName());
		user.setEmail(userForm.getEmail());
		user.setEnabled(false);
		user.setAbout(userForm.getAbout());
		user.setPassword(userForm.getPassword());
		user.setPhoneNumber(userForm.getPhoneNumber());
		user.setProfilePic("/images/profile.png");
		userService.saveUser(user);
		System.out.println("User saved: ");
		//message="Registration successful"
		//add message
		Message message=Message.builder()
				.content("Registraion Successful, Verification link is send to your email!")
				.type(MessageType.green)
				.build();
		session.setAttribute("message",message);
		//redirect to login page
		return "redirect:/login";
	}
}
