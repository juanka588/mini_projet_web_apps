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
	
	public double capitalGain;

	public double confidenceIndex;

	@OneToMany(mappedBy = "advice")
	public List<Comment> comments;
	

	@OneToMany (mappedBy = "advice")
	public List<Data> dataRate;

	
	@OneToOne
	public Category category;

	public InvestementAdvice(Date creationDate, String title, String content, User author, Type type,
			double capitalGain, double confidenceIndex, Category category) {
		super();
		this.creationDate = creationDate;
		this.title = title;
		this.content = content;
		this.author = author;
		Data d = new Data(capitalGain, confidenceIndex ,author.id, this ).save();
		this.dataRate = new ArrayList<Data>();
        this.dataRate.add(d);
		this.comments = new ArrayList<>();
		this.category = category;
		this.type = type;
		author.investementAdvices.add(this);
		//author.save();
		this.capitalGain = getcapital();
		this.confidenceIndex = getconfidence();
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

	public void addRate(Long userId, @Required double newCapitalGain, @Required double newConfidenceIndex) {
		if (((0<newCapitalGain) && (newCapitalGain<10000) ) || ((0<newConfidenceIndex) && (newConfidenceIndex<10) )){
			Data newdata = new Data(newCapitalGain, newConfidenceIndex ,userId, this ).save();
			this.dataRate.add(newdata);
			this.capitalGain = getcapital();
			this.confidenceIndex = getconfidence();
		}
	
	}
		
		
	public double getcapital() {
		double avg = 0;
		for (int i =0; i<dataRate.size(); i++) {
			avg += dataRate.get(i).capitalGain;
			System.out.println("capital gain  : "+ i + "==> "+ dataRate.get(i).capitalGain);
		}
		return avg / dataRate.size();
		
	}

	public double getconfidence() {
		double avg = 0.0;
		for (int i =0; i<dataRate.size(); i++) {
			avg += dataRate.get(i).index;
		}
		return avg / dataRate.size();
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
