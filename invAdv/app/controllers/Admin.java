package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.util.*;

import models.*;

public class Admin extends Controller {
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		}
	}

	public static void index() {
		String user = Security.connected();
		List<InvestementAdvice> posts = InvestementAdvice.find("author.email",
				user).fetch();
		render(posts);
	}

	/*
	 * Ajoute l'advice de l'utilisateur si l'advice n'existe pas Sinon,
	 * modification des paramètres de l'advice
	 */
	public static void save(Long id, @Required String title,
			@Required String content, @Required double capitalGain,
			@Required double confidenceIndex, @Required String type,
			@Required String category) {
		InvestementAdvice post;
		if (validation.hasErrors()) {
			List<Type> allTypes = Type.all().fetch();
			List<Category> allCategories = Category.all().fetch();
			render("@form", id, allCategories, allTypes);
		} else {
			if (id == null) {
				// Create post
				User author = User.find("byEmail", Security.connected())
						.first();
				Type t = Type.find("byType", type).first();
				Category c = Category.find("byCategoryTitle", category).first();

				post = new InvestementAdvice(new Date(), title, content,
						author, t, capitalGain, confidenceIndex, c);
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
			// Save
			post.save();
			index();
		}
	}

	/*
	 * Envoie toute la liste des catégories et types
	 */
	public static void form(Long id) {
		if (id != null) {
			// Récupérer la liste des types
			List<Type> allTypes = Type.all().fetch();
			// Récupérer la liste des catégories
			List<Category> allCategories = Category.all().fetch();
			InvestementAdvice post = InvestementAdvice.findById(id);
			render(post, allCategories, allTypes);
		}

		// Récupérer la liste des types
		List<Type> allTypes = Type.all().fetch();
		// Récupérer la liste des catégories
		List<Category> allCategories = Category.all().fetch();

		render(allCategories, allTypes);
	}

	/*
	 * Envoie toute la liste des catégories et types
	 */
	public static void fill() {
		// Récupérer la liste des types
		List<Type> allTypes = Type.all().fetch();
		// Récupérer la liste des catégories
		List<Category> allCategories = Category.all().fetch();

		render(allCategories, allTypes);
	}

}
