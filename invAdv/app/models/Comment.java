package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;

@Entity
public class Comment extends Model {

	@ManyToOne
	public User author;

	@Required
	public String comment;

	@ManyToOne
	public InvestementAdvice advice;

	@Required
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

	@Override
	public String toString() {
		return comment;
	}
}
