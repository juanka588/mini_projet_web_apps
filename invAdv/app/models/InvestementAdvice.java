package models;

import java.util.*;
import javax.persistence.*;

import org.apache.commons.collections.map.HashedMap;

import play.data.validation.MaxSize;
import play.data.validation.*;
import play.db.jpa.*;

@Entity
public class InvestementAdvice extends Model {

	@Required
	public Date creationDate;

	@Required
	public String title;

	@Required
	@MaxSize(10000)
	public String content;

	@Required
	@ManyToOne
	public User author;

	@OneToOne
	public Type type;

	public HashMap<Long, Double> capitalGains;

	public HashMap<Long, Double> confidenceIndexs;

	public double capitalGain;

	public double confidenceIndex;

	@OneToMany(mappedBy = "advice")
	public List<Comment> comments;

	@OneToOne
	public Category category;

	public InvestementAdvice(Date creationDate, String title, String content, User author, Type type,
			double capitalGain, double confidenceIndex, Category category) {
		super();
		this.creationDate = creationDate;
		this.title = title;
		this.content = content;
		this.author = author;
		this.comments = new ArrayList<>();
		this.category = category;
		this.type = type;
		author.investementAdvices.add(this);
		this.capitalGain = capitalGain;
		this.confidenceIndex = confidenceIndex;
	}

	public double getCapitalGain() {
		return capitalGain;
	}

	public void setCapitalGain(double capitalGain) {
		this.capitalGain = capitalGain;
	}

	public double getConfidenceIndex() {
		return confidenceIndex;
	}

	public void setConfidenceIndex(double confidenceIndex) {
		this.confidenceIndex = confidenceIndex;
	}

	public boolean addCapitalGain(Long userId, double newCapitalGain) {
		if (capitalGains == null) {
			this.capitalGains = new HashMap<Long, Double>();
			this.capitalGains.put(author.id, capitalGain);
		}
		boolean b = (capitalGains.putIfAbsent(userId, newCapitalGain) == null);
		this.capitalGain = getcapital();
		this.save();
		return b;

	}

	public boolean addConfidenceIndex(Long userId, double newConfidenceIndex) {
		if (confidenceIndexs == null) {
			this.confidenceIndexs = new HashMap<Long, Double>();
			this.confidenceIndexs.put(author.id, confidenceIndex);
		}
		boolean b = (confidenceIndexs.putIfAbsent(userId, newConfidenceIndex) == null);
		this.confidenceIndex = getconfidence();
		this.save();
		return b;
	}

	public double getcapital() {
		double avg = 0;
		for (Long key : capitalGains.keySet()) {
			avg += capitalGains.get(key);
		}
		return avg / capitalGains.size();
	}

	public double getconfidence() {
		double avg = 0.0;
		for (Long key : confidenceIndexs.keySet()) {
			avg += confidenceIndexs.get(key);
		}
		return avg / confidenceIndexs.size();
	}

	public void addComment(String fullname, String content) {
		User author = User.find("byFullname", fullname).first();
		Comment comment = new Comment(author, content, this, new Date()).save();
		comments.add(comment);
		this.save();
		System.out.println("comment added succesfully " + comment.toString());
	}

	public InvestementAdvice previous() {
		return InvestementAdvice.find("creationDate < ? order by creationDate desc", creationDate).first();
	}

	public InvestementAdvice next() {
		return InvestementAdvice.find("creationDate > ? order by creationDate asc", creationDate).first();
	}

	@Override
	public String toString() {
		return title;
	}
}
