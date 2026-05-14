package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public abstract class Kexa implements Serializable {

	@Id
	@GeneratedValue
	private Integer kexaNumber;

	private Date pubDate;
	private String title;
	private int egoera;

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Kexa() {
		super();
	}

	/**
	 * Kexa berri bat sortzen du.
	 * Hasierako egoera automatikoki 0 balioarekin ezartzen da.
	 *
	 * @param title Kexaren titulua.
	 * @param pubDate Argitalpen-data.
	 */
	public Kexa(String title, Date pubDate) {
		super();
		this.title = title;
		this.pubDate = pubDate;
		this.egoera = 0;
	}

	/**
	 * Beste kexa baten kopia partziala sortzen du.
	 * Egoera berriro 0 balioarekin hasieratzen da.
	 *
	 * @param kexa Kopiatzeko kexa.
	 */
	public Kexa(Kexa kexa) {
		super();
		this.title = kexa.getTitle();
		this.pubDate = kexa.getPubDate();
		egoera = 0;
	}

	/**
	 * Kexaren identifikatzailea itzultzen du.
	 *
	 * @return Kexaren identifikatzailea.
	 */
	public Integer getKexaNumber() {
		return kexaNumber;
	}

	/**
	 * Kexaren identifikatzailea eguneratzen du.
	 *
	 * @param kexaNumber Ezarri nahi den identifikatzailea.
	 */
	public void setKexaNumber(Integer kexaNumber) {
		this.kexaNumber = kexaNumber;
	}

	/**
	 * Kexaren argitalpen-data itzultzen du.
	 *
	 * @return Argitalpen-data.
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * Kexaren argitalpen-data eguneratzen du.
	 *
	 * @param pubDate Ezarri nahi den data.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * Kexaren titulua itzultzen du.
	 *
	 * @return Kexaren titulua.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Kexaren titulua eguneratzen du.
	 *
	 * @param title Ezarri nahi den titulua.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Kexaren egoera itzultzen du.
	 *
	 * @return Kexaren egoera.
	 */
	public int getEgoera() {
		return egoera;
	}

	/**
	 * Kexaren egoera eguneratzen du.
	 *
	 * @param egoera Ezarri nahi den egoera.
	 */
	public void setEgoera(int egoera) {
		this.egoera = egoera;
	}
}