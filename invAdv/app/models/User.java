package models;

import java.util.*;
import javax.persistence.*;

import com.mchange.v2.lang.ThreadGroupUtils;

import play.db.jpa.*;

@Entity
public class User extends Model {

	public String email;
	public String password;
	public String fullname;
	public boolean isAdmin;

	@OneToMany(mappedBy = "author")
	public List<InvestementAdvice> investementAdvices;

	@OneToMany(mappedBy = "author")
	public List<Comment> comments;

	public User(String email, String password, String fullname) {
		this(email, password, fullname, false);
	}

	public User(String email, String password, String fullname, boolean isAdmin) {
		if (find("byEmail", email).first() == null) {
			this.email = email;
			this.password = password;
			this.fullname = fullname;
			this.isAdmin = isAdmin;
			this.comments = new ArrayList<>();
			this.investementAdvices = new ArrayList<>();
		} else {
			System.out.println("the user already exists");
		}
	}

	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	@Override
	public String toString() {
		return fullname;
	}
}
