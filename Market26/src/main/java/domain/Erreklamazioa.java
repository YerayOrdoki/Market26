package domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Erreklamazioa extends Kexa {

	@XmlIDREF
	private Registered eroslea;

	@XmlIDREF
	private Registered saltzailea;

	private BoughtSale produktoa;

	// Egoeraren balio posibleak:
	// 0: negoziatzen
	// 1: administratzaileari eskalatuta
	// 2: itzulera onartua
	// 3: itzulera deuseztatua

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DisputaMezua> mezuak = new ArrayList<DisputaMezua>();

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Erreklamazioa() {
		super();
	}

	/**
	 * Erreklamazio berri bat sortzen du.
	 *
	 * @param title Erreklamazioaren titulua.
	 * @param pubDate Argitalpen-data.
	 * @param eroslea Erreklamazioa egiten duen eroslea.
	 * @param saltzailea Erreklamazioarekin lotutako saltzailea.
	 * @param produktoa Erreklamatutako erositako produktua.
	 */
	public Erreklamazioa(String title, Date pubDate, Registered eroslea, Registered saltzailea, BoughtSale produktoa) {
		super(title, pubDate);
		this.eroslea = eroslea;
		this.saltzailea = saltzailea;
		this.produktoa = produktoa;
	}

	/**
	 * Erreklamazioaren eroslea itzultzen du.
	 *
	 * @return Eroslea.
	 */
	public Registered getEroslea() {
		return eroslea;
	}

	/**
	 * Erreklamazioaren eroslea eguneratzen du.
	 *
	 * @param eroslea Ezarri nahi den eroslea.
	 */
	public void setEroslea(Registered eroslea) {
		this.eroslea = eroslea;
	}

	/**
	 * Erreklamazioaren saltzailea itzultzen du.
	 *
	 * @return Saltzailea.
	 */
	public Registered getSaltzailea() {
		return saltzailea;
	}

	/**
	 * Erreklamazioaren saltzailea eguneratzen du.
	 *
	 * @param saltzailea Ezarri nahi den saltzailea.
	 */
	public void setSaltzailea(Registered saltzailea) {
		this.saltzailea = saltzailea;
	}

	/**
	 * Erreklamazioari lotutako mezuen zerrenda itzultzen du.
	 *
	 * @return Mezuen zerrenda.
	 */
	public List<DisputaMezua> getMezuak() {
		return mezuak;
	}

	/**
	 * Erreklamazioari lotutako mezuen zerrenda eguneratzen du.
	 *
	 * @param mezuak Ezarri nahi den mezuen zerrenda.
	 */
	public void setMezuak(List<DisputaMezua> mezuak) {
		this.mezuak = mezuak;
	}

	/**
	 * Erreklamazioarekin lotutako produktua itzultzen du.
	 *
	 * @return Produktua.
	 */
	public BoughtSale getProduktoa() {
		return produktoa;
	}

	/**
	 * Erreklamazioarekin lotutako produktua eguneratzen du.
	 *
	 * @param produktoa Ezarri nahi den produktua.
	 */
	public void setProduktoa(BoughtSale produktoa) {
		this.produktoa = produktoa;
	}
}