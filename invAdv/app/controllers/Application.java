package controllers;

import play.*;
import play.data.validation.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	@Before
	static void addDefaults() {
	    renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
	    renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}

	public static void show(Long id) {
	    InvestementAdvice post = InvestementAdvice.findById(id);
	    render(post);
	}
	
    public static void index() {
    	  InvestementAdvice frontPost = InvestementAdvice.find("order by creationDate desc").first();
          List<InvestementAdvice> olderPosts = InvestementAdvice.find("order by creationDate desc").from(1).fetch(10);
          render(frontPost, olderPosts);
    }

    public static void postComment(Long postId,  @Required String author, @Required String content) {
        InvestementAdvice post = InvestementAdvice.findById(postId);
        if (validation.hasErrors()) {
            render("Application/show.html", post);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        show(postId);
    }
    
    public static void postCapitalGain(Long postId,  @Required String author, @Required double capitalGain, @Required double confidenceIndex) {
        InvestementAdvice post = InvestementAdvice.findById(postId);
        User A = User.find("byFullname", author).first();
        if (validation.hasErrors()) {
            render("Application/show.html", post);
        }
        boolean c = post.addCapitalGain(A.id, capitalGain);
        boolean d = post.addConfidenceIndex(A.id, confidenceIndex);
        if ((c)&(d)){
        	flash.success("Thanks for posting %s", author);
        }
        show(postId);
    }
}