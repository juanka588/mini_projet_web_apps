package controllers;

import play.*;
import play.data.validation.*;
import play.mvc.*;

import java.util.*;

import org.h2.mvstore.ConcurrentArrayList;

import models.*;

public class Application extends Controller {

	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		} else {
			renderArgs.put("user", null);
		}

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

	public static void creatUser() {
		render();
	}

	public static void createUser(@Required String email, @Required String password, @Required String fullname) {
		if (validation.hasErrors()) {
			render("Application/creatUser.html");
		} else {
			User user = new User(email, password, fullname, false).save();
			InvestementAdvice frontPost = InvestementAdvice.find("order by creationDate desc").first();
			List<InvestementAdvice> olderPosts = InvestementAdvice.find("order by creationDate desc").from(1).fetch(10);
			render("Application/index.html", frontPost, olderPosts);
		}
	}

	public static void postComment(Long postId, @Required String content) {
		InvestementAdvice post = InvestementAdvice.findById(postId);
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			if (validation.hasErrors()) {
				render("Application/show.html", post);
			} else {
				post.addComment(user.fullname, content);
				flash.success("Thanks for posting %s", user.fullname);
				show(postId);
			}
		} else {
			render("Application/creatUser.html");
		}
	}

	static boolean find_user(long user_id, InvestementAdvice post) {
		for (int i = 0; i < post.dataRate.size(); i++) {
			if (post.dataRate.get(i).idUser == user_id) {
				return true;
			}
		}
		return false;
	}

	public static void postCapitalGain(Long postId, @Required double capitalGain, @Required double confidenceIndex) {
		InvestementAdvice post = InvestementAdvice.findById(postId);
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			if (validation.hasErrors()) {
				render("Application/index.html", post);
			} else {
				if (!find_user(user.id, post)) {
					boolean cond = post.addRate(user.id, capitalGain, confidenceIndex);
					if (cond) {
						System.out.println("capitalgain : " + post.capitalGain);
						flash.success("Thanks for posting %s", user.fullname );
						render("Application/show.html", post);
					}
					else{
						
						render("Application/show.html", post);
					}
				} else {
					flash.success("you have already posted %s", user.fullname );
					render("Application/show.html", post);
				}
			}
		} else {
			render("Application/creatUser.html");
		}
	}


	public static void listAdviceByTitle(@Required String title) {
		if (validation.hasErrors()) {
			render("Application/index.html");
		} else {
			List<InvestementAdvice> olderPosts = InvestementAdvice.find("select p from InvestementAdvice p where "
					+ "title like ? OR content like ?", "%"+title+"%", "%"+title+"%").from(0)
					.fetch(10);
			render(olderPosts);
		}
	}

	public static void listAdviceByCategory(@Required String category) {
		Category cat = Category.find("byCategoryTitle", category).first();
		List<InvestementAdvice> olderPosts = InvestementAdvice.find("byCategory", cat).from(0).fetch();
		ArrayList<Category> list = new ArrayList<Category>();
		list.add(cat);
		System.out.println(cat.categoryChilds.size());
		for (int j = 0; j < list.size(); j++) {
			Category c = list.get(j);
			System.out.println(c);
			for (int i = 0; i < c.categoryChilds.size(); i++) {
				list.add(c.categoryChilds.get(i));
				List<InvestementAdvice> Posts = InvestementAdvice.find("byCategory", c.categoryChilds.get(i)).fetch();
				olderPosts.addAll(Posts);
			}
		}
		render(olderPosts);
	}

	public static void getCat() {
		List<Category> allCategories = Category.all().fetch();
		List<Category> ParentCategories = new ArrayList<Category>();
		for (int i = 0; i < allCategories.size(); i++) {
			if (allCategories.get(i).parentCategory == null) {
				ParentCategories.add(allCategories.get(i));
			}
		}
		render(ParentCategories);
	}

}