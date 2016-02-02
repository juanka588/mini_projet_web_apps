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

	@OneToMany(mappedBy = "advice")
	public List<Data> dataRate;

	@OneToOne
	public Category category;

	/*
	 * Constructeur de la classe InvestementAdvice
	 */
	public InvestementAdvice(Date creationDate, String title, String content,
			User author, Type type, double capitalGain, double confidenceIndex,
			Category category) {

		super();
		this.creationDate = creationDate;
		this.title = title;
		this.content = content;
		this.author = author;
		this.save();
		Data d = new Data(capitalGain, confidenceIndex, author.id, this).save();
		this.dataRate = new ArrayList<Data>();
		this.dataRate.add(d);
		this.comments = new ArrayList<>();
		this.category = category;
		this.type = type;
		author.investementAdvices.add(this);
		this.capitalGain = getcapital();
		this.confidenceIndex = getconfidence();
		this.save();
	}

	/*
	 * Accesseur de capitalGain
	 */
	public double getCapitalGain() {
		return capitalGain;
	}

	/*
	 * Mutateur de capitalGain
	 */
	public void setCapitalGain(double capitalGain) {
		this.capitalGain = capitalGain;
	}

	/*
	 * Accesseur de confidenceIndex
	 */
	public double getConfidenceIndex() {
		return confidenceIndex;
	}

	/*
	 * Mutateur de confidenceIndex
	 */
	public void setConfidenceIndex(double confidenceIndex) {
		this.confidenceIndex = confidenceIndex;
	}

	/*
	 * Ajoute les paramètres (newCapitalGain et newConfidenceIndex) à la liste
	 * Date de l'advice
	 */
	public boolean addRate(Long userId, @Required double newCapitalGain,
			@Required double newConfidenceIndex) {
		boolean cond = ((-10000 < newCapitalGain) && (newCapitalGain < 10000))
				|| ((0 < newConfidenceIndex) && (newConfidenceIndex < 20));
		if (cond) {
			if (dataRate.isEmpty()) {
				Data d = new Data(this.capitalGain, this.confidenceIndex,
						this.author.id, this).save();
				dataRate.add(d);
			}
			Data newdata = new Data(newCapitalGain, newConfidenceIndex, userId,
					this).save();
			this.dataRate.add(newdata);
			this.save();
			this.capitalGain = getcapital();
			this.confidenceIndex = getconfidence();
			this.save();
		}
		return cond;
	}

	/*
	 * @return la moyenne des capitalGain de l'advice
	 */
	public double getcapital() {
		double avg = 0;
		for (int i = 0; i < dataRate.size(); i++) {
			avg += dataRate.get(i).capitalGain;
		}
		return avg / dataRate.size();

	}

	/*
	 * @return la moyenne des confidenceIndex de l'advice
	 */
	public double getconfidence() {
		double avg = 0.0;
		for (int i = 0; i < dataRate.size(); i++) {
			avg += dataRate.get(i).index;
		}
		return avg / dataRate.size();
	}

	/*
	 * Ajoute le commentaire à la liste des commentaire de l'advice
	 */
	public void addComment(String fullname, String content) {
		User author = User.find("byFullname", fullname).first();
		Comment comment = new Comment(author, content, this, new Date()).save();
		comments.add(comment);
		this.save();
	}

	/*
	 * @return l'advice précedent (classement par dates)
	 */
	public InvestementAdvice previous() {
		return InvestementAdvice.find(
				"creationDate < ? order by creationDate desc", creationDate)
				.first();
	}

	/*
	 * @return l'advice suivant (classement par dates)
	 */
	public InvestementAdvice next() {
		return InvestementAdvice.find(
				"creationDate > ? order by creationDate asc", creationDate)
				.first();
	}

	@Override
	public String toString() {
		return title;
	}
}
