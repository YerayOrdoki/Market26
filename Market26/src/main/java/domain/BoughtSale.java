package domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Sale klasearen antzeko klasea da.
 * Erosketaren data gordetzen du, eta erositakoen zerrendan erabiltzen da.
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class BoughtSale {

	@Id
	@GeneratedValue
	private int id;

	private Date erosketaData;

	@Embedded
	private Sale s;

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public BoughtSale() {
		super();
	}

	/**
	 * Erositako salmenta berri bat sortzen du.
	 * Erosketa-data unean bertan ezartzen da, eta jasotako salmentaren kopia bat gordetzen da.
	 *
	 * @param sale Erosi den salmenta.
	 */
	public BoughtSale(Sale sale) {
		this.erosketaData = new Date();
		this.s = new Sale(sale);
	}

	/**
	 * Erosketaren identifikatzailea itzultzen du.
	 *
	 * @return Erosketaren identifikatzailea.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Erosketaren identifikatzailea eguneratzen du.
	 *
	 * @param id Ezarri nahi den identifikatzailea.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Erosketaren data itzultzen du.
	 *
	 * @return Erosketaren data.
	 */
	public Date getErosketaData() {
		return erosketaData;
	}

	/**
	 * Erosketaren data eguneratzen du.
	 *
	 * @param erosketaData Ezarri nahi den data.
	 */
	public void setErosketaData(Date erosketaData) {
		this.erosketaData = erosketaData;
	}

	/**
	 * Gordetako salmenta itzultzen du.
	 *
	 * @return Salmenta.
	 */
	public Sale getSale() {
		return s;
	}

	/**
	 * Gordetako salmenta itzultzen du.
	 * Metodo hau aurrekoaren baliokidea da.
	 *
	 * @return Salmenta.
	 */
	public Sale getS() {
		return s;
	}

	/**
	 * Gordetako salmenta eguneratzen du.
	 *
	 * @param s Ezarri nahi den salmenta.
	 */
	public void setS(Sale s) {
		this.s = s;
	}
}