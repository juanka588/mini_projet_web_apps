package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;

@Entity
public class Category extends Model {

	@OneToMany(mappedBy = "parentCategory")
	public List<Category> categoryChilds;

	@ManyToOne
	public Category parentCategory;

	@Required
	public String categoryTitle;

	/*
	 * Constructeur de la classe Category
	 * 
	 * @param parentCategory le catégorie parent
	 * 
	 * @param categoryTitle l'intitulé de la catégorie
	 */
	public Category(Category parentCategory, String categoryTitle) {
		super();
		Category selected = find("byCategoryTitle", categoryTitle).first();
		if (selected == null) {
			this.parentCategory = parentCategory;
			this.categoryTitle = categoryTitle;
			this.categoryChilds = new ArrayList<>();
			if (parentCategory != null) {
				parentCategory.categoryChilds.add(this);
				parentCategory.save();
			}
		} else {
			System.out.println("the category already exists");
		}
	}

	@Override
	public String toString() {
		return categoryTitle;
	}

}