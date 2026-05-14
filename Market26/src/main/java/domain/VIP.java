package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("serial")
public class VIP implements Serializable {

	@Id
	@GeneratedValue
	private Integer id;

	@OneToMany(fetch = FetchType.EAGER)
	private List<Sale> erreserbak = new ArrayList<>();

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public VIP() {
		super();
	}

	/**
	 * VIP objektuaren identifikatzailea itzultzen du.
	 *
	 * @return Identifikatzailea.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * VIP objektuaren identifikatzailea eguneratzen du.
	 *
	 * @param id Ezarri nahi den identifikatzailea.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * VIP erabiltzailearen erreserben zerrenda itzultzen du.
	 *
	 * @return Erreserben zerrenda.
	 */
	public List<Sale> getErreserbak() {
		return erreserbak;
	}

	/**
	 * VIP erabiltzailearen erreserben zerrenda eguneratzen du.
	 *
	 * @param erreserbak Ezarri nahi den erreserben zerrenda.
	 */
	public void setErreserbak(List<Sale> erreserbak) {
		this.erreserbak = erreserbak;
	}

	/**
	 * Egiaztatzen du VIP erabiltzaileak salmenta jakin baten erreserba duen ala ez.
	 *
	 * @param sale Egiaztatu nahi den salmenta.
	 * @return true erreserba badu, false bestela.
	 */
	public boolean daukaErreserba(Sale sale) {
		return erreserbak.stream()
				.filter(s -> s != null)
				.anyMatch(s -> s.getSaleNumber().equals(sale.getSaleNumber()));
	}
}