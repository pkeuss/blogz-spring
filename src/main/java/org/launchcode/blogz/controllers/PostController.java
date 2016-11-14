package org.launchcode.blogz.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.blogz.models.Post;
import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PostController extends AbstractController {

	@RequestMapping(value = "/blog/newpost", method = RequestMethod.GET)
	public String newPostForm() {
		return "newpost";
	}
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, Model model) {
		
		// TODO - implement newPost
		String error = "";
		
		// get request parameters
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		
		// validate parameters and return to form if errors present
		if(title == null || title == "" || body == null || body == ""){
			error = "Both a title and a body are needed to create a post";
			model.addAttribute("title", title);
			model.addAttribute("body", body);
			model.addAttribute("error", error);
			return "newpost";
		}
		
		// get logged in user and create a new post
		HttpSession thisSession  = request.getSession();
		User u = getUserFromSession(thisSession);
		if(u == null){
			return "redirect:login";
		}
		
		Post p = new Post(title, body, u);
		postDao.save(p);
		
		return "redirect:" + u.getUsername()+ "/" + p.getUid();  		
	}
	
	// handles requests such as /blog/chris/5
	@RequestMapping(value = "/blog/{username}/{uid}", method = RequestMethod.GET)
	public String singlePost(@PathVariable String username, @PathVariable int uid, Model model) {
		
		// TODO - implement singlePost
		
		//get given post 
		Post p = postDao.findByUid(uid);
		
		//pass the post into the template
		model.addAttribute("post", p);
		
		return "post";
	}
	
	@RequestMapping(value = "/blog/{username}", method = RequestMethod.GET)
	public String userPosts(@PathVariable String username, Model model) {
		
		// TODO - implement userPosts
		User u = userDao.findByUsername(username);
		
		//get all posts for a particular user
		List<Post> listOfPosts = u.getPosts();
		
		//pass the posts into the template
		model.addAttribute("posts", listOfPosts);
		
		return "blog";
	}
	
}
