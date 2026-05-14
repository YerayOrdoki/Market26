package domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Salaketa extends Kexa {

	private String mota;

	// Egoeraren balio posibleak:
	// 0: salaketa tratatu gabe
	// 1: salaketa ukatuta
	// 2: salaketa onartuta

	@XmlIDREF
	@ManyToOne(fetch = FetchType.EAGER)
	private Registered salatzailea;

	@ManyToOne(fetch = FetchType.EAGER)
	private Sale produktoa;

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Salaketa() {
		super();
	}

	/**
	 * Salaketa berri bat sortzen du.
	 *
	 * @param mota Salaketaren mota.
	 * @param produktoa Salatutako produktua.
	 * @param pubDate Argitalpen-data.
	 * @param salatzailea Salaketa sortzen duen erabiltzailea.
	 */
	public Salaketa(String mota, Sale produktoa, Date pubDate, Registered salatzailea) {
		super(produktoa.getTitle(), pubDate);
		this.mota = mota;
		this.produktoa = produktoa;
		this.salatzailea = salatzailea;
	}

	/**
	 * Salaketaren mota itzultzen du.
	 *
	 * @return Salaketaren mota.
	 */
	public String getMota() {
		return mota;
	}

	/**
	 * Salaketaren mota eguneratzen du.
	 *
	 * @param mota Ezarri nahi den mota.
	 */
	public void setMota(String mota) {
		this.mota = mota;
	}

	/**
	 * Salatutako produktua itzultzen du.
	 *
	 * @return Produktua.
	 */
	public Sale getProduktoa() {
		return produktoa;
	}

	/**
	 * Salatutako produktua eguneratzen du.
	 *
	 * @param produktoa Ezarri nahi den produktua.
	 */
	public void setProduktoa(Sale produktoa) {
		this.produktoa = produktoa;
	}

	/**
	 * Salaketa egin duen erabiltzailea itzultzen du.
	 *
	 * @return Salatzailea.
	 */
	public Registered getSalatzailea() {
		return salatzailea;
	}

	/**
	 * Salaketa egin duen erabiltzailea eguneratzen du.
	 *
	 * @param salatzailea Ezarri nahi den erabiltzailea.
	 */
	public void setSalatzailea(Registered salatzailea) {
		this.salatzailea = salatzailea;
	}
}