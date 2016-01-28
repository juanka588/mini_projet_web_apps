package controllers;
import play.*;
import play.data.validation.Required;
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
     
    public static void save(Long id, @Required String title, @Required String content, @Required double capitalGain, @Required double confidenceIndex, String type, String category ) {
    	InvestementAdvice post;
        
        if(id == null) {
            // Create post
            User author = User.find("byEmail", Security.connected()).first();
            Type t = Type.find("byType", type).first();
            Category c = Category.find("byCategoryTitle", category).first();

            post = new InvestementAdvice(new Date(), title, content, author, t, capitalGain, confidenceIndex, c);
        } else {
            // Retrieve post
        	post = InvestementAdvice.findById(id);
        	Type t = Type.find("byType", type).first();
            Category c = Category.find("byCategoryTitle", category).first();
            // Edit
            post.title = title;
            post.content = content;
            post.capitalGain = capitalGain;
            post.confidenceIndex = confidenceIndex;
            post.type = t;
            post.category = c;
        }
      
        // Validate
        validation.valid(post);
        if(validation.hasErrors()) {
            //
        	 List<Type> allTypes =  Type.all().fetch();
     		//Récupérer la liste des  catégories
     		List<Category> allCategories = Category.all().fetch();
     		
     		render("@form", post,allCategories, allTypes);
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

        // Récupérer la liste des types
        List<Type> allTypes =  Type.all().fetch();
		//Récupérer la liste des  catégories
		List<Category> allCategories = Category.all().fetch();
		
		render(allCategories, allTypes);
    }
    
    
    public static void fill() {
        // Récupérer la liste des types
        List<Type> allTypes =  Type.all().fetch();
		//Récupérer la liste des  catégories
		List<Category> allCategories = Category.all().fetch();
		
		render(allCategories, allTypes);
    }
    
   
   
    
}
