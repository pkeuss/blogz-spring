package org.launchcode.blogz.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController extends AbstractController {
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm() {
		return "signup";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, Model model) {
		// TODO - implement signup
		// Create needed parameters and initialize
		boolean isPassError = false;
		boolean isUserError = false;
		String verError = "";
		String passError = "";
		String userError = "";
		
		// get parameters from request
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String verify = request.getParameter("verify");
		
		// validate parameters 
		// Do the Passwords match?
		isPassError = password.equals(verify);
		if(isPassError == false){
			verError = "The Passwords don't match";
		}
		
		// User name acceptable?
		isUserError = User.isValidUsername(username);
		if(isUserError == false){
			userError = "Usernames must start with a letter and contain only numbers, letters, a dash"
					+ ", or an underscore.  They also must be 5 - 12 characters long.";						
		}
		// Check to make sure the username is not already taken
		else{
			List<User> users = userDao.findAll();
			for(int i = 0; i < users.size(); i++){
				isUserError = users.get(i).getUsername().equals(username);
				if(isUserError == true){
					userError = "That Username is already spoken for, please choose a different one";
				}
			}
		}
		
		// Password acceptable?
		isPassError = User.isValidPassword(password);
		if(isPassError == false){
			passError = "The Password must be between 6 - 20 characters long with no spaces";
		}
		
		// redirect back to signin page if errors are present
		if (verError != "" || userError != "" || passError != ""){
			model.addAttribute("username_error", userError);
			model.addAttribute("password_error", passError);
			model.addAttribute("verify_error", verError);
			return "signup";
		}		
		
		// if good create new user and put them in the session
		User u = new User(username, password);
		userDao.save(u);
		HttpSession thisSession = request.getSession();
		
		setUserInSession(thisSession, u);
		
		return "redirect:blog/newpost";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {
		
		String error = "";
		boolean pass = false;
		// TODO - implement login
		// get parameters from request
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		// fetch user from table
		User u = userDao.findByUsername(username);
		 
		// re-prompt if username is not found in user table
		if (u == null){
			error = "Username does not exist";
			model.addAttribute("error", error);
			return "login";
		}
		
		// check to make sure the password is correct
		pass = u.isMatchingPassword(password);
		
		// re-prompt if password cannot be verified
		if (pass == false){
			error = "The password you typed is incorrect";
			model.addAttribute("error", error);
			model.addAttribute("username", username);
			return "login";
		}
		
		// login user
		HttpSession thisSession = request.getSession();
		setUserInSession(thisSession, u);
				
		return "redirect:blog/newpost";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request){
        request.getSession().invalidate();
		return "redirect:/";
	}
}
