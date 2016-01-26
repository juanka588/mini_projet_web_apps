package models;

import java.util.*;
import javax.persistence.*;

import org.apache.commons.collections.map.HashedMap;

import play.db.jpa.*;

@Entity
public class InvestementAdvice extends Model {

	public Date creationDate;
	public String title;
	public String content;
	@ManyToOne
	public User author;

	@OneToOne
	public Type type;

	public HashMap<Long, Double> capitalGains;

	public HashMap<Long, Double> confidenceIndexs;

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
		this.capitalGains = new HashMap<>();
		capitalGains.put(author.id, capitalGain);
		this.confidenceIndexs = new HashMap<>();
		confidenceIndexs.put(author.id, confidenceIndex);
		this.comments = new ArrayList<>();
		this.category = category;
		this.type = type;
		author.investementAdvices.add(this);
	}

	public boolean addCapitalGain(Long userId, double newCapitalGain) {
		return (capitalGains.putIfAbsent(userId, newCapitalGain) == null);
	}

	public boolean addConfidenceIndex(Long userId, double newConfidenceIndex) {
		return (confidenceIndexs.putIfAbsent(userId, newConfidenceIndex) == null);
	}

	public double getCapitalGain() {
		double avg = 0;
		for (Long key : capitalGains.keySet()) {
			avg += capitalGains.get(key);
		}
		return avg / capitalGains.size();
	}

	public double getConfidenceIndex() {
		double avg = 0;
		for (Long key : confidenceIndexs.keySet()) {
			avg += confidenceIndexs.get(key);
		}
		return avg / confidenceIndexs.size();
	}

}
