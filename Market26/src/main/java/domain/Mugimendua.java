package domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import configuration.UtilDate;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Mugimendua {

	Date data;
	BoughtSale produktua;
	Double diruKantitatea;

	// 1: dirua irabazi, 0: dirua galdu
	int mugimenduMota;

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Mugimendua() {
		super();
	}

	/**
	 * Produktu bati lotutako mugimendu berri bat sortzen du.
	 * Produktuaren prezioa automatikoki gordetzen da diru-kopuru bezala.
	 *
	 * @param produktua Mugimenduarekin lotutako erositako produktua.
	 * @param mota Mugimendu mota.
	 */
	public Mugimendua(BoughtSale produktua, int mota) {
		this.produktua = produktua;
		this.data = UtilDate.trim(new Date());
		this.mugimenduMota = mota;
		this.diruKantitatea = (double) produktua.getSale().getPrice();
	}

	/**
	 * Diru-sarrera edo diru-irteera zuzen bat gordetzeko mugimendu berri bat sortzen du.
	 *
	 * @param diruKantitatea Mugimenduan erregistratu nahi den diru-kopurua.
	 * @param mota Mugimendu mota.
	 */
	public Mugimendua(double diruKantitatea, int mota) {
		this.diruKantitatea = diruKantitatea;
		this.mugimenduMota = mota;
		this.produktua = null;
		this.data = UtilDate.trim(new Date());
	}

	/**
	 * Mugimenduaren data itzultzen du.
	 *
	 * @return Mugimenduaren data.
	 */
	public Date getData() {
		return data;
	}

	/**
	 * Mugimenduaren data eguneratzen du.
	 *
	 * @param data Ezarri nahi den data.
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * Mugimenduarekin lotutako produktua itzultzen du.
	 *
	 * @return Lotutako produktua.
	 */
	public BoughtSale getProduktua() {
		return produktua;
	}

	/**
	 * Mugimenduarekin lotutako produktua eguneratzen du.
	 *
	 * @param produktua Ezarri nahi den produktua.
	 */
	public void setProduktua(BoughtSale produktua) {
		this.produktua = produktua;
	}

	/**
	 * Mugimenduaren diru-kantitatea itzultzen du.
	 *
	 * @return Diru-kantitatea.
	 */
	public Double getDiruKantitatea() {
		return diruKantitatea;
	}

	/**
	 * Mugimenduaren diru-kantitatea eguneratzen du.
	 *
	 * @param diruKantitatea Ezarri nahi den diru-kantitatea.
	 */
	public void setDiruKantitatea(Double diruKantitatea) {
		this.diruKantitatea = diruKantitatea;
	}

	/**
	 * Mugimendu motaren balioa itzultzen du.
	 *
	 * @return Mugimendu mota.
	 */
	public int getMugimenduMota() {
		return mugimenduMota;
	}

	/**
	 * Mugimendu mota eguneratzen du.
	 *
	 * @param mugimenduMota Ezarri nahi den mugimendu mota.
	 */
	public void setMugimenduMota(int mugimenduMota) {
		this.mugimenduMota = mugimenduMota;
	}
}