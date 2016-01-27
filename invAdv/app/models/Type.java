package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;

@Entity
public class Type extends Model {
	@Required
	@Unique
	public String type;

	public Type(String type) {
		super();
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}
