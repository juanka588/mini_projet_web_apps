package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Comment extends Model {
	@ManyToOne
	public User author;
	public String comment;
}
