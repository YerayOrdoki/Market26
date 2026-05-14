package domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class DisputaMezua {

	@Id
	@GeneratedValue
	private Integer saleId;

	String textua;
	Date data;

	@XmlIDREF
	User igorlea;

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public DisputaMezua() {
		super();
	}

	/**
	 * Disputa bateko mezu berri bat sortzen du.
	 * Data automatikoki une honetako datarekin ezartzen da.
	 *
	 * @param textua Mezuaren edukia.
	 * @param igorlea Mezua bidaltzen duen erabiltzailea.
	 */
	public DisputaMezua(String textua, User igorlea) {
		super();
		this.textua = textua;
		this.data = new Date();
		this.igorlea = igorlea;
	}

	/**
	 * Mezuaren identifikatzailea itzultzen du.
	 *
	 * @return Mezuaren identifikatzailea.
	 */
	public Integer getSaleId() {
		return saleId;
	}

	/**
	 * Mezuaren identifikatzailea eguneratzen du.
	 *
	 * @param saleId Ezarri nahi den identifikatzailea.
	 */
	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	/**
	 * Mezuaren testua itzultzen du.
	 *
	 * @return Mezuaren testua.
	 */
	public String getTextua() {
		return textua;
	}

	/**
	 * Mezuaren testua eguneratzen du.
	 *
	 * @param textua Ezarri nahi den testua.
	 */
	public void setTextua(String textua) {
		this.textua = textua;
	}

	/**
	 * Mezuaren data itzultzen du.
	 *
	 * @return Mezuaren data.
	 */
	public Date getData() {
		return data;
	}

	/**
	 * Mezuaren data eguneratzen du.
	 *
	 * @param data Ezarri nahi den data.
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * Mezuaren igorlea itzultzen du.
	 *
	 * @return Igorlea.
	 */
	public User getIgorlea() {
		return igorlea;
	}

	/**
	 * Mezuaren igorlea eguneratzen du.
	 *
	 * @param igorlea Ezarri nahi den igorlea.
	 */
	public void setIgorlea(User igorlea) {
		this.igorlea = igorlea;
	}
}