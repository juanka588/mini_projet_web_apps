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
	
	public Date commentDate;

	public Comment(User author, String comment, InvestementAdvice advice, Date commentDate) {
		super();
		this.author = author;
		this.comment = comment;
		this.advice = advice;
		this.commentDate = commentDate;
		advice.comments.add(this);
		author.comments.add(this);
	}

}
