package models;

import java.util.*;
import javax.persistence.*;

import org.apache.commons.collections.map.HashedMap;

import play.data.validation.MaxSize;
import play.data.validation.*;
import play.db.jpa.*;

@Entity
public class Data extends Model {

	public double capitalGain;
	public double index;
	public long idUser;

	@ManyToOne
	public InvestementAdvice advice;

	/*
	 * Constructeur de la classe Data
	 * 
	 * @param capitalGain la valeur de +/- value
	 * 
	 * @param index l'indice de confiance
	 * 
	 * @param idUser l'Id de l'utiulisateur
	 * 
	 * @param advice l'investissement relatif à ces parmètres
	 */
	public Data(double capitalGain, double index, long idUser,
			InvestementAdvice advice) {
		super();
		this.capitalGain = capitalGain;
		this.index = index;
		this.idUser = idUser;
		this.advice = advice;
	}

	@Override
	public String toString() {
		return index + " " + capitalGain;
	}
}
