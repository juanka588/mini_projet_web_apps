package models;

import java.util.*;
import javax.persistence.*;

import org.apache.commons.collections.map.HashedMap;

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

	public HashMap<Long, Double> capitalGains;

	public HashMap<Long, Double> confidenceIndexs;

	@OneToMany(mappedBy = "advice")
	public List<Comment> comments;

	@OneToOne
	public Category category;

	public InvestementAdvice(Date creationDate, String title, User author, double capitalGain, double confidenceIndex,
			List<Comment> comments, Category category) {
		super();
		this.creationDate = creationDate;
		this.title = title;
		this.author = author;
		this.capitalGains = new HashMap<>();
		capitalGains.put(author.id,capitalGain);
		this.confidenceIndexs = new HashMap<>();
		confidenceIndexs.put(author.id,confidenceIndex);
		this.comments = comments;
		this.category = category;
	}

}
