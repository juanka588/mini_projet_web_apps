package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Category extends Model {

	@OneToMany(mappedBy = "parentCategory")
	public List<Category> categoryChilds;

	@ManyToOne
	public Category parentCategory;

	public String categoryTitle;

	public Category(Category parentCategory, String categoryTitle) {
		super();
		if (find("byCategoryTitle", categoryTitle).first() == null) {
			this.parentCategory = parentCategory;
			this.categoryTitle = categoryTitle;
			this.categoryChilds = new ArrayList<>();
			if (parentCategory != null) {
				parentCategory.categoryChilds.add(this);
			}
		} else {
			System.out.println("the category already exists");
		}
	}

}