package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Category extends Model {
	@OneToMany
	public List<Category> categoryChilds;
	public String categoryTitle;
}
