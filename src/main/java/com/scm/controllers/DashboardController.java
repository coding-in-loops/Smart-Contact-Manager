package com.scm.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.ContactService;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user/dashboard")
public class DashboardController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @RequestMapping
    public String dashboard(Model model, Authentication authentication) {
        // Get the logged-in user's email
        String username = Helper.getEmailOfLoggedInUser(authentication);
        System.out.println("Logged-in username: " + username);

        // Fetch the user by email
        User user = userService.getUserByEmail(username);
        if (user == null) {
            System.out.println("User not found with email: " + username);
            return "redirect:/login"; // Redirect if user is not found
        }

        // Fetch total contacts
        long totalContacts = (user.getContacts() != null) ? user.getContacts().size() : 0;
        System.out.println("Total contacts: " + totalContacts);

        // Fetch recently added contacts (e.g., top 5)
        List<Contact> recentlyAdded = contactService.getRecentlyAddedContacts(user, 5);
        if (recentlyAdded != null && !recentlyAdded.isEmpty()) {
            System.out.println("Recently added contacts: " + recentlyAdded.size());
        } else {
            System.out.println("No recently added contacts.");
        }

        // Add attributes to the model
        model.addAttribute("loggedInUser", user); // Add logged-in user details
        model.addAttribute("totalContacts", totalContacts); // Add total contacts
        model.addAttribute("recentlyAddedContacts", recentlyAdded); // Add recent contacts

        return "user/dashboard"; // Return dashboard view
    }
}
