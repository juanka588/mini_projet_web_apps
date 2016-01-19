package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Category extends Model {

	@OneToMany(mappedBy="parentCategory")
	public List<Category> categoryChilds;

	@ManyToOne
	public Category parentCategory;

	public String categoryTitle;

	public Category(List<Category> categoryChilds, String categoryTitle) {
		super();
		this.categoryChilds = categoryChilds;
		this.categoryTitle = categoryTitle;
	}

}
