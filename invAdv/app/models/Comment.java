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

	/*
	 * Constructeur de la classe Comment
	 * 
	 * @param author l'auteur du commentaire
	 * 
	 * @param comment le contenu du commentaire
	 * 
	 * @param advice l'investissement relatif au commentaire
	 * 
	 * @param commentDate la date de publication du commentaire
	 */
	public Comment(User author, String comment, InvestementAdvice advice,
			Date commentDate) {
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
