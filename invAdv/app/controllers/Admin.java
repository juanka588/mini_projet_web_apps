package controllers;
import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;

public class Admin extends Controller{
	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }
 
    
    public static void index() {
        String user = Security.connected();
        List<InvestementAdvice> posts = InvestementAdvice.find("author.email", user).fetch();
        render(posts);
    }
    
    public static void form() {
        render();
    }
     
    public static void save() {
        // Not implemented yet
    }
    
  
    
    public static void save(Long id, String title, String content, String tags) {
    	InvestementAdvice post;
        if(id == null) {
            // Create post
            User author = User.find("byEmail", Security.connected()).first();
            post = new InvestementAdvice(new Date(), title, content, author, null, 0, 0, null);

        } else {
            // Retrieve post
        	post = InvestementAdvice.findById(id);
            // Edit
            post.title = title;
            post.content = content;
        }
        // Set tags list
        /*for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                post.tags.add(Tag.findOrCreateByName(tag));
            }
        }*/
        // Validate
        validation.valid(post);
        if(validation.hasErrors()) {
            render("@form", post);
        }
        // Save
        post.save();
        index();
    }

    
    public static void form(Long id) {
        if(id != null) {
            InvestementAdvice post = InvestementAdvice.findById(id);
            render(post);
        }
        render();
    }

    
}
