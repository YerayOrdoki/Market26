package dataAccess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.jws.WebMethod;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Admin;
import domain.BoughtSale;
import domain.DisputaMezua;
import domain.Erreklamazioa;
import domain.Eskaera;
import domain.Gps;
import domain.Mugimendua;
import domain.Notifikazioa;
import domain.Registered;
import domain.Salaketa;
import domain.Sale;
import domain.SaleSellerBoughtContainer;
import domain.User;
import domain.VIP;
import exceptions.ErreklamazioaAlreadyExistException;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

/**
 * ObjectDb datu-basearekiko datu-atzipena kudeatzen duen klasea.
 */
public class DataAccess {

	// --- ATRIBUTUAK ---
	private EntityManager db;
	private EntityManagerFactory emf;
	private static final int OINARRI_TAMAINA = 160;
	private static final String OINARRI_BIDEA = "src/main/resources/images/";
	private boolean hasieratu = false;

	ConfigXML konfigurazioa = ConfigXML.getInstance();

	// --- ERAIKITZAILEAK ---

	/**
	 * DataAccess klasearen eraikitzaile lehenetsia.
	 * Datu-basearen konexioa ireki eta, beharrezkoa bada, hasieratu egiten du.
	 */
	public DataAccess() {
		if (hasieratu) {
			if (konfigurazioa.isDatabaseInitialized()) {
				String fitxategiIzena = konfigurazioa.getDbFilename();
				File ezabatzekoFitxategia = new File(fitxategiIzena);

				if (ezabatzekoFitxategia.delete()) {
					File ezabatzekoFitxategiTenporala = new File(fitxategiIzena + "$");
					ezabatzekoFitxategiTenporala.delete();
					System.out.println("Fitxategia ezabatu da");
				} else {
					System.out.println("Eragiketak huts egin du");
				}
			}
			open();
			if (konfigurazioa.isDatabaseInitialized()) {
				initializeDB();
			}
			System.out.println("DataAccess sortua => isDatabaseLocal: " + konfigurazioa.isDatabaseLocal()
					+ " isDatabaseInitialized: " + konfigurazioa.isDatabaseInitialized());
			close();
		}
	}

	/**
	 * Datu-baseko kudeatzailea parametro bezala jasotzen duen eraikitzailea.
	 *
	 * @param db EntityManager instantzia.
	 */
	public DataAccess(EntityManager db) {
		this.db = db;
	}

	// --- DATU-BASEA IREKI, ITXI ETA HASIERATZEKO METODOAK ---

