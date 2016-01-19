package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Comment extends Model {
	
	@ManyToOne
	public User author;
	
	public String comment;
	
	@ManyToOne
	public InvestementAdvice advice;
	
	public Comment(User author, String comment) {
		super();
		this.author = author;
		this.comment = comment;
	}
	
}
