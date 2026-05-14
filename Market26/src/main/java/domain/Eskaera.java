package domain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import gui.ErreserbatuakGUI;
import gui.MainGUIerregistratua;

/**
 * Erabiltzaile batek egiten duen produktu-eskaera irudikatzen du.
 * Beste erabiltzaileek euren produktuak (Sale) eskaini ditzakete eskaera honen barruan.
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Eskaera implements Serializable {

	// --- ATRIBUTUAK ---

	@Id
	@GeneratedValue
	private Integer eskaeraNumber;

	private String title;
	private String description;
	private Date pubDate;

	/**
	 * Eskaera egin duen erabiltzailea.
	 */
	@ManyToOne
	private Registered eskatzailea;

	/**
	 * Eskaera honi lotutako produktu-eskaintzak.
	 * ManyToMany erlazioa erabiltzen da produktu bera eskaera batean baino gehiagotan
	 * eskaini ahal izateko.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Sale> eskaintzak = new ArrayList<>();

	// --- ERAIKITZAILEAK ---

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Eskaera() {
		super();
	}

	/**
	 * Eskaera berri bat sortzen du.
	 *
	 * @param title Eskaeraren titulua.
	 * @param description Eskaeraren deskribapena.
	 * @param pubDate Argitalpen-data.
	 * @param eskatzailea Eskaera sortu duen erabiltzailea.
	 */
	public Eskaera(String title, String description, Date pubDate, Registered eskatzailea) {
		super();
		this.title = title;
		this.description = description;
		this.pubDate = pubDate;
		this.eskatzailea = eskatzailea;
	}

	// --- GETTER ETA SETTER METODOAK ---

	/**
	 * Eskaeraren identifikatzailea itzultzen du.
	 *
	 * @return Eskaeraren identifikatzailea.
	 */
	public Integer getEskaeraNumber() {
		return eskaeraNumber;
	}

	/**
	 * Eskaeraren identifikatzailea eguneratzen du.
	 *
	 * @param eskaeraNumber Ezarri nahi den identifikatzailea.
	 */
	public void setEskaeraNumber(Integer eskaeraNumber) {
		this.eskaeraNumber = eskaeraNumber;
	}

	/**
	 * Eskaeraren titulua itzultzen du.
	 *
	 * @return Eskaeraren titulua.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Eskaeraren titulua eguneratzen du.
	 *
	 * @param title Ezarri nahi den titulua.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Eskaeraren deskribapena itzultzen du.
	 *
	 * @return Eskaeraren deskribapena.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Eskaeraren deskribapena eguneratzen du.
	 *
	 * @param description Ezarri nahi den deskribapena.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Eskaeraren argitalpen-data itzultzen du.
	 *
	 * @return Argitalpen-data.
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * Eskaeraren argitalpen-data eguneratzen du.
	 *
	 * @param pubDate Ezarri nahi den data.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * Eskaera egin duen erabiltzailea itzultzen du.
	 *
	 * @return Eskatzailea.
	 */
	public Registered getEskatzailea() {
		return eskatzailea;
	}

	/**
	 * Eskaera egin duen erabiltzailea eguneratzen du.
	 *
	 * @param eskatzailea Ezarri nahi den erabiltzailea.
	 */
	public void setEskatzailea(Registered eskatzailea) {
		this.eskatzailea = eskatzailea;
	}

	/**
	 * Eskaerari lotutako eskaintzen zerrenda itzultzen du.
	 *
	 * @return Eskaintzen zerrenda.
	 */
	public List<Sale> getEskaintzak() {
		return eskaintzak;
	}

	/**
	 * Eskaerari lotutako eskaintzen zerrenda eguneratzen du.
	 *
	 * @param eskaintzak Ezarri nahi den eskaintzen zerrenda.
	 */
	public void setEskaintzak(List<Sale> eskaintzak) {
		this.eskaintzak = eskaintzak;
	}

	// --- LAGUNTZAILE METODOAK ---

	/**
	 * Eskaera bati produktu-eskaintza berri bat gehitzen dio.
	 * Produktua jadanik zerrendan badago, ez da berriro gehitzen.
	 *
	 * @param eskaintza Gehitu nahi den salmenta.
	 */
	public void addEskaintza(Sale eskaintza) {
		if (!this.eskaintzak.contains(eskaintza)) {
			this.eskaintzak.add(eskaintza);
		}
	}

	/**
	 * Eskaera batetik produktu-eskaintza bat kentzen du.
	 *
	 * @param eskaintza Kendu nahi den salmenta.
	 */
	public void removeEskaintza(Sale eskaintza) {
		this.eskaintzak.remove(eskaintza);
	}

	/*
	 * GUI probetarako edo etorkizuneko integraziorako utzitako zatia.
	 * Hemen erreserben leihoa irekitzeko botoiaren logika agertzen da.
	 *
	 * btnErreserbatuak = new JButton("Erreserbatuak");
	 * btnErreserbatuak.addActionListener(new ActionListener() {
	 *     public void actionPerformed(ActionEvent e) {
	 *         MainGUIerregistratua.this.setVisible(false);
	 *
	 *         zureErreserbakLeihoa = new ErreserbatuakGUI(erabiltzailea, MainGUIerregistratua.this);
	 *         zureErreserbakLeihoa.setVisible(true);
	 *     }
	 * });
	 *
	 * btnErreserbatuak.setBounds(308, 23, 224, 27);
	 * panela4.add(btnErreserbatuak);
	 * edukiPanela.add(panela3);
	 * Registered r = facade.getSellerByEmail(saltzaileEmaila);
	 * // btnErreserbatuak.setVisible(r.getVip()!=null);
	 * // Null balioa egon daitekeenez, GUIra itzultzean kontu handiz kudeatu behar da.
	 */
}