package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.Unique;
import play.db.jpa.*;

@Entity
public class Type extends Model{
	@Unique
	public String type;

	public Type(String type) {
		super();
		this.type = type;
	}
}
