package domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Admin extends User {

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Kexa> kexak = new ArrayList<Kexa>();

	/**
	 * Parametrorik gabeko eraikitzailea.
	 * JPAk entitatea behar bezala instantziatu ahal izateko mantentzen da.
	 */
	public Admin() {
		super();
	}

	/**
	 * Administratzaile berri bat sortzen du.
	 *
	 * @param email Administratzailearen posta elektronikoa.
	 * @param name Administratzailearen izena.
	 * @param pass Administratzailearen pasahitza.
	 */
	public Admin(String email, String name, String pass) {
		super(email, name, pass);
	}

	/**
	 * Administratzaileari lotutako kexen zerrenda itzultzen du.
	 *
	 * @return Kexen zerrenda.
	 */
	public List<Kexa> getKexak() {
		return kexak;
	}

	/**
	 * Administratzaileari lotutako kexen zerrenda eguneratzen du.
	 *
	 * @param kexak Ezarri nahi den kexen zerrenda.
	 */
	public void setKexak(List<Kexa> kexak) {
		this.kexak = kexak;
	}

	/**
	 * Objektuaren testu-adierazpena itzultzen du.
	 *
	 * @return Objektuaren testu-adierazpena.
	 */
	@Override
	public String toString() {
		return null;
	}
}