package models;

import java.util.*;
import javax.persistence.*;

import com.mchange.v2.lang.ThreadGroupUtils;

import play.data.validation.*;
import play.db.jpa.*;

@Entity
public class User extends Model {
	@Email
	@Required
	public String email;
	@Required
	public String password;
	@Required
	public String fullname;
	public boolean isAdmin;

	@OneToMany(mappedBy = "author")
	public List<InvestementAdvice> investementAdvices;

	@OneToMany(mappedBy = "author")
	public List<Comment> comments;

	/*
	 * Constructeur dans le cas d'un utilisateur non admin
	 */
	public User(String email, String password, String fullname) {
		this(email, password, fullname, false);
	}

	/*
	 * Constructeur paramétré de la classe User
	 */
	public User(@Required String email, @Required String password,
			@Required String fullname, boolean isAdmin) {
		if (find("byEmail", email).first() == null) {
			this.email = email;
			this.password = password;
			this.fullname = fullname;
			this.isAdmin = isAdmin;
			this.comments = new ArrayList<>();
			this.investementAdvices = new ArrayList<>();
		} else {
			System.out.println("the user already exists" + fullname);
		}
	}

	/*
	 * Procedure qui sert à faire l'authentification d'un utilisateur
	 * 
	 * @param email l'email de l'utilisateur
	 * 
	 * @param password le password de l'utilisateur
	 */
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	@Override
	public String toString() {
		return fullname;
	}
}