	/**
	 * Datu-baseko konexioa irekitzen du.
	 */
	public void open() {
		String fitxategiIzena = konfigurazioa.getDbFilename();
		if (konfigurazioa.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fitxategiIzena);
			db = emf.createEntityManager();
		} else {
			Map<String, String> propietateak = new HashMap<String, String>();
			propietateak.put("javax.persistence.jdbc.user", konfigurazioa.getUser());
			propietateak.put("javax.persistence.jdbc.password", konfigurazioa.getPassword());

			emf = Persistence.createEntityManagerFactory("objectdb://" + konfigurazioa.getDatabaseNode() + ":"
					+ konfigurazioa.getDatabasePort() + "/" + fitxategiIzena, propietateak);
			db = emf.createEntityManager();
		}
		System.out.println("DataAccess irekia => isDatabaseLocal: " + konfigurazioa.isDatabaseLocal());
	}

	/**
	 * Datu-baseko konexioa ixten du.
	 */
	public void close() {
		db.close();
		System.out.println("DataAcess itxita");
	}

	/**
	 * Datu-basea hasieratzen du produktu eta saltzaile batzuekin.
	 * BLFacadeImplementation-ek deitzen du config.xml fitxategian "initialize" aukera zehaztuta badago.
	 */
	public void initializeDB() {
		db.getTransaction().begin();
		try {
			Registered saltzailea1 = new Registered("seller1@gmail.com", "Aitor Fernandez", "aurrera");
			saltzailea1.setKokapena(new Gps("Calle de Fuenterrabía, 2,", "20005", "España"));
			Registered saltzailea2 = new Registered("seller22@gmail.com", "Ane Gaztañaga", "aurrera");
			saltzailea2.setKokapena(new Gps("Paseo de La Concha, 12,", "20007", "España"));

			Registered saltzailea3 = new Registered("seller3@gmail.com", "Test Seller", "aurrera");
			saltzailea3.setKokapena(new Gps("Avenida de la Libertad, 1,", "20004", "España"));

			Registered saltzailea4 = new Registered("a", "a", "a");
			saltzailea4.setKokapena(new Gps("Calle 31 de Agosto, 25,", "20003", "España"));

			Registered saltzailea5 = new Registered("b", "b", "b");
			saltzailea5.setKokapena(new Gps("Paseo de Miraconcha, 28,", "20007", "España"));

			Admin administratzailea1 = new Admin("admin1", "admin1", "123");

			Date gaur = UtilDate.trim(new Date());

			saltzailea1.addSale("futbol baloia", "oso polita, gutxi erabilita", 2, 10, gaur, null);
			saltzailea1.addSale("salomon mendiko botak", "44 zenbakia, 3 ateraldi", 2, 20, gaur, null);
			saltzailea1.addSale("samsung 42\" telebista", "berria, erabili gabe", 1, 175, gaur, null);

			saltzailea2.addSale("imac 27", "7 urte, dena ondo dabil", 1, 200, gaur, null);
			saltzailea2.addSale("iphone 17", "oso gutxi erabilita", 2, 400, gaur, null);
			saltzailea2.addSale("orbea mendiko bizikleta", "29\" 10 urte, mantenua behar du", 3, 225, gaur, null);
			saltzailea2.addSale("polar kilor erlojua", "Vantage M, ondo dago", 3, 30, gaur, null);

			saltzailea3.addSale("sukaldeko mahaia", "1.8*0.8, 4 aulkiekin. Prezio finkoa", 3, 45, gaur, null);
			saltzailea4.addSale("a", "oso polita, gutxi erabilita", 2, 10, gaur, null);

			db.persist(saltzailea1);
			db.persist(saltzailea2);
			db.persist(saltzailea3);
			db.persist(saltzailea4);
			db.persist(saltzailea5);
			db.persist(administratzailea1);

			db.getTransaction().commit();
			System.out.println("Datu-basea hasieratu da");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// --- ERABILTZAILEEN METODOAK ---

	/**
	 * Erabiltzaile bat datu-basean gehitzen du.
	 *
	 * @param email Erabiltzailearen posta elektronikoa.
	 * @param izenAbizena Erabiltzailearen izen-abizenak.
	 * @param password Erabiltzailearen pasahitza.
	 * @return Registered objektua, edo null erabiltzailea jadanik existitzen bada.
	 */
	public Registered erabiltzaileaSortu(String email, String izenAbizena, String password) {
		Registered erabiltzailea;
		try {
			db.getTransaction().begin();
			erabiltzailea = new Registered(email, izenAbizena, password);
			db.persist(erabiltzailea);
			db.getTransaction().commit();
			System.out.println("Erabiltzailea gehituta");
		} catch (Exception e) {
			erabiltzailea = null;
		}
		return erabiltzailea;
	}

	/**
	 * Saioa hasteko datuak egiaztatzen ditu.
	 *
	 * @param email Erabiltzailearen posta.
	 * @param pass Erabiltzailearen pasahitza.
	 * @return User objektua baldin badago, null bestela.
	 */
	public User isLogged(String email, String pass) {
		User erabiltzailea = db.find(User.class, email);
		if (erabiltzailea != null) {
			boolean pasahitzaZuzena = erabiltzailea.isPassCorrect(pass);
			if (!pasahitzaZuzena) {
				erabiltzailea = null;
			}
		}
		return erabiltzailea;
	}

	/**
	 * Erabiltzaile bat izenaren bidez bilatzen du.
	 *
	 * @param izena Bilatu beharreko izena.
	 * @return Registered objektua.
	 */
	public Registered getSellerByName(String izena) {
		TypedQuery<Registered> query = db.createQuery("SELECT s FROM Registered s WHERE s.name='" + izena + "'",
				Registered.class);
		List<Registered> saltzaileak = query.getResultList();
		if (saltzaileak.isEmpty()) {
			return null;
		} else {
			return (Registered) saltzaileak.get(0);
		}
	}

	/**
	 * Erabiltzaile bat postaren bidez bilatzen du.
	 *
	 * @param email Bilatu beharreko posta.
	 * @return Registered objektua.
	 */
	public Registered getSellerByEmail(String email) {
		db.getTransaction().begin();
		Registered saltzailea = db.find(Registered.class, email);
		db.getTransaction().commit();
		return saltzailea;
	}

	// --- FABORITOEN ETA EROSITAKOEN KUDEAKETA ---

	/**
	 * Erabiltzaile baten faboritoen zerrendan salmenta bat gehitzen du.
	 *
	 * @param produktoaNumber Gehitu nahi den salmentaren identifikadorea.
	 * @param saltzaileaId Faboritoa gehituko zaion erabiltzailearen posta.
	 * @return true faboritoa gehitu bada, false bestela.
	 */
	@WebMethod
	public boolean faboritoaGehitu(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		Sale faboritoKudeatua = db.find(Sale.class, produktoaNumber);
		if (erabiltzailea != null) {
			erabiltzailea.addFaboritoa(faboritoKudeatua);
			db.getTransaction().commit();
			System.out.println("Faboritoa gehituta");
			return true;
		}
		return false;
	}

	/**
	 * Erabiltzaile baten faboritoen zerrendatik salmenta bat ezabatzen du.
	 *
	 * @param produktoaNumber Ezabatu nahi den salmentaren identifikadorea.
	 * @param saltzaileaId Faboritoa kenduko zaion erabiltzailearen posta.
	 * @return true ondo ezabatu bada.
	 */
	@WebMethod
	public boolean faboritoaEzabatu(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		Sale faboritoKudeatua = db.find(Sale.class, produktoaNumber);
		if (erabiltzailea != null) {
			erabiltzailea.removeFaboritoa(faboritoKudeatua);
		}
		db.getTransaction().commit();
		System.out.println("Faboritoa ezabatuta");
		return true;
	}

	/**
	 * Egiaztatzen du ea produktu bat erabiltzaile baten faboritoetan dagoen.
	 *
	 * @param produktoaNumber Produktuaren identifikadorea.
	 * @param saltzaileaId Erabiltzailearen posta.
	 * @return true faboritoa bada, false bestela.
	 */
	public boolean faboritoaDa(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();

		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		boolean existitzenDa = false;

		for (Sale faboritoa : erabiltzailea.getFaboritoak()) {
			if (faboritoa.getSaleNumber().equals(produktoaNumber)) {
				existitzenDa = true;
				break;
			}
		}

		db.getTransaction().commit();
		System.out.println(existitzenDa);
		return existitzenDa;
	}

	/**
	 * Erabiltzaile baten faboritoen zerrenda lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen posta.
	 * @return Faboritoen salmenten zerrenda.
	 */
	public List<Sale> getFaboritoLista(String saltzaileaId) {
		TypedQuery<Sale> query = db
				.createQuery("SELECT f FROM Registered s JOIN s.faboritoak f WHERE s.email = :sellerEmail", Sale.class);
		query.setParameter("sellerEmail", saltzaileaId);
		List<Sale> faboritoak = query.getResultList();
		return faboritoak;
	}

	/**
	 * Erositako produktu bat erabiltzailearen erositakoen zerrendara gehitzen du.
	 *
	 * @param produktoaNumber Gehitu nahi den produktuaren identifikadorea.
	 * @param saltzaileaId Eroslearen posta.
	 * @return true ondo gehitu bada, false bestela.
	 */
	@WebMethod
	public boolean erositakoakGehitu(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, produktoaNumber);
		Sale erositakoKudeatua = db.find(Sale.class, produktoaNumber);
		if (erabiltzailea != null) {
			erabiltzailea.addErositakoa(erositakoKudeatua);
			System.out.println("Erositakoa gehituta");
			db.getTransaction().commit();
			return true;
		}
		db.getTransaction().commit();
		return false;
	}

	/**
	 * Erabiltzaile batek erosi dituen produktuen zerrenda lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen posta.
	 * @return Erositakoen zerrenda.
	 */
	public List<BoughtSale> getErositakoLista(String saltzaileaId) {
		TypedQuery<BoughtSale> query = db.createQuery(
				"SELECT f FROM Registered s JOIN s.erositakoak f WHERE s.email = :sellerEmail", BoughtSale.class);
		query.setParameter("sellerEmail", saltzaileaId);
		List<BoughtSale> erositakoak = query.getResultList();
		return erositakoak;
	}

	// --- EROSKETA ETA SALMENTEN METODOAK ---

	/**
	 * Erabiltzaile batek produktu bat erosten duenean burutzen den logika osoa.
	 * Erosleari dirua kentzen zaio, saltzaileari dirua ematen zaio, datu-baseko
	 * salgaien zerrendatik produktua kentzen da eta erosien zerrendara gehitzen da.
	 *
	 * @param produktoaNumber Erosi beharreko produktuaren IDa.
	 * @param saltzaileaId Eroslearen posta.
	 * @return Erositako produktua.
	 */
	public boolean produktoaErosi(Integer produktoaNumber, String saltzaileaId, boolean bidalketaOrdaindu) {
		Sale eproduktoa = db.find(Sale.class, produktoaNumber);
		Sale itzuli = new Sale(eproduktoa);
		db.getTransaction().begin();
		boolean erosiDa = false;
		Registered erosle = db.find(Registered.class, saltzaileaId);

		Registered saltzaile = db.find(Registered.class, eproduktoa.getSeller().getEmail());

		if (erosle != null) {
			eproduktoa = db.find(Sale.class, produktoaNumber);
			boolean erosDezake = true;
			if (eproduktoa.isErreserbatuta()) {
				VIP vip = erosle.getVip();
				if (vip != null && vip.daukaErreserba(eproduktoa)) {
					erosDezake = true;
				} else {
					erosDezake = false;
				}
			}
			if (erosDezake) {
				boolean vipDaa = erosle.getVip() != null;
				Double bidalketaGastuak = (bidalketaOrdaindu && !vipDaa)
						? erosle.getKokapena().bidalketaGastuakKalkulatu(eproduktoa.getSeller().getKokapena())
						: 0.0;
				if (!(erosle.getSaldoa() < (eproduktoa.getPrice() + bidalketaGastuak))) {
					saldoakManeiatu(erosle, saltzaile, eproduktoa, bidalketaGastuak);
					erosiDa = true;
					BoughtSale boughtSale = new BoughtSale(eproduktoa);
					db.persist(boughtSale);
					erosle.getErositakoak().add(boughtSale);
					erosle.getMugimenduak().add(new Mugimendua(boughtSale, 0));
					if (bidalketaGastuak != 0.0) {
						erosle.getMugimenduak().add(new Mugimendua(bidalketaGastuak, 0));
					}

					Sale ezabatzekoSalmenta = null;
					for (Sale s : saltzaile.getSales()) {
						if (s.getSaleNumber().equals(eproduktoa.getSaleNumber())) {
							ezabatzekoSalmenta = s;
							break;
						}
					}
					if (ezabatzekoSalmenta != null) {
						saltzaile.getSales().remove(ezabatzekoSalmenta);

						saltzaile.getSaldutakoak().add(boughtSale);
						saltzaile.getMugimenduak().add(new Mugimendua(boughtSale, 1));

						List<Eskaera> ezabatzekoEskaerak = new ArrayList<>();
						for (Eskaera eskaera : erosle.getSortutakoEskaerak()) {
							if (eskaera.getEskaintzak().contains(eproduktoa)) {
								ezabatzekoEskaerak.add(eskaera);
							}
						}
						for (Eskaera eskaera : ezabatzekoEskaerak) {
							erosle.getSortutakoEskaerak().remove(eskaera);
							db.remove(eskaera);
							System.out.println(">> DataAccess: Eskaera automatikoki ezabatuta erosketa egitean: "
									+ eskaera.getTitle());
						}

						TypedQuery<Eskaera> queryEskaerak = db.createQuery(
								"SELECT e FROM Eskaera e WHERE :sale MEMBER OF e.eskaintzak", Eskaera.class);
						queryEskaerak.setParameter("sale", eproduktoa);
						for (Eskaera eskaera : queryEskaerak.getResultList()) {
							eskaera.getEskaintzak().remove(eproduktoa);
						}

						Sale saskitikKenduBeharko = null;
						for (Sale saskia : erosle.getSaskia()) {
							if (saskia.getSaleNumber().equals(eproduktoa.getSaleNumber())) {
								saskitikKenduBeharko = saskia;
								break;
							}
						}
						if (saskitikKenduBeharko != null) {
							erosle.getSaskia().remove(saskitikKenduBeharko);
						}

						db.remove(eproduktoa);
						System.out.println("Produktua datu-basetik ezabatu da");
					}
				} else {
					System.out.println("Ez dago saldo nahikoa");
				}
			}
		}
		db.getTransaction().commit();
		notifikazioaBidali(saltzaile.getEmail(), eproduktoa.getTitle() + " produktoa saldu duzu.");
		System.out.println(saltzaile.getSales());
		if (itzuli == null || !erosiDa) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Erreklamazio bati dagokion jatorrizko salmenta (BoughtSale) lortzen du.
	 */
	public BoughtSale lortuErositakoSalmenta(String erosleEmail, String title) {
		try {
			TypedQuery<BoughtSale> query = db.createQuery(
					"SELECT bs FROM Registered r JOIN r.erositakoak bs WHERE r.email = :email AND bs.s.title = :title",
					BoughtSale.class);
			query.setParameter("email", erosleEmail);
			query.setParameter("title", title);

			List<BoughtSale> emaitzak = query.getResultList();
			if (!emaitzak.isEmpty()) {
				return emaitzak.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Eroslearen eta saltzailearen saldoak eguneratzen ditu.
	 *
	 * @param eroslea Eroslea.
	 * @param saltzailea Saltzailea.
	 * @param erositakoProduktua Erositako salmenta.
	 */
	private void saldoakManeiatu(Registered eroslea, Registered saltzailea, Sale erositakoProduktua,
			Double bidalGas) {
		System.out.println("Oraingo saldoa eroslea: " + eroslea.getSaldoa());
		eroslea.setSaldoa(eroslea.getSaldoa() - erositakoProduktua.getPrice() - bidalGas);
		System.out.println("Saldo berria eroslea: " + eroslea.getSaldoa());

		System.out.println("Oraingo saldoa saltzailea: " + saltzailea.getSaldoa());
		saltzailea.setSaldoa(saltzailea.getSaldoa() + erositakoProduktua.getPrice());
		System.out.println("Saldo berria saltzailea: " + saltzailea.getSaldoa());
	}

	/**
	 * Erabiltzaile batek salgai dituen produktuak eskuratzen ditu
	 * bilaketa testua eta data kontuan hartuta.
	 *
	 * @param saltzaileaId Erabiltzailearen posta.
	 * @param bilaketaTestua Bilaketarako iragazkia.
	 * @param data Data muga.
	 * @return Salmenten zerrenda.
	 */
	public List<Sale> getSalesBySeller(String saltzaileaId, String bilaketaTestua, Date data) {
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Registered sel JOIN sel.sales s "
				+ "WHERE sel.email = :email " + "AND s.title LIKE :query " + "AND s.pubDate <= :date", Sale.class);

		query.setParameter("email", saltzaileaId);
		query.setParameter("query", "%" + bilaketaTestua + "%");
		query.setParameter("date", data);

		return query.getResultList();
	}

	/**
	 * Erabiltzaile batek dagoeneko saldu dituen produktuak eskuratzen ditu.
	 *
	 * @param saltzaileaId Erabiltzailearen posta.
	 * @param bilaketaTestua Bilaketarako iragazkia.
	 * @param data Data muga.
	 * @return Saldutakoen zerrenda.
	 */
	public List<BoughtSale> getSoldBySeller(String saltzaileaId, String bilaketaTestua, Date data) {
		TypedQuery<BoughtSale> query = db.createQuery(
				"SELECT f FROM Registered s JOIN s.saldutakoak f WHERE s.email = :saltzaileaId", BoughtSale.class);
		query.setParameter("saltzaileaId", saltzaileaId);
		List<BoughtSale> erositakoak = query.getResultList();
		System.out.println(erositakoak);
		return erositakoak;
	}

	/**
	 * Erabiltzaile batek mugimendu ekonomikoen zerrenda lortzen du bilaketa testua
	 * aplikatuz.
	 *
	 * @param saltzaileaId Erabiltzailearen posta.
	 * @param bilaketaTestua Bilaketarako iragazkia.
	 * @param data Data muga.
	 * @return Mugimenduen zerrenda.
	 */
	public List<Mugimendua> getMugimenduakBySeller(String saltzaileaId, String bilaketaTestua, Date data) {
		TypedQuery<Mugimendua> query = db.createQuery("SELECT m FROM Registered sel " + "JOIN sel.mugimenduak m "
				+ "LEFT JOIN m.produktua bs " + "LEFT JOIN bs.s sale " + "WHERE sel.email = :email "
				+ "AND (bs IS NULL OR sale.title LIKE :query) " + "AND m.data <= :date", Mugimendua.class);

		query.setParameter("email", saltzaileaId);
		query.setParameter("query", "%" + bilaketaTestua + "%");
		query.setParameter("date", data);

		return query.getResultList();
	}

	/**
	 * Saltzaile baten salmenta ezabatzen du datu-basetik.
	 *
	 * @param produktoaNumber Ezabatu nahi den salmentaren IDa.
	 * @param saltzaileaId Saltzailearen posta.
	 * @return true ezabaketa ondo egin bada, false bestela.
	 */
	public boolean removeSale(Integer produktoaNumber, String saltzaileaId) {
		try {
			db.getTransaction().begin();

			Registered saltzailea = db.find(Registered.class, saltzaileaId);
			Sale ezabatzekoSalmenta = db.find(Sale.class, produktoaNumber);

			if (saltzailea != null && ezabatzekoSalmenta != null) {
				saltzailea.getSales().remove(ezabatzekoSalmenta);
				db.remove(ezabatzekoSalmenta);
				db.getTransaction().commit();
				System.out.println(">> DataAccess: Ezabaketa ondo eman da");
				return true;
			}
			db.getTransaction().commit();
			return false;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return false;
		}
	}

	// --- DIRUAREN KUDEAKETA ---

	/**
	 * Erabiltzaile baten kontutik dirua ateratzen du
	 * saldoa eguneratuz eta mugimendua gordez.
	 *
	 * @param kantitatea Atera nahi den diru kopurua.
	 * @param userId Erabiltzailearen posta.
	 * @return true operazioa ondo burutu bada.
	 */
	public boolean DiruaAtera(Double kantitatea, String userId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, userId);
		boolean ondo = erabiltzailea.DiruaAtera(kantitatea);

		if (ondo) {
			Mugimendua mugimendua = new Mugimendua(kantitatea, 0);
			db.persist(mugimendua);
			erabiltzailea.getMugimenduak().add(mugimendua);
		}

		db.getTransaction().commit();
		return ondo;
	}

	/**
	 * Erabiltzaile baten kontuan dirua sartzen du
	 * saldoa eguneratuz eta mugimendua gordez.
	 *
	 * @param kantitatea Sartu nahi den diru kopurua.
	 * @param userId Erabiltzailearen posta.
	 */
	public void DiruaSartu(Double kantitatea, String userId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, userId);
		erabiltzailea.DiruaSartu(kantitatea);

		Mugimendua mugimendua = new Mugimendua(kantitatea, 1);
		db.persist(mugimendua);
		erabiltzailea.getMugimenduak().add(mugimendua);

		db.getTransaction().commit();
	}

	/**
	 * Salmenta (produktu) berri bat sortzen du eta saltzailearen zerrendan gehitzen du.
	 *
	 * @param title Produktuaren titulua.
	 * @param description Produktuaren deskribapena.
	 * @param status Egoera.
	 * @param price Prezioa.
	 * @param pubDate Argitalpen data.
	 * @param sellerEmail Saltzailearen posta.
	 * @param file Irudi fitxategia.
	 * @return Sortutako Sale objektua.
	 */
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail,
			File file) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {

		System.out.println(">> DataAccess: createProduct=> title= " + title + " seller=" + sellerEmail);
		try {

			if (pubDate.before(UtilDate.trim(new Date()))) {
				throw new MustBeLaterThanTodayException(
						ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
			}
			if (file == null) {
				throw new FileNotUploadedException(
						ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorFileNotUploadedException"));
			}

			db.getTransaction().begin();

			Registered saltzailea = db.find(Registered.class, sellerEmail);
			if (saltzailea.doesSaleExist(title)) {
				db.getTransaction().commit();
				throw new SaleAlreadyExistException(
						ResourceBundle.getBundle("Etiquetas").getString("DataAccess.SaleAlreadyExist"));
			}

			Sale salmenta = saltzailea.addSale(title, description, status, price, pubDate, file);

			db.persist(saltzailea);
			db.getTransaction().commit();
			System.out.println("sale stored " + salmenta + " " + saltzailea);
			return salmenta;
		} catch (NullPointerException e) {
			e.printStackTrace();
			db.getTransaction().commit();
			return null;
		}
	}

	/**
	 * Tituluan testu zehatz bat daukaten salmentak bilatzen ditu.
	 *
	 * @param deskribapena Bilatu nahi den testua.
	 * @return Salmenten zerrenda.
	 */
	public List<Sale> getSales(String deskribapena) {
		System.out.println(">> DataAccess: getProducts=> from= " + deskribapena);

		List<Sale> emaitza = new ArrayList<Sale>();
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1", Sale.class);
		query.setParameter(1, "%" + deskribapena + "%");

		List<Sale> salmentak = query.getResultList();
		for (Sale salmenta : salmentak) {
			emaitza.add(salmenta);
		}
		return emaitza;
	}

	/**
	 * Tituluan testu zehatz bat daukaten eta argitalpen-data muga baten barruan
	 * dauden salmentak bilatzen ditu.
	 *
	 * @param deskribapena Bilatu nahi den testua.
	 * @param argitalpenData Data muga.
	 * @return Salmenten zerrenda.
	 */
	public List<Sale> getPublishedSales(String deskribapena, Date argitalpenData) {
		System.out.println(">> DataAccess: getProducts=> from= " + deskribapena);

		List<Sale> emaitza = new ArrayList<Sale>();
		TypedQuery<Sale> query = db.createQuery(
				"SELECT s FROM Registered r JOIN r.sales s WHERE s.title LIKE ?1 AND s.pubDate <=?2", Sale.class);
		query.setParameter(1, "%" + deskribapena + "%");
		query.setParameter(2, argitalpenData);
		System.out.println(query.toString());

		
		List<Sale> salmentak = query.getResultList();
		for (Sale salmenta : salmentak) {
			if (!salmenta.getSeller().isBanned()) {
				emaitza.add(salmenta);
			}
		}
		System.out.println(emaitza.toString());
		return emaitza;
	}

	// --- IRUDIEN KUDEAKETA ---

	/**
	 * Fitxategi baten irudia eskuratzen du karpeta lokaletik.
	 *
	 * @param fitxategiIzena Irudiaren izena.
	 * @return Irudia BufferedImage formatuan.
	 */
	public BufferedImage getFile(String fitxategiIzena) {
		File fitxategia = new File(OINARRI_BIDEA + fitxategiIzena);
		BufferedImage helburuIrudia = null;
		try {
			helburuIrudia = rescale(ImageIO.read(fitxategia));
		} catch (IOException ex) {
		}
		return helburuIrudia;
	}

	/**
	 * Irudi bat eskalatzeko metodo laguntzailea.
	 *
	 * @param jatorrizkoIrudia Eskalatu nahi den irudia.
	 * @return Eskalatutako irudia.
	 */
	public BufferedImage rescale(BufferedImage jatorrizkoIrudia) {
		System.out.println("rescale " + jatorrizkoIrudia);
		BufferedImage eskalatutakoIrudia = new BufferedImage(OINARRI_TAMAINA, OINARRI_TAMAINA,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D grafikoak = eskalatutakoIrudia.createGraphics();
		grafikoak.drawImage(jatorrizkoIrudia, 0, 0, OINARRI_TAMAINA, OINARRI_TAMAINA, null);
		grafikoak.dispose();
		return eskalatutakoIrudia;
	}

	// --- ERREKLAMAZIOEN KUDEAKETA ---

	/**
	 * Erreklamazio bat bilatzen du eroslearen/saltzailearen postaren eta
	 * produktuaren tituluaren arabera.
	 *
	 * @param userEmail Erabiltzailearen posta (eroslea ala saltzailea).
	 * @param title Produktuaren titulua.
	 * @return Erreklamazioa objektua, edo null ez bada existitzen.
	 */
	public Erreklamazioa lortuErreklamazioa(String userEmail, String title) {
		try {
			TypedQuery<Erreklamazioa> query = db.createQuery(
					"SELECT e FROM Erreklamazioa e WHERE (e.eroslea.email = :email OR e.saltzailea.email = :email) AND e.title = :title",
					Erreklamazioa.class);
			query.setParameter("email", userEmail);
			query.setParameter("title", title);

			List<Erreklamazioa> emaitzak = query.getResultList();

			if (!emaitzak.isEmpty()) {
				return emaitzak.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Erreklamazio berri bat sortzen du eta erabiltzaileen zerrendetan
	 * (bidaliak eta jasoak) gordetzen du.
	 *
	 * @param title Erreklamatutako produktuaren titulua.
	 * @param deskribapena Erreklamazioa sortzen den testua.
	 * @param sortzaileEmaila Erreklamazioa sortzen duenaren posta.
	 * @param jasotzaileEmaila Erreklamazioa jasotzen duenaren posta.
	 * @param pubDate Erreklamazioaren data.
	 * @param produktoa Erositako produktua.
	 */
	public void ErreklamazioaSortu(String title, String deskribapena, String sortzaileEmaila,
			String jasotzaileEmaila, Date pubDate, BoughtSale produktoa)
			throws MustBeLaterThanTodayException, ErreklamazioaAlreadyExistException {

		if (pubDate.before(UtilDate.trim(new Date()))) {
			throw new MustBeLaterThanTodayException(
					ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
		}

		try {
			db.getTransaction().begin();

			Registered erreklamazioSortzailea = db.find(Registered.class, sortzaileEmaila);
			Registered erreklamazioJasotzailea = db.find(Registered.class, jasotzaileEmaila);

			if (lortuErreklamazioa(sortzaileEmaila, title) != null) {
				db.getTransaction().commit();
				throw new ErreklamazioaAlreadyExistException("JADANIK ERREKLAMAZIOA EXISTITZEN DA");
			}

			Erreklamazioa erreklamazioa = new Erreklamazioa(title, pubDate, erreklamazioSortzailea,
					erreklamazioJasotzailea, produktoa);
			db.persist(erreklamazioa);

			DisputaMezua lehenMezua = new DisputaMezua(deskribapena, erreklamazioSortzailea);
			db.persist(lehenMezua);
			erreklamazioa.getMezuak().add(lehenMezua);

			erreklamazioSortzailea.getErreklamazioBidaliak().add(erreklamazioa);
			erreklamazioJasotzailea.getErreklamazioaJasoak().add(erreklamazioa);

			db.getTransaction().commit();

			notifikazioaBidali(jasotzaileEmaila, "Erreklamazio berri bat daukazu '" + title + "' produktuagatik.");
			System.out.println("Erreklamazioa sortua lehen mezuarekin! ID: " + erreklamazioa.getKexaNumber());

		} catch (ErreklamazioaAlreadyExistException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Erreklamazio baten hariari mezu berri bat gehitzen dio.
	 *
	 * @param idErreklamazioa Erreklamazioaren IDa.
	 * @param emailEmisor Mezua bidaltzen duenaren posta.
	 * @param testua Mezuaren testua.
	 * @return Eguneratutako Erreklamazioa objektua.
	 */
	public Erreklamazioa disputaMezuaGehitu(Integer idErreklamazioa, String emailEmisor, String testua) {
		db.getTransaction().begin();

		Erreklamazioa erreklamazioa = db.find(Erreklamazioa.class, idErreklamazioa);
		Registered igorlea = db.find(Registered.class, emailEmisor);

		if (erreklamazioa != null && igorlea != null) {
			DisputaMezua mezua = new DisputaMezua(testua, igorlea);
			db.persist(mezua);
			erreklamazioa.getMezuak().add(mezua);
		}

		db.getTransaction().commit();
		return erreklamazioa;
	}

	/**
	 * Erreklamazio baten egoera aldatzen du (0: Negoziatzen, 1: Eskalatuta, 2:
	 * Onartuta, 3: Ukatuta). Onartzen bada (2), sistemako metodoak erabiliz dirua
	 * automatikoki itzultzen dio erosleari.
	 *
	 * @param idErreklamazioa Erreklamazioaren IDa.
	 * @param egoeraBerria Ezarri nahi den egoera zenbakia.
	 * @return Eguneratutako Erreklamazioa objektua.
	 */
	public Erreklamazioa erreklamazioaEgoeraAldatu(Integer idErreklamazioa, int egoeraBerria) {
		db.getTransaction().begin();
		Erreklamazioa erreklamazioa = db.find(Erreklamazioa.class, idErreklamazioa);

		double itzultzekoPrezioa = 0.0;
		String erosleEmaila = null;
		String saltzaileEmaila = null;

		if (erreklamazioa != null) {
			erreklamazioa.setEgoera(egoeraBerria);

			if (egoeraBerria == 2) {
				TypedQuery<BoughtSale> query = db.createQuery(
						"SELECT bs FROM Registered r JOIN r.erositakoak bs WHERE r.email = :email AND bs.s.title = :title",
						BoughtSale.class);
				query.setParameter("email", erreklamazioa.getEroslea().getEmail());
				query.setParameter("title", erreklamazioa.getTitle());

				List<BoughtSale> erositakoak = query.getResultList();

				if (!erositakoak.isEmpty()) {
					itzultzekoPrezioa = erositakoak.get(0).getSale().getPrice();
					erosleEmaila = erreklamazioa.getEroslea().getEmail();
					saltzaileEmaila = erreklamazioa.getSaltzailea().getEmail();
				}
			}
		}

		db.getTransaction().commit();

		if (itzultzekoPrezioa > 0) {
			boolean kendutakoDirua = DiruaAtera(itzultzekoPrezioa, saltzaileEmaila);

			if (kendutakoDirua) {
				DiruaSartu(itzultzekoPrezioa, erosleEmaila);
				System.out.println(">> Itzulketa ondo prozesatu da DiruaAtera eta DiruaSartu erabiliz: "
						+ itzultzekoPrezioa + "€");
			} else {
				System.err.println(">> ERROREA: Saltzaileak ez zuen nahikoa saldo itzulketa egiteko.");
			}
		}

		if (egoeraBerria == 2) {
			notifikazioaBidali(erreklamazioa.getEroslea().getEmail(),
					"ZORIONAK! '" + erreklamazioa.getTitle() + "' erreklamazioa onartu da. Dirua itzuli zaizu.");
			notifikazioaBidali(erreklamazioa.getSaltzailea().getEmail(),
					"INFORMAZIOA: '" + erreklamazioa.getTitle() + "' erreklamazioa onartu da. Dirua kendu zaizu.");
		} else if (egoeraBerria == 3) {
			notifikazioaBidali(erreklamazioa.getEroslea().getEmail(),
					"UKATUTA: '" + erreklamazioa.getTitle() + "' erreklamazioa ukatu da.");
		}

		return erreklamazioa;
	}

	/**
	 * Administratzaileari eskalatu zaizkion (egoera = 1) erreklamazio guztiak
	 * lortzen ditu.
	 *
	 * @return Eskalatutako erreklamazioen zerrenda.
	 */
	public List<Erreklamazioa> getErreklamazioEskalatuak() {
		TypedQuery<Erreklamazioa> query = db.createQuery("SELECT e FROM Erreklamazioa e WHERE e.egoera = 1",
				Erreklamazioa.class);
		return query.getResultList();
	}

	// --- SALAKETEN KUDEAKETA ---

	/**
	 * Salaketa berri bat sortzen du.
	 *e
	 * @param mota Salaketaren mota.
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param pubDate Argitalpen-data.
	 * @param salatzailea Salaketa egiten duen erabiltzailea.
	 */
	public void salaketaSortu(String mota, Integer produktoaNumber, Date pubDate, Registered salatzailea)
			throws MustBeLaterThanTodayException, ErreklamazioaAlreadyExistException {

		System.out.println(">> DataAccess: SalaketaSortu=> mota= " + mota + " Produktoa=" + produktoaNumber);

		if (pubDate.before(UtilDate.trim(new Date()))) {
			throw new MustBeLaterThanTodayException(
					ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
		}

		try {
			db.getTransaction().begin();

			Sale produktoa = db.find(Sale.class, produktoaNumber);
			Registered salatzaileaDB = db.find(Registered.class, salatzailea.getEmail());

			Salaketa salaketa = new Salaketa(mota, produktoa, pubDate, salatzaileaDB);

			produktoa.getSalaketak().add(salaketa);
			salatzaileaDB.getSortutakoSalaketak().add(salaketa);

			db.persist(salaketa);
			db.getTransaction().commit();

			System.out.println("Salaketa sortua, ProduktoZenb: " + produktoaNumber + " | " + produktoa + " mota: "
					+ mota + " | " + "salatzailea:" + salatzailea.getEmail());

		} catch (Exception e) {
			e.printStackTrace();
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
		}
	}

	/**
	 * Irakurri gabeko salaketak lortzen ditu.
	 *
	 * @return Irakurri gabeko salaketen zerrenda.
	 */
	public List<Salaketa> getSalaketak() {
		TypedQuery<Salaketa> query = db
				.createQuery("SELECT e FROM Salaketa e JOIN FETCH e.produktoa WHERE e.egoera = 0", Salaketa.class);
		return query.getResultList();
	}

	/**
	 * Salaketa baten egoera aldatzen du.
	 *
	 * @param idSalaketa Salaketaren identifikatzailea.
	 * @param egoeraBerria Egoera berria.
	 */
	public void salaketaEgoeraAldatu(Integer idSalaketa, int egoeraBerria) {
		db.getTransaction().begin();
		Salaketa salaketa = db.find(Salaketa.class, idSalaketa);

		if (salaketa == null) {
			db.getTransaction().rollback();
			System.err.println("Ez da salaketa aurkitu ID honekin: " + idSalaketa);
			return;
		}

		salaketa.setEgoera(egoeraBerria);
		System.out.println("Salaketa egoera= " + egoeraBerria);
		db.getTransaction().commit();

		if (egoeraBerria == 2) {
			notifikazioaBidali(salaketa.getProduktoa().getSeller().getEmail(),
					"Salmentan duzun '" + salaketa.getProduktoa().getTitle() + "' produktua ezabatu dugu (ARRAZOIA: "
							+ salaketa.getMota() + ")");
		}
	}

	/**
	 * Produktu bat salatuta dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @return Produktua salatuta badago true.
	 */
	public boolean salatutaDago(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();

		Registered salatzailea = db.find(Registered.class, saltzaileaId);
		boolean existitzenDa = true;

		for (Salaketa salatuta : salatzailea.getSortutakoSalaketak()) {
			if (salatuta.getProduktoa() == null) {
				continue;
			}
			if (salatuta.getProduktoa().getSaleNumber().equals(produktoaNumber) && salatuta.getEgoera() != 1) {
				existitzenDa = false;
				break;
			}
		}

		db.getTransaction().commit();
		System.out.println(existitzenDa);
		return existitzenDa;
	}

	// --- NOTIFIKAZIOEN KUDEAKETA ---

	/**
	 * Erabiltzaile bati notifikazio edo mezu berri bat bidaltzen dio.
	 *
	 * @param email Jasotzailearen posta.
	 * @param mezua Bidali beharreko mezuaren testua.
	 */
	public void notifikazioaBidali(String email, String mezua) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, email);
		if (erabiltzailea != null) {
			Notifikazioa notifikazioa = new Notifikazioa(mezua, erabiltzailea);
			db.persist(notifikazioa);
			erabiltzailea.getNotifikazioak().add(notifikazioa);
		}
		db.getTransaction().commit();
	}

	/**
	 * Erabiltzaile baten notifikazio guztiak lortzen ditu, berrienak goian
	 * erakutsiz.
	 *
	 * @param email Erabiltzailearen posta.
	 * @return Notifikazioen zerrenda.
	 */
	public List<Notifikazioa> getNotifikazioak(String email) {
		TypedQuery<Notifikazioa> query = db.createQuery(
				"SELECT n FROM Notifikazioa n WHERE n.hartzailea.email = :email ORDER BY n.data DESC",
				Notifikazioa.class);
		query.setParameter("email", email);
		return query.getResultList();
	}

	/**
	 * Egiaztatzen du erabiltzaileak irakurri gabeko notifikaziorik duen ala ez.
	 * Interfaze grafikoan puntu gorria erakusteko erabiltzen da.
	 *
	 * @param email Erabiltzailearen posta.
	 * @return true irakurri gabeko mezuak badaude, false bestela.
	 */
	public boolean badituIrakurriGabekoak(String email) {
		TypedQuery<Long> query = db.createQuery(
				"SELECT COUNT(n) FROM Notifikazioa n WHERE n.hartzailea.email = :email AND n.irakurrita = false",
				Long.class);
		query.setParameter("email", email);
		return query.getSingleResult() > 0;
	}

	/**
	 * Erabiltzailearen notifikazio guztiak "irakurrita" bezala markatzen ditu.
	 *
	 * @param email Erabiltzailearen posta.
	 */
	public void markatuIrakurrita(String email) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, email);
		if (erabiltzailea != null) {
			for (Notifikazioa notifikazioa : erabiltzailea.getNotifikazioak()) {
				notifikazioa.setIrakurrita(true);
			}
		}
		db.getTransaction().commit();
	}

	// --- ADMINISTRATZAILEAREN METODOAK ---

	/**
	 * Produktu bat ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 */
	public void produktoaEzabatu(Integer produktoaNumber, String saltzaileaId) {
		Sale eproduktoa = db.find(Sale.class, produktoaNumber);
		db.getTransaction().begin();

		Registered saltzailea = db.find(Registered.class, eproduktoa.getSeller().getEmail());

		eproduktoa = db.find(Sale.class, produktoaNumber);

		Sale ezabatzekoSalmenta = null;
		for (Sale s : saltzailea.getSales()) {
			if (s.getSaleNumber().equals(eproduktoa.getSaleNumber())) {
				ezabatzekoSalmenta = s;
				break;
			}
		}
		if (ezabatzekoSalmenta != null) {
			saltzailea.getSales().remove(ezabatzekoSalmenta);
		}

		db.remove(eproduktoa);
		System.out.println("Produktua datu-basetik ezabatu da");

		db.getTransaction().commit();
	}

	/**
	 * Erabiltzaile kontu bat banatzen du.
	 *
	 * @param registeredId Erabiltzailearen identifikatzailea.
	 */
	public void kontuaBaneatu(String registeredId) {
		if (!db.getTransaction().isActive()) {
			db.getTransaction().begin();
		}

		System.out.println("Ezabatu nahi den ID-a: '" + registeredId + "'");
		Registered kontua = db.find(Registered.class, registeredId);
		System.out.println("Aurkitutakoa: " + kontua);
		if (kontua != null) {
			kontua.setBanned(true);
		}
		db.getTransaction().commit();
	}

	/**
	 * Erabiltzaile kontu baten debekua kentzen du.
	 *
	 * @param registeredId Erabiltzailearen identifikatzailea.
	 */
	public void kontuaDesbaneatu(String registeredId) {
		if (!db.getTransaction().isActive()) {
			db.getTransaction().begin();
		}

		System.out.println("Ezabatu nahi den ID-a: '" + registeredId + "'");
		Registered kontua = db.find(Registered.class, registeredId);
		System.out.println("Aurkitutakoa: " + kontua);
		if (kontua != null) {
			kontua.setBanned(false);
		}
		db.getTransaction().commit();
	}

	/**
	 * Erregistratutako erabiltzaileak lortzen ditu.
	 *
	 * @param desc Bilaketako testua.
	 * @return Erabiltzaileen zerrenda.
	 */
	public List<Registered> getRegisteredUsers(String desc) {
		System.out.println(">> DataAccess: getRegisteredUsers=> from= " + desc);

		List<Registered> emaitza = new ArrayList<Registered>();
		TypedQuery<Registered> query = db.createQuery("SELECT s FROM Registered s WHERE s.name LIKE ?1",
				Registered.class);
		query.setParameter(1, "%" + desc + "%");

		List<Registered> erabiltzaileak = query.getResultList();
		for (Registered erabiltzailea : erabiltzaileak) {
			emaitza.add(erabiltzailea);
		}
		return emaitza;
	}

	/**
	 * Sale edo BoughtSale objektuari dagokion edukiontzia lortzen du.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @return Lotutako edukiontzia.
	 */
	public SaleSellerBoughtContainer getSaleBoughtSale(Integer saleNumber) {
		SaleSellerBoughtContainer emaitza;
		db.getTransaction().begin();
		Sale itzuli = db.find(Sale.class, saleNumber);
		if (itzuli == null) {
			BoughtSale itzuli2 = db.find(BoughtSale.class, saleNumber);
			emaitza = new SaleSellerBoughtContainer(itzuli2);
		} else {
			emaitza = new SaleSellerBoughtContainer(itzuli);
		}
		db.getTransaction().commit();
		System.out.println(emaitza);
		return emaitza;
	}

	/**
	 * Erreklamazio bati lotutako saltzailea lortzen du.
	 *
	 * @param dm Erreklamazioa.
	 * @return Saltzailea.
	 */
	public User getSellerByErreklamazioa(Erreklamazioa dm) {
		db.getTransaction().begin();
		Erreklamazioa itzuli = db.find(Erreklamazioa.class, dm);
		db.getTransaction().commit();
		return itzuli.getSaltzailea();
	}

	/**
	 * Erreklamazio bati lotutako eroslea lortzen du.
	 *
	 * @param dm Erreklamazioa.
	 * @return Eroslea.
	 */
	public User getBuyerByErreklamazioa(Erreklamazioa dm) {
		db.getTransaction().begin();
		Erreklamazioa itzuli = db.find(Erreklamazioa.class, dm);
		db.getTransaction().commit();
		return itzuli.getEroslea();
	}

	/**
	 * Salaketa bati lotutako salatzailea lortzen du.
	 *
	 * @param sal Salaketa.
	 * @return Salatzailea.
	 */
	public User getSalatzaileaBySalaketa(Salaketa sal) {
		db.getTransaction().begin();
		Salaketa itzuli = db.find(Salaketa.class, sal);
		db.getTransaction().commit();
		return itzuli.getSalatzailea();
	}

	/**
	 * Disputa-mezu baten igorlea lortzen du.
	 *
	 * @param dm Disputa-mezua.
	 * @return Igorlea.
	 */
	public User getIgorleaByDM(DisputaMezua dm) {
		db.getTransaction().begin();
		DisputaMezua itzuli = db.find(DisputaMezua.class, dm);
		db.getTransaction().commit();
		return itzuli.getIgorlea();
	}

	/**
	 * Erreklamazio bat bere identifikatzailearen arabera lortzen du.
	 *
	 * @param kexaNumber Erreklamazioaren identifikatzailea.
	 * @return Aurkitutako erreklamazioa.
	 */
	public Erreklamazioa lortuErreklamazioById(Integer kexaNumber) {
		return db.find(Erreklamazioa.class, kexaNumber);
	}

	/**
	 * Erabiltzaile baten profil-datuak eguneratzen ditu.
	 *
	 * @param email Erabiltzailearen identifikatzailea (posta elektronikoa).
	 * @param izenBerria Ezarri nahi den izen berria.
	 * @param kalea Ezarri nahi den kalea.
	 * @param postaKodea Ezarri nahi den posta-kodea.
	 * @param herrialdea Ezarri nahi den herrialdea.
	 * @return ondo joan bada true itzultzen du, false bestela.
	 */
	public boolean erabiltzaileaEguneratu(String email, String izenBerria, String kalea, String postaKodea,
			String herrialdea) {
		db.getTransaction().begin();
		try {
			Registered erregistroa = db.find(Registered.class, email);
			if (erregistroa != null) {
				erregistroa.setName(izenBerria);

				if (erregistroa.getKokapena() == null) {
					erregistroa.setKokapena(new Gps(kalea, postaKodea, herrialdea));
				} else {
					erregistroa.getKokapena().eguneratuGps(kalea, postaKodea, herrialdea);
				}

				System.out.println(">> DataAccess: Erabiltzailea eguneratuta: " + email);
			}
			db.getTransaction().commit();
			return true;
		} catch (IllegalArgumentException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			return false;
		}
	}

	/**
	 * Salmenta bat VIP bati erreserbatzen dio.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @param vipId VIP identifikatzailea.
	 */
	public void saleErreserbatu(Integer saleNumber, Integer vipId) {
		db.getTransaction().begin();

		VIP vip = db.find(VIP.class, vipId);
		Sale salmenta = db.find(Sale.class, saleNumber);

		vip.getErreserbak().add(salmenta);
		salmenta.setErreserbatuta(true);
		db.getTransaction().commit();
	}

	/**
	 * Salmenta baten erreserba kentzen dio VIP bati.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @param vipId VIP identifikatzailea.
	 */
	public void saleDesErreserbatu(Integer saleNumber, Integer vipId) {
		db.getTransaction().begin();

		VIP vip = db.find(VIP.class, vipId);
		Sale salmenta = db.find(Sale.class, saleNumber);

		vip.getErreserbak().remove(salmenta);
		salmenta.setErreserbatuta(false);
		db.getTransaction().commit();
	}

	/**
	 * Produktu bat erreserbatuta dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param vipId VIP identifikatzailea.
	 * @return Produktua erreserbatuta badago true.
	 */
	public boolean erreserbatutaDago(Integer produktoaNumber, Integer vipId) {
		db.getTransaction().begin();

		VIP erabiltzaileaVip = db.find(VIP.class, vipId);
		boolean existitzenDa = false;

		if (erabiltzaileaVip != null) {
			for (Sale erreserbak : erabiltzaileaVip.getErreserbak()) {
				if (erreserbak == null) {
					continue;
				}
				if (erreserbak.getSaleNumber().equals(produktoaNumber)) {
					existitzenDa = true;
					break;
				}
			}
		}

		db.getTransaction().commit();
		System.out.println(existitzenDa);
		return existitzenDa;
	}

	/**
	 * Erabiltzaile bat harpidetzen du VIP gisa.
	 *
	 * @param harId Erabiltzailearen identifikatzailea.
	 * @return true harpidetza egin bada, false bestela.
	 */
	public boolean harpidetu(String harId) {
		db.getTransaction().begin();
		try {
			Registered r = db.find(Registered.class, harId);

			if (r == null || r.getVip() != null) {
				db.getTransaction().rollback();
				return false;
			}

			VIP vip = new VIP();

			r.setVip(vip);

			db.persist(vip);
			db.getTransaction().commit();
			return true;
		} catch (Exception e) {
			db.getTransaction().rollback();
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Datu-basean erabiltzaile baten eskaera berri bat sortzen du.
	 *
	 * @param title Eskaeraren titulua.
	 * @param desc Eskaeraren deskribapena.
	 * @param pubDate Argitalpen data.
	 * @param eskatzaileEmail Eskaera egiten duen erabiltzailearen posta.
	 * @return Sortutako Eskaera objektua.
	 */
	public Eskaera eskaeraSortu(String title, String desc, Date pubDate, String eskatzaileEmail) {
		db.getTransaction().begin();
		try {
			Registered eskatzailea = db.find(Registered.class, eskatzaileEmail);
			Eskaera eskaera = new Eskaera(title, desc, pubDate, eskatzailea);

			db.persist(eskaera);
			eskatzailea.addEskaera(eskaera);

			db.getTransaction().commit();
			System.out.println(">> DataAccess: Eskaera sortuta: " + title);
			return eskaera;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Datu-basean dauden Eskaera guztiak lortzen ditu.
	 *
	 * @return Eskaeren zerrenda.
	 */
	public List<Eskaera> getEskaerak() {
		TypedQuery<Eskaera> query = db.createQuery("SELECT e FROM Eskaera e ORDER BY e.pubDate DESC", Eskaera.class);
		return query.getResultList();
	}

	/**
	 * Existitzen den eskaera bati produktu bat eskaintzen dio.
	 *
	 * @param eskaeraId Eskaeraren IDa.
	 * @param saleId Eskaini nahi den produktuaren IDa.
	 * @param saltzaileEmail Eskaintza egiten duen erabiltzailearen posta.
	 * @return true dena ondo joan bada.
	 */
	public boolean eskaintzaGehitu(Integer eskaeraId, Integer saleId, String saltzaileEmail) {
		db.getTransaction().begin();
		try {
			Eskaera eskaera = db.find(Eskaera.class, eskaeraId);
			Sale eskaintza = db.find(Sale.class, saleId);
			Registered saltzailea = db.find(Registered.class, saltzaileEmail);

			if (eskaera != null && eskaintza != null) {
				eskaera.addEskaintza(eskaintza);
				db.getTransaction().commit();

				String mezua = "Zure '" + eskaera.getTitle() + "' eskaerak eskaintza berri bat jaso du: "
						+ eskaintza.getTitle() + " (" + saltzailea.getName() + ")";
				notifikazioaBidali(eskaera.getEskatzailea().getEmail(), mezua);

				System.out.println(
						">> DataAccess: Eskaintza gehituta: " + eskaintza.getTitle() + " -> " + eskaera.getTitle());
				return true;
			}
			db.getTransaction().commit();
			return false;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Erabiltzaile baten eskaerak soilik lortzen ditu.
	 *
	 * @param email Erabiltzailearen posta.
	 * @return Bere eskaeren zerrenda.
	 */
	public List<Eskaera> getZureEskaerak(String email) {
		TypedQuery<Eskaera> query = db.createQuery("SELECT e FROM Eskaera e WHERE e.eskatzailea.email = :email",
				Eskaera.class);
		query.setParameter("email", email);
		return query.getResultList();
	}

	// --- SASKIAREN METODOAK ---

	/**
	 * Produktu bat saskira gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean saskianGehitu(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		Sale saskikoProduktua = db.find(Sale.class, produktoaNumber);

		System.out.println(">> saskianGehitu => saltzailea: " + erabiltzailea);
		System.out.println(">> saskianGehitu => saskiaP: " + saskikoProduktua);

		if (erabiltzailea != null && saskikoProduktua != null) {
			erabiltzailea.addSaskira(saskikoProduktua);
			db.persist(erabiltzailea);
			db.getTransaction().commit();
			System.out.println("Saskira gehituta, tamaina: " + erabiltzailea.getSaskia().size());
			return true;
		}

		System.out.println(">> ERROREA: saltzailea edo saskiaP null da!");
		db.getTransaction().rollback();
		return false;
	}

	/**
	 * Produktu bat saskitik ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean saskitikEzabatu(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		Sale saskikoProduktua = db.find(Sale.class, produktoaNumber);
		if (erabiltzailea != null) {
			erabiltzailea.removeSaskitik(saskikoProduktua);
		}
		db.getTransaction().commit();
		System.out.println("Saskitik ezabatuta");
		return true;
	}

	/**
	 * Produktu bat saskian dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Produktua saskian badago true.
	 */
	public boolean saskianDago(Integer produktoaNumber, String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		boolean existitzenDa = false;
		for (Sale saskia : erabiltzailea.getSaskia()) {
			if (saskia.getSaleNumber().equals(produktoaNumber)) {
				existitzenDa = true;
				break;
			}
		}

		db.getTransaction().commit();
		System.out.println(existitzenDa);
		return existitzenDa;
	}

	/**
	 * Erabiltzaile baten saskiko produktuen zerrenda lortzen du.
	 *
	 * @param desc Bilaketako testua.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Saskiko produktuen zerrenda.
	 */
	public List<Sale> getSaskiaLista(String desc, String saltzaileaId) {
		System.out.println(">> getSaskiaLista => email: " + saltzaileaId + " desc: " + desc);

		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);

		if (erabiltzailea == null) {
			System.out.println(">> ERROREA: Ez da erabiltzailea aurkitu!");
			return new ArrayList<>();
		}

		System.out.println(">> Saskia tamaina DB-tik: " + erabiltzailea.getSaskia().size());
		erabiltzailea.getSaskia().forEach(s -> System.out.println("   - " + s.getTitle()));

		List<Sale> emaitza = new ArrayList<>();
		for (Sale salmenta : erabiltzailea.getSaskia()) {
			if (desc == null || desc.isEmpty() || salmenta.getTitle().toLowerCase().contains(desc.toLowerCase())) {
				emaitza.add(salmenta);
			}
		}

		System.out.println(">> Emaitza tamaina: " + emaitza.size());
		return emaitza;
	}

	/**
	 * Erabiltzaile baten saskiaren prezio totala lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Prezio totala.
	 */
	public float getSaskiaPrezioTotala(String saltzaileaId) {
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		System.out.println(">> Saskia tamaina DB-tik: " + erabiltzailea.getSaskia().size());
		erabiltzailea.getSaskia().forEach(s -> System.out.println("   - " + s.getTitle()));
		float emaitza = 0;
		for (Sale salmenta : erabiltzailea.getSaskia()) {
			emaitza += salmenta.getPrice();
		}

		return emaitza;
	}

	/**
	 * Saskiko produktu guztiak erosten ditu.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	public void saskikoProduktuakErosi(String saltzaileaId) {
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		List<Sale> saskikoKopia = new ArrayList<>(erabiltzailea.getSaskia());

		boolean lehenengoa = true;
		for (Sale salmenta : saskikoKopia) {
			produktoaErosi(salmenta.getSaleNumber(), saltzaileaId, lehenengoa);
			lehenengoa = false;
		}

		db.getTransaction().begin();
		Registered erabiltzaileaFresh = db.find(Registered.class, saltzaileaId);
		erabiltzaileaFresh.getSaskia().clear();
		db.getTransaction().commit();
	}

	/**
	 * Erabiltzaile baten saskia husten du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	public void saskiaHustu(String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		System.out.println(">> saskiaHustu => tamaina aurretik: " + erabiltzailea.getSaskia().size());
		if (erabiltzailea != null) {
			erabiltzailea.getSaskia().clear();
		}
		db.getTransaction().commit();
		System.out.println(">> saskiaHustu => tamaina ondoren: " + erabiltzailea.getSaskia().size());
	}

	/**
	 * Salmentak dituzten erabiltzaile erregistratuak lortzen ditu.
	 *
	 * @param desc Bilaketako testua.
	 * @param user1 Kontsultan erabiltzen den erabiltzaile-identifikatzailea.
	 * @return Erabiltzaileen zerrenda.
	 */
	public List<Registered> getRegisteredUsersWithSales(String desc, String user1) {
		System.out.println(">> DataAccess: getRegisteredUsers=> from= " + desc);

		List<Registered> emaitza = new ArrayList<Registered>();
		TypedQuery<Registered> query = db.createQuery("SELECT s FROM Registered s WHERE s.name LIKE ?1",
				Registered.class);
		query.setParameter(1, "%" + desc + "%");
		Registered unekoa = db.find(Registered.class, user1);
		List<Registered> erabiltzaileak = query.getResultList();
		for (Registered erabiltzailea : erabiltzaileak) {
			if (!erabiltzailea.getSales().isEmpty() && !erabiltzailea.equals(unekoa)) {
				emaitza.add(erabiltzailea);
			}
		}
		return emaitza;
	}

	/**
	 * Erabiltzaile baten saskiaren bidalketa-kostua kalkulatzen du.
	 *
	 * @param erosleaId Eroslearen identifikatzailea.
	 * @param saltzaileId Saltzailearen identifikatzailea.
	 * @return Bidalketa-kostua.
	 */
	public double getSaskiaBidalketaKostua(String erosleaId, String saltzaileId) {
		Registered erosle = db.find(Registered.class, erosleaId);
		Registered saltzailea = db.find(Registered.class, saltzaileId);
		return erosle.getKokapena().bidalketaGastuakKalkulatu(saltzailea.getKokapena());
	}

	/**
	 * Erabiltzaile bat VIP den egiaztatzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Erabiltzailea VIP bada true.
	 */
	public boolean vipDa(String saltzaileaId) {
		db.getTransaction().begin();
		Registered erabiltzailea = db.find(Registered.class, saltzaileaId);
		VIP saltzailea = null;
		if (erabiltzailea.getVip() != null) {
			saltzailea = db.find(VIP.class, erabiltzailea.getVip().getId());
		}
		db.getTransaction().commit();
		return saltzailea != null;
	}
}