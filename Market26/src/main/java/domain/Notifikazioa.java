package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Notifikazioa implements Serializable {

	@Id
	@GeneratedValue
	private Integer id;

	private String mezua;
	private Date data;

	// true bada, erabiltzaileak dagoeneko irakurri du
	// false bada, notifikazioa berria da
	private boolean irakurrita;

	/**
	 * Mezua jasotzen duen erabiltzailea.
	 */
	@ManyToOne
	private Registered hartzailea;

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Notifikazioa() {
		super();
	}

	/**
	 * Notifikazio berri bat sortzen du.
	 * Sortzen den unean, notifikazioa irakurri gabe bezala markatzen da.
	 *
	 * @param mezua Notifikazioaren testua.
	 * @param hartzailea Notifikazioa jasoko duen erabiltzailea.
	 */
	public Notifikazioa(String mezua, Registered hartzailea) {
		this.mezua = mezua;
		this.hartzailea = hartzailea;
		this.data = new Date();
		this.irakurrita = false;
	}

	/**
	 * Notifikazioaren identifikatzailea itzultzen du.
	 *
	 * @return Identifikatzailea.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Notifikazioaren identifikatzailea eguneratzen du.
	 *
	 * @param id Ezarri nahi den identifikatzailea.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Notifikazioaren mezua itzultzen du.
	 *
	 * @return Mezua.
	 */
	public String getMezua() {
		return mezua;
	}

	/**
	 * Notifikazioaren mezua eguneratzen du.
	 *
	 * @param mezua Ezarri nahi den mezua.
	 */
	public void setMezua(String mezua) {
		this.mezua = mezua;
	}

	/**
	 * Notifikazioaren data itzultzen du.
	 *
	 * @return Data.
	 */
	public Date getData() {
		return data;
	}

	/**
	 * Notifikazioaren data eguneratzen du.
	 *
	 * @param data Ezarri nahi den data.
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * Notifikazioa irakurrita dagoen ala ez adierazten du.
	 *
	 * @return true irakurrita badago, false bestela.
	 */
	public boolean isIrakurrita() {
		return irakurrita;
	}

	/**
	 * Notifikazioaren irakurketa-egoera eguneratzen du.
	 *
	 * @param irakurrita Irakurketa-egoera berria.
	 */
	public void setIrakurrita(boolean irakurrita) {
		this.irakurrita = irakurrita;
	}

	/**
	 * Notifikazioaren hartzailea itzultzen du.
	 *
	 * @return Hartzailea.
	 */
	public Registered getHartzailea() {
		return hartzailea;
	}

	/**
	 * Notifikazioaren hartzailea eguneratzen du.
	 *
	 * @param hartzailea Ezarri nahi den hartzailea.
	 */
	public void setHartzailea(Registered hartzailea) {
		this.hartzailea = hartzailea;
	}
}