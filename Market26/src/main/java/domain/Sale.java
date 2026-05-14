package domain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Sale implements Serializable {

	// ---- ATRIBUTUAK ----

	@Id
	@GeneratedValue
	private Integer saleNumber;

	private String title;
	private String description;
	private int status;
	private float price;
	private Date pubDate;
	private String fileName;
	private boolean erreserbatuta = false;

	@XmlIDREF
	private Registered seller;

	@OneToMany(fetch = FetchType.EAGER, cascade = javax.persistence.CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<Salaketa> salaketak = new ArrayList<Salaketa>();

	// ---- ERAIKITZAILEAK ----

	/**
	 * Parametrorik gabeko eraikitzailea.
	 */
	public Sale() {
		super();
	}

	/**
	 * Salmenta berri bat sortzen du.
	 *
	 * @param title Salmentaren titulua.
	 * @param description Salmentaren deskribapena.
	 * @param status Salmentaren egoera.
	 * @param price Prezioa.
	 * @param pubDate Argitalpen-data.
	 * @param file Lotutako irudi-fitxategia.
	 * @param seller Saltzailea.
	 */
	public Sale(String title, String description, int status, float price, Date pubDate, File file, Registered seller) {
		super();
		this.title = title;
		this.description = description;
		this.status = status;
		this.price = price;
		this.pubDate = pubDate;

		if (file != null) {
			this.fileName = file.getName();
			try {
				BufferedImage img1 = ImageIO.read(file);
				String path = "src/main/resources/images/";
				File outputfile = new File(path + file.getName());
				ImageIO.write(img1, "png", outputfile);
			} catch (IOException ex) {
				// Irudia gordetzean errorea gertatuz gero, ez da tratamendu gehigarririk egiten.
			}
		}

		this.seller = seller;
	}

	/**
	 * Beste salmenta baten kopia sortzen du.
	 *
	 * @param sale Kopiatu nahi den salmenta.
	 */
	public Sale(Sale sale) {
		super();
		this.title = sale.getTitle();
		this.description = sale.getDescription();
		this.status = sale.getStatus();
		this.price = sale.getPrice();
		this.pubDate = sale.getPublicationDate();
		this.fileName = sale.getFile();
		this.seller = sale.getSeller();
	}

	// ---- GETTER ETA SETTER METODOAK ----

	/**
	 * Salmentaren identifikatzailea itzultzen du.
	 *
	 * @return Salmentaren identifikatzailea.
	 */
	public Integer getSaleNumber() {
		return saleNumber;
	}

	/**
	 * Salmentaren identifikatzailea eguneratzen du.
	 *
	 * @param saleNumber Ezarri nahi den identifikatzailea.
	 */
	public void setSaleNumber(Integer saleNumber) {
		this.saleNumber = saleNumber;
	}

	/**
	 * Salmentaren titulua itzultzen du.
	 *
	 * @return Salmentaren titulua.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Salmentaren titulua eguneratzen du.
	 *
	 * @param title Ezarri nahi den titulua.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Salmentaren deskribapena itzultzen du.
	 *
	 * @return Salmentaren deskribapena.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Salmentaren deskribapena eguneratzen du.
	 *
	 * @param description Ezarri nahi den deskribapena.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Salmentaren egoera itzultzen du.
	 *
	 * @return Salmentaren egoera.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Salmentaren egoera eguneratzen du.
	 *
	 * @param status Ezarri nahi den egoera.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Salmentaren prezioa itzultzen du.
	 *
	 * @return Prezioa.
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * Salmentaren prezioa eguneratzen du.
	 *
	 * @param price Ezarri nahi den prezioa.
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * Salmentaren argitalpen-data itzultzen du.
	 *
	 * @return Argitalpen-data.
	 */
	public Date getPublicationDate() {
		return pubDate;
	}

	/**
	 * Salmentaren argitalpen-data eguneratzen du.
	 *
	 * @param publicationDate Ezarri nahi den argitalpen-data.
	 */
	public void setPublicationDate(Date publicationDate) {
		this.pubDate = publicationDate;
	}

	/**
	 * Salmentaren argitalpen-data itzultzen du.
	 * Metodo hau aurrekoaren baliokidea da.
	 *
	 * @return Argitalpen-data.
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * Salmentaren argitalpen-data eguneratzen du.
	 *
	 * @param pubDate Ezarri nahi den argitalpen-data.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * Salmentaren saltzailea itzultzen du.
	 *
	 * @return Saltzailea.
	 */
	public Registered getSeller() {
		return seller;
	}

	/**
	 * Salmentaren saltzailea eguneratzen du.
	 *
	 * @param seller Ezarri nahi den saltzailea.
	 */
	public void setSeller(Registered seller) {
		this.seller = seller;
	}

	/**
	 * Salmentari lotutako fitxategi-izena itzultzen du.
	 *
	 * @return Fitxategi-izena.
	 */
	public String getFile() {
		return fileName;
	}

	/**
	 * Salmentari lotutako fitxategi-izena itzultzen du.
	 *
	 * @return Fitxategi-izena.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Salmentari lotutako fitxategi-izena eguneratzen du.
	 *
	 * @param fileName Ezarri nahi den fitxategi-izena.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Salmentari lotutako salaketen zerrenda itzultzen du.
	 *
	 * @return Salaketen zerrenda.
	 */
	public List<Salaketa> getSalaketak() {
		return salaketak;
	}

	/**
	 * Salmentari lotutako salaketen zerrenda eguneratzen du.
	 *
	 * @param salaketak Ezarri nahi den salaketen zerrenda.
	 */
	public void setSalaketak(List<Salaketa> salaketak) {
		this.salaketak = salaketak;
	}

	/**
	 * Produktua erreserbatuta dagoen ala ez adierazten du.
	 *
	 * @return true erreserbatuta badago, false bestela.
	 */
	public boolean isErreserbatuta() {
		return erreserbatuta;
	}

	/**
	 * Produktuaren erreserba-egoera eguneratzen du.
	 *
	 * @param erreserbatuta Ezarri nahi den egoera.
	 */
	public void setErreserbatuta(boolean erreserbatuta) {
		this.erreserbatuta = erreserbatuta;
	}

	// ---- LAGUNTZAILE METODOAK ----

	/**
	 * Salmentari salaketa berri bat gehitzen dio.
	 * Aldi berean, salaketa hori salatzailearen zerrendan ere gehitzen da.
	 *
	 * @param mota Salaketaren mota.
	 * @param sale Salatutako salmenta.
	 * @param pubDate Argitalpen-data.
	 * @param salatzailea Salaketa egin duen erabiltzailea.
	 * @return Sortutako salaketa.
	 */
	public Salaketa addSalaketa(String mota, Sale sale, Date pubDate, Registered salatzailea) {
		Salaketa salaketa = new Salaketa(mota, sale, pubDate, salatzailea);
		this.salaketak.add(salaketa);
		salatzailea.getSortutakoSalaketak().add(salaketa);
		return salaketa;
	}

	/**
	 * Salmentaren testu-adierazpena itzultzen du.
	 *
	 * @return Testu-adierazpena.
	 */
	public String toString() {
		return saleNumber + ";" + title + ";" + price;
	}
}