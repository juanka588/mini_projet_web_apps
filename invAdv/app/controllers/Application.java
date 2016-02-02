package controllers;

import play.*;
import play.data.validation.*;
import play.mvc.*;
import play.libs.*;
import play.cache.*;

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
	

	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#E4EAFD");
		Cache.set(id, code, "10mn");
		renderBinary(captcha);
	}

	public static void show(Long id) {
		InvestementAdvice post = InvestementAdvice.findById(id);
		String randomID = Codec.UUID();
		render(post, randomID);
	}

	public static void index() {
		InvestementAdvice frontPost = InvestementAdvice.find("order by creationDate desc").first();
		List<InvestementAdvice> olderPosts = InvestementAdvice.find("order by creationDate desc").from(1).fetch(10);
		render(frontPost, olderPosts);
	}

	public static void creatUser() {
		render();
	}

	public static void createUser(@Required(message = "An email is required") String email,
			@Required(message = "A password is required") String password,
			@Required(message = "A fullname is required") String fullname,
			@Required(message = "Please type the code") String code, String randomID) {

		validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
		int s = User.find("byEmail", email).fetch().size();
		validation.equals(s, 0).message("User already exists");

		if (validation.hasErrors()) {
			render("Application/creatUser.html", randomID);
		} else {
			User user = new User(email, password, fullname, false);
			user.save();
			InvestementAdvice frontPost = InvestementAdvice.find("order by creationDate desc").first();
			List<InvestementAdvice> olderPosts = InvestementAdvice.find("order by creationDate desc").from(1).fetch(10);
			Cache.delete(randomID);
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

	public static void postCapitalGain(Long postId, @Required(message = "Value of capital gain is required") double capitalGain, 
			@Required(message = "Value of confidence index is required") double confidenceIndex) {
		InvestementAdvice post = InvestementAdvice.findById(postId);
		
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			boolean findUser = find_user(user.id, post);
			validation.equals(findUser, false).message("you have already posted " + user.fullname);

			if (validation.hasErrors()) {
				render("Application/show.html", post);
			} else {
				if (!findUser) {
					boolean cond = post.addRate(user.id, capitalGain, confidenceIndex);
					if (cond) {
						flash.success("Thanks for posting %s", user.fullname);
						render("Application/show.html", post);
					} else {
						render("Application/show.html", post);
					}

				} else {
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
			List<InvestementAdvice> olderPosts = InvestementAdvice
					.find("select p from InvestementAdvice p where " + "title like ? OR content like ?",
							"%" + title + "%", "%" + title + "%")
					.from(0).fetch(10);
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