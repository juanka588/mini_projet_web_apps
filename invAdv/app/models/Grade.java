package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Grade extends Model {
	@ManyToOne
	public User author;
	public float grade;
}
