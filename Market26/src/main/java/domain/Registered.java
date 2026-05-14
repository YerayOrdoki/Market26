package domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Registered extends User {
	// ----ATRIBUTUAK----
	
	private double saldoa;
	private boolean isBanned;
	@OneToOne(cascade=javax.persistence.CascadeType.ALL)
	private Gps kokapena;
	
	@OneToOne(cascade=javax.persistence.CascadeType.ALL)
	private VIP vip;
	
	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	
	private List<Sale> sales = new ArrayList<Sale>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Sale> saskia = new ArrayList<Sale>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@XmlTransient
	private List<Sale> faboritoak = new ArrayList<Sale>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<BoughtSale> erositakoak = new ArrayList<BoughtSale>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<BoughtSale> saldutakoak = new ArrayList<BoughtSale>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<Erreklamazioa> erreklamazioBidaliak = new ArrayList<Erreklamazioa>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<Salaketa> sortutakoSalaketak = new ArrayList<Salaketa>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<Erreklamazioa> erreklamazioaJasoak = new ArrayList<Erreklamazioa>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlTransient
	private List<Mugimendua> mugimenduak = new ArrayList<Mugimendua>();
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "hartzailea")
	@XmlTransient
	private List<Notifikazioa> notifikazioak = new ArrayList<Notifikazioa>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "eskatzailea", orphanRemoval = true)
	@XmlTransient
	private List<Eskaera> sortutakoEskaerak = new ArrayList<>();

	// ----ERAIKITZAILEAK----
	public Registered(String email, String name, String pass) {
		super(email, name, pass);
		this.saldoa = 500;
		this.isBanned = false;
		this.vip=null;

	}

	public Registered() {
		super();
	}

	// ----METODOAK----
	public List<Erreklamazioa> getErreklamazioBidaliak() {
		return erreklamazioBidaliak;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	public void setFaboritoak(List<Sale> faboritoak) {
		this.faboritoak = faboritoak;
	}

	public void setErositakoak(List<BoughtSale> erositakoak) {
		this.erositakoak = erositakoak;
	}

	public void setSaldutakoak(List<BoughtSale> saldutakoak) {
		this.saldutakoak = saldutakoak;
	}

	public void setMugimenduak(List<Mugimendua> mugimenduak) {
		this.mugimenduak = mugimenduak;
	}

	public void setNotifikazioak(List<Notifikazioa> notifikazioak) {
		this.notifikazioak = notifikazioak;
	}

	public void setErreklamazioBidaliak(List<Erreklamazioa> erreklamazioBidaliak) {
		this.erreklamazioBidaliak = erreklamazioBidaliak;
	}

	public List<Erreklamazioa> getErreklamazioaJasoak() {
		return erreklamazioaJasoak;
	}

	public void setErreklamazioaJasoak(List<Erreklamazioa> erreklamazioaJasoak) {
		this.erreklamazioaJasoak = erreklamazioaJasoak;
	}

	public void setSaldoa(double s) {
		this.saldoa = s;
	}

	public double getSaldoa() {
		return saldoa;
	}

	public List<Mugimendua> getMugimenduak() {
		return mugimenduak;
	}

	public List<BoughtSale> getErositakoak() {

		return erositakoak;
	}

	public List<BoughtSale> getSaldutakoak() {

		return saldutakoak;
	}

	public List<Sale> getSales() {
		return sales;
	}

	public List<Sale> getFaboritoak() {
		return faboritoak;
	}

	public List<Notifikazioa> getNotifikazioak() {
		return notifikazioak;

	}

	public List<Salaketa> getSortutakoSalaketak() {
		return sortutakoSalaketak;
	}

	public void setSortutakoSalaketak(List<Salaketa> sortutakoSalaketak) {
		this.sortutakoSalaketak = sortutakoSalaketak;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	
	public Gps getKokapena() {
		return kokapena;
	}

	public void setKokapena(Gps kokapena) {
		this.kokapena = kokapena;
	}

	/**
	 * This method creates/adds a sale to a seller
	 * 
	 * @param title           of the sale
	 * @param description     of the sale
	 * @param status
	 * @param selling         price
	 * @param publicationDate
	 * @return Sale
	 */
	public Sale addSale(String title, String description, int status, float price, Date pubDate, File file) {

		Sale sale = new Sale(title, description, status, price, pubDate, file, this);
		sales.add(sale);
		return sale;
	}

	/**
	 * This method checks if the ride already exists for that driver
	 * 
	 * @param from the origin location
	 * @param to   the destination location
	 * @param date the date of the ride
	 * @return true if the ride exists and false in other case
	 */
	public boolean doesSaleExist(String title) {
		for (Sale s : sales)
			if (s.getTitle() != null && s.getTitle().compareTo(title) == 0)
				return true;
		return false;
	}

	public Erreklamazioa addErreklamazioaJasoak(String title, Date pubDate, Registered bidali, Registered jaso,
			BoughtSale produktoa) {
		Erreklamazioa erreklamazioa = new Erreklamazioa(title, pubDate, bidali, jaso, produktoa);
		this.erreklamazioaJasoak.add(erreklamazioa);
		return erreklamazioa;
	}

	public Erreklamazioa addErreklamazioaBidaliak(String title, Date pubDate, Registered bidali, Registered jaso,
			BoughtSale produktoa) {
		Erreklamazioa erreklamazioa = new Erreklamazioa(title, pubDate, bidali, jaso, produktoa);
		this.erreklamazioBidaliak.add(erreklamazioa);
		return erreklamazioa;
	}

	public void addFaboritoa(Sale faboritoa) {
		this.getFaboritoak().add(faboritoa);
	}

	public void addErositakoa(Sale erositakoa) {
		this.getErositakoak().add(new BoughtSale(erositakoa));
	}

	public void addSortutakoSalaketa(Salaketa salaketa) {
		this.sortutakoSalaketak.add(salaketa);
	}

	public void removeErreklamazioaJasoak(Erreklamazioa erreklamazioa) {
		this.getErreklamazioaJasoak().remove(erreklamazioa);
	}

	public void removeErreklamazioBidaliak(Erreklamazioa erreklamazioa) {
		this.getErreklamazioBidaliak().remove(erreklamazioa);
	}

	public void removeFaboritoa(Sale faboritoa) {
		this.getFaboritoak().remove(faboritoa);
	}

	public void DiruaSartu(double dirua) {
		this.saldoa += dirua;
	}

	public boolean DiruaAtera(double dirua) {
		double kantBerria = this.saldoa -= dirua;
		if (kantBerria < 0) {
			this.saldoa += dirua;
			return false;
		}
		return true;
	}

	public String toString() {
		return getEmail() + ";" + getName() + getSales();
	}

	public VIP getVip() {
		return vip;
	}

	public void setVip(VIP vip) {
		this.vip = vip;
	}
	public List<Eskaera> getSortutakoEskaerak() {
	    return sortutakoEskaerak;
	}

	public void setSortutakoEskaerak(List<Eskaera> sortutakoEskaerak) {
	    this.sortutakoEskaerak = sortutakoEskaerak;
	}
	/**
	 * Erabiltzailearen eskaeren zerrendara eskaera berri bat gehitzen du.
	 */
	public void addEskaera(Eskaera eskaera) {
	    this.sortutakoEskaerak.add(eskaera);
	}
	
	public List<Sale> getSaskia() {
		return saskia;
	}
	
	public void setSaskia(List<Sale> saskia) {
		this.saskia = saskia;
	}
	
	public void removeSaskitik(Sale sale) {
		this.getSaskia().remove(sale);
	}
	
	public void addSaskira(Sale sale) {
		this.getSaskia().add(sale);
	}
}
