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
			}
			post.addComment(user.fullname, content);
			flash.success("Thanks for posting %s", user.fullname);
			show(postId);
		} else {
			// flash.error("Votre post n'a pas été ajouté car vous n'etes pas
			// connécté %s", author);
			render("Application/show.html", post);
		}
	}

	public static void postCapitalGain(Long postId, @Required double capitalGain, @Required double confidenceIndex) {
		InvestementAdvice post = InvestementAdvice.findById(postId);
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			if (validation.hasErrors()) {
				render("Application/index.html", post);
			}
			post.addRate(user.id, capitalGain, confidenceIndex);
			System.out.println("capitalgain : " + post.capitalGain);
			flash.success("Thanks for posting %s", user.fullname);
			render("Application/show.html", post);
		} else {
			render("Application/show.html", post);
		}
	}

	public static void listAdviceByTitle(@Required String title) {
		if (validation.hasErrors()) {
			render("Application/index.html");
		} else {
			InvestementAdvice frontPost = InvestementAdvice.find("byTitleLike", "%" + title + "%").first();
			List<InvestementAdvice> olderPosts = InvestementAdvice.find("byTitleLike", "%" + title + "%").from(1)
					.fetch(10);
			render("Application/index.html", frontPost, olderPosts);
		}
	}

	public static void listAdviceByCategory(@Required String category) {
		System.out.println(category);
		Category cat = Category.find("byCategoryTitle", category).first();
		InvestementAdvice frontPost = InvestementAdvice.find("byCategory", cat).first();
		List<InvestementAdvice> olderPosts = InvestementAdvice.find("byCategory", cat).from(1).fetch();
		ArrayList<Category> list = new ArrayList<Category>();
		list.add(cat);
		for (int j=0;j<list.size();j++) {
			Category c=list.get(j);
			for (int i = 0; i < c.categoryChilds.size(); i++) {
				list.add(c.categoryChilds.get(i));
				List<InvestementAdvice> Posts = InvestementAdvice.find("byCategory", c.categoryChilds.get(i))
						.fetch();
				olderPosts.addAll(Posts);
			}
		}
		render(frontPost, olderPosts);
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