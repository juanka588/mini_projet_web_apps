package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class InvestementAdvice extends Model {

	public Date creationDate;
	public String title;

	@ManyToOne
	public User author;

	public enum type {
		SHORT_TERM, MIDDLE_TERM, LONG_TERM
	};

	@OneToMany
	public List<Grade> capitalGain;
	@OneToMany
	public List<Grade> confidenceIndex;
	@OneToMany
	public List<Comment> comments;
	
	@OneToOne
	public Category category;
	
	
}
