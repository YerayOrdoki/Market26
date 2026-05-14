package businessLogic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.EntityExistsException;

import dataAccess.DataAccess;
import domain.BoughtSale;
import domain.DisputaMezua;
import domain.Erreklamazioa;
import domain.Eskaera;
import domain.Mugimendua;
import domain.Notifikazioa;
import domain.Registered;
import domain.Salaketa;
import domain.Sale;
import domain.SaleSellerBoughtContainer;
import domain.User;
import exceptions.ErreklamazioaAlreadyExistException;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

/**
 * Negozio-logika web zerbitzu gisa inplementatzen duen klasea.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation implements BLFacade {

	private static final int baseSize = 160;
	private static final String basePath = "src/main/resources/images/";

	DataAccess dbManager;

	/**
	 * Klasearen instantzia lehenetsia sortzen du.
	 */
	public BLFacadeImplementation() {
		System.out.println("Creating BLFacadeImplementation instance");
		dbManager = new DataAccess();
	}

	/**
	 * Klasearen instantzia bat sortzen du emandako DataAccess objektuarekin.
	 *
	 * @param da Datu-atzipenerako erabiliko den objektua.
	 */
	public BLFacadeImplementation(DataAccess da) {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dbManager = da;
	}

	/**
	 * Produktu-salmenta berri bat sortzen du.
	 *
	 * @param title Produktuaren izenburua.
	 * @param description Produktuaren deskribapena.
	 * @param status Produktuaren egoera.
	 * @param price Produktuaren prezioa.
	 * @param pubDate Argitalpen-data.
	 * @param sellerEmail Saltzailearen emaila.
	 * @param file Produktuarekin lotutako fitxategia.
	 * @return Sortutako salmenta.
	 * @throws FileNotUploadedException Fitxategia ezin bada igo.
	 * @throws MustBeLaterThanTodayException Data baliogabea bada.
	 * @throws SaleAlreadyExistException Salmenta dagoeneko existitzen bada.
	 */
	@WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail,
			File file) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		dbManager.open();
		Sale salmenta = dbManager.createSale(title, description, status, price, pubDate, sellerEmail, file);
		dbManager.close();
		return salmenta;
	}

	/**
	 * Erreklamazio berri bat sortzen du.
	 *
	 * @param title Erreklamazioaren izenburua.
	 * @param deskribapena Erreklamazioaren deskribapena.
	 * @param sortzaileEmaila Erreklamazioa sortzen duen erabiltzailearen emaila.
	 * @param jasotzaileEmaila Erreklamazioa jasotzen duen erabiltzailearen emaila.
	 * @param pubDate Argitalpen-data.
	 * @param produktoa Erositako produktua.
	 * @throws MustBeLaterThanTodayException Data baliogabea bada.
	 * @throws ErreklamazioaAlreadyExistException Erreklamazioa lehendik existitzen bada.
	 */
	@WebMethod
	public void erreklamazioaSortu(String title, String deskribapena, String sortzaileEmaila, String jasotzaileEmaila,
			Date pubDate, BoughtSale produktoa)
			throws MustBeLaterThanTodayException, ErreklamazioaAlreadyExistException {
		dbManager.open();
		dbManager.ErreklamazioaSortu(title, deskribapena, sortzaileEmaila, jasotzaileEmaila, pubDate, produktoa);
		dbManager.close();
	}

	/**
	 * Deskribapenaren arabera salmenten zerrenda lortzen du.
	 *
	 * @param desc Bilaketako deskribapena.
	 * @return Aurkitutako salmenten zerrenda.
	 */
	@WebMethod
	public List<Sale> getSales(String desc) {
		dbManager.open();
		List<Sale> salmentak = dbManager.getSales(desc);
		dbManager.close();
		return salmentak;
	}

	/**
	 * Deskribapenaren eta dataren arabera argitaratutako salmentak lortzen ditu.
	 *
	 * @param desc Bilaketako deskribapena.
	 * @param pubDate Argitalpen-data.
	 * @return Aurkitutako salmenten zerrenda.
	 */
	@WebMethod
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		dbManager.open();
		List<Sale> salmentak = dbManager.getPublishedSales(desc, pubDate);
		dbManager.close();
		return salmentak;
	}

	/**
	 * Fitxategi baten irudia lortzen du.
	 *
	 * @param fileName Fitxategiaren izena.
	 * @return Irudi-bufferra.
	 */
	@WebMethod
	public BufferedImage getFile(String fileName) {
		return dbManager.getFile(fileName);
	}

	/**
	 * Datu-basea hasieratzen du.
	 */
	@WebMethod
	public void initializeBD() {
		dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}

	/**
	 * Irudi bat deskargatzen du izenaren arabera.
	 *
	 * @param imageName Irudiaren izena.
	 * @return Kargatutako irudia edo null errorea gertatuz gero.
	 */
	@WebMethod
	public Image downloadImage(String imageName) {
		File image = new File(basePath + imageName);
		try {
			return ImageIO.read(image);
		} catch (IOException e) {
			// Irudia ezin bada irakurri, salbuespena kontsolan erakusten da.
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Erabiltzailearen saioa balidatzen du.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @param pass Erabiltzailearen pasahitza.
	 * @return Erabiltzailea, baliozkoa bada.
	 */
	@WebMethod
	public User isLogged(String email, String pass) {
		dbManager.open();
		User erabiltzailea = dbManager.isLogged(email, pass);
		dbManager.close();
		return erabiltzailea;
	}

	/**
	 * Erregistratutako erabiltzaile berri bat sortzen du.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @param erabiltzailea Erabiltzaile-izena.
	 * @param pass Pasahitza.
	 * @return Sortutako erabiltzailea.
	 * @throws EntityExistsException Erabiltzailea existitzen bada.
	 */
	@WebMethod
	public Registered erabiltzaileaSortu(String email, String erabiltzailea, String pass)
			throws EntityExistsException {
		dbManager.open();
		Registered erregistratua = dbManager.erabiltzaileaSortu(email, erabiltzailea, pass);
		dbManager.close();
		return erregistratua;
	}

	/**
	 * Produktu bat gogokoetatik ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean faboritoaEzabatu(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.faboritoaEzabatu(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Produktu bat gogokoetara gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean faboritoaGehitu(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.faboritoaGehitu(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Produktu bat erosien zerrendara gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void erositakoaGehitu(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.erositakoakGehitu(produktoaNumber, saltzaileaId);
		dbManager.close();
	}

	/**
	 * Produktu bat gogokoetan dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Produktua gogokoetan badago true.
	 */
	@WebMethod
	public boolean faboritoaDa(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.faboritoaDa(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Saltzaile bat bere izenaren arabera lortzen du.
	 *
	 * @param izena Saltzailearen izena.
	 * @return Aurkitutako saltzailea.
	 */
	@WebMethod
	public Registered getSellerByName(String izena) {
		dbManager.open();
		Registered saltzailea = dbManager.getSellerByName(izena);
		dbManager.close();
		return saltzailea;
	}

	/**
	 * Saltzaile bat bere emailaren arabera lortzen du.
	 *
	 * @param email Saltzailearen emaila.
	 * @return Aurkitutako saltzailea.
	 */
	@WebMethod
	public Registered getSellerByEmail(String email) {
		dbManager.open();
		Registered saltzailea = dbManager.getSellerByEmail(email);
		dbManager.close();
		return saltzailea;
	}

	/**
	 * Erabiltzaile baten gogokoen zerrenda lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Gogokoen zerrenda.
	 */
	@WebMethod
	public List<Sale> getFaboritoak(String saltzaileaId) {
		dbManager.open();
		List<Sale> faboritoak = dbManager.getFaboritoLista(saltzaileaId);
		dbManager.close();
		return faboritoak;
	}

	/**
	 * Erabiltzaile baten erositako produktuen zerrenda lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Erositako produktuen zerrenda.
	 */
	@WebMethod
	public List<BoughtSale> getErositakoak(String saltzaileaId) {
		dbManager.open();
		List<BoughtSale> erositakoak = dbManager.getErositakoLista(saltzaileaId);
		dbManager.close();
		return erositakoak;
	}

	/**
	 * Erositako salmenta bat lortzen du eroslearen emailaren eta izenburuaren arabera.
	 *
	 * @param erosleEmail Eroslearen emaila.
	 * @param title Salmentaren izenburua.
	 * @return Erositako salmenta.
	 */
	@WebMethod
	public BoughtSale lortuErositakoSalmenta(String erosleEmail, String title) {
		dbManager.open();
		BoughtSale erositakoSalmenta = dbManager.lortuErositakoSalmenta(erosleEmail, title);
		dbManager.close();
		return erositakoSalmenta;
	}

	/**
	 * Produktu baten erosketa egiten du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Eroslearen identifikatzailea.
	 * @param bidalketaOrdaindu Bidalketa ordaindu den ala ez.
	 * @return Erosketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean produktoaErosi(Integer produktoaNumber, String saltzaileaId, boolean bidalketaOrdaindu) {
		dbManager.open();
		boolean itzulitakoa = dbManager.produktoaErosi(produktoaNumber, saltzaileaId, bidalketaOrdaindu);
		dbManager.close();
		return itzulitakoa;
	}

	/**
	 * Saltzaile baten salmentak lortzen ditu iragazkiaren arabera.
	 *
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @param query Bilaketa-testua.
	 * @param date Data-iragazkia.
	 * @return Salmenten zerrenda.
	 */
	@WebMethod
	public List<Sale> getSalesBySeller(String saltzaileaId, String query, Date date) {
		dbManager.open();
		List<Sale> zerrenda = dbManager.getSalesBySeller(saltzaileaId, query, date);
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Saltzaile batek saldutako produktuak lortzen ditu.
	 *
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @param query Bilaketa-testua.
	 * @param date Data-iragazkia.
	 * @return Saldu diren produktuen zerrenda.
	 */
	@WebMethod
	public List<BoughtSale> getSoldBySeller(String saltzaileaId, String query, Date date) {
		dbManager.open();
		List<BoughtSale> zerrenda = dbManager.getSoldBySeller(saltzaileaId, query, date);
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Salmenta bat ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean removeSale(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean eginda = dbManager.removeSale(produktoaNumber, saltzaileaId);
		dbManager.close();
		return eginda;
	}

	/**
	 * Erabiltzaile baten kontutik dirua ateratzen du.
	 *
	 * @param kantitatea Atera beharreko kopurua.
	 * @param userId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean diruaAtera(Double kantitatea, String userId) {
		dbManager.open();
		boolean eginda = dbManager.DiruaAtera(kantitatea, userId);
		dbManager.close();
		return eginda;
	}

	/**
	 * Erabiltzaile baten kontuan dirua sartzen du.
	 *
	 * @param kantitatea Sartu beharreko kopurua.
	 * @param userId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void diruaSartu(Double kantitatea, String userId) {
		dbManager.open();
		dbManager.DiruaSartu(kantitatea, userId);
		dbManager.close();
	}

	/**
	 * Erabiltzaile baten mugimenduak lortzen ditu.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @param query Bilaketa-testua.
	 * @param date Data-iragazkia.
	 * @return Mugimenduen zerrenda.
	 */
	@WebMethod
	public List<Mugimendua> getMugimenduakBySeller(String saltzaileaId, String query, Date date) {
		dbManager.open();
		List<Mugimendua> zerrenda = dbManager.getMugimenduakBySeller(saltzaileaId, query, date);
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Erreklamazio bat lortzen du eroslearen emailaren eta izenburuaren arabera.
	 *
	 * @param erosleEmail Eroslearen emaila.
	 * @param title Erreklamazioaren izenburua.
	 * @return Aurkitutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa lortuErreklamazioa(String erosleEmail, String title) {
		dbManager.open();
		Erreklamazioa erreklamazioa = dbManager.lortuErreklamazioa(erosleEmail, title);
		dbManager.close();
		return erreklamazioa;
	}

	/**
	 * Erreklamazio bati disputa-mezu bat gehitzen dio.
	 *
	 * @param idErreklamazioa Erreklamazioaren identifikatzailea.
	 * @param emailIgorlea Mezua bidaltzen duenaren emaila.
	 * @param testua Mezuaren edukia.
	 * @return Eguneratutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa disputaMezuaGehitu(Integer idErreklamazioa, String emailIgorlea, String testua) {
		dbManager.open();
		Erreklamazioa erreklamazioa = dbManager.disputaMezuaGehitu(idErreklamazioa, emailIgorlea, testua);
		dbManager.close();
		return erreklamazioa;
	}

	/**
	 * Erreklamazio baten egoera aldatzen du.
	 *
	 * @param idErreklamazioa Erreklamazioaren identifikatzailea.
	 * @param egoeraBerria Egoera berria.
	 * @return Eguneratutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa erreklamazioaEgoeraAldatu(Integer idErreklamazioa, int egoeraBerria) {
		dbManager.open();
		Erreklamazioa erreklamazioa = dbManager.erreklamazioaEgoeraAldatu(idErreklamazioa, egoeraBerria);
		dbManager.close();
		return erreklamazioa;
	}

	/**
	 * Eskalatutako erreklamazio guztiak lortzen ditu.
	 *
	 * @return Eskalatutako erreklamazioen zerrenda.
	 */
	@WebMethod
	public List<Erreklamazioa> getErreklamazioEskalatuak() {
		dbManager.open();
		List<Erreklamazioa> zerrenda = dbManager.getErreklamazioEskalatuak();
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Erabiltzaile bati notifikazio bat bidaltzen dio.
	 *
	 * @param email Jasotzailearen emaila.
	 * @param mezua Bidali beharreko mezua.
	 */
	@WebMethod
	public void notifikazioaBidali(String email, String mezua) {
		dbManager.open();
		dbManager.notifikazioaBidali(email, mezua);
		dbManager.close();
	}

	/**
	 * Erabiltzaileak irakurri gabeko notifikaziorik duen egiaztatzen du.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @return Irakurri gabeko notifikazioak badaude true.
	 */
	@WebMethod
	public boolean badituIrakurriGabekoak(String email) {
		dbManager.open();
		boolean eginda = dbManager.badituIrakurriGabekoak(email);
		dbManager.close();
		return eginda;
	}

	/**
	 * Erabiltzaile baten notifikazio guztiak irakurritzat markatzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 */
	@WebMethod
	public void markatuIrakurrita(String email) {
		dbManager.open();
		dbManager.markatuIrakurrita(email);
		dbManager.close();
	}

	/**
	 * Erabiltzaile baten notifikazioak lortzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @return Notifikazioen zerrenda.
	 */
	@WebMethod
	public List<Notifikazioa> getNotifikazioak(String email) {
		dbManager.open();
		List<Notifikazioa> zerrenda = dbManager.getNotifikazioak(email);
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Salaketa berri bat sortzen du.
	 *
	 * @param mota Salaketaren mota.
	 * @param numProduktoa Produktuaren identifikatzailea.
	 * @param pubDate Argitalpen-data.
	 * @param salatzailea Salaketa egiten duen erabiltzailea.
	 * @throws MustBeLaterThanTodayException Data baliogabea bada.
	 * @throws ErreklamazioaAlreadyExistException Salaketa lehendik existitzen bada.
	 */
	@WebMethod
	public void salaketaSortu(String mota, Integer numProduktoa, Date pubDate, Registered salatzailea)
			throws MustBeLaterThanTodayException, ErreklamazioaAlreadyExistException {
		dbManager.open();
		dbManager.salaketaSortu(mota, numProduktoa, pubDate, salatzailea);
		dbManager.close();
	}

	/**
	 * Salaketa guztiak lortzen ditu.
	 *
	 * @return Salaketen zerrenda.
	 */
	@WebMethod
	public List<Salaketa> getSalaketak() {
		dbManager.open();
		List<Salaketa> zerrenda = dbManager.getSalaketak();
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Produktu bat ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 */
	@WebMethod
	public void produktoaEzabatu(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		dbManager.produktoaEzabatu(produktoaNumber, saltzaileaId);
		dbManager.close();
	}

	/**
	 * Erabiltzaile-kontu bat banatzen du.
	 *
	 * @param registeredId Erregistratutako erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void kontuaBaneatu(String registeredId) {
		dbManager.open();
		dbManager.kontuaBaneatu(registeredId);
		dbManager.close();
	}

	/**
	 * Erabiltzaile-kontu baten debekua kentzen du.
	 *
	 * @param registeredId Erregistratutako erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void kontuaDesbaneatu(String registeredId) {
		dbManager.open();
		dbManager.kontuaDesbaneatu(registeredId);
		dbManager.close();
	}

	/**
	 * Erregistratutako erabiltzaileen zerrenda lortzen du.
	 *
	 * @param desc Bilaketako deskribapena.
	 * @return Erabiltzaileen zerrenda.
	 */
	@WebMethod
	public List<Registered> getRegisteredUsers(String desc) {
		dbManager.open();
		List<Registered> erabiltzaileak = dbManager.getRegisteredUsers(desc);
		dbManager.close();
		return erabiltzaileak;
	}

	/**
	 * Salaketa baten egoera aldatzen du.
	 *
	 * @param idErreklamazioa Salaketaren identifikatzailea.
	 * @param egoeraBerria Egoera berria.
	 */
	@WebMethod
	public void salaketaEgoeraAldatu(Integer idErreklamazioa, int egoeraBerria) {
		dbManager.open();
		dbManager.salaketaEgoeraAldatu(idErreklamazioa, egoeraBerria);
		dbManager.close();
	}

	/**
	 * Produktu bat salatua dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @return Produktua salatua badago true.
	 */
	@WebMethod
	public boolean salatutaDago(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.salatutaDago(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Salmenta, saltzailea eta erosketa lotzen dituen edukiontzia lortzen du.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @return Lotutako datuen edukiontzia.
	 */
	@WebMethod
	public SaleSellerBoughtContainer getSaleSellerBoughtContainer(Integer saleNumber) {
		dbManager.open();
		SaleSellerBoughtContainer itzulitakoa = dbManager.getSaleBoughtSale(saleNumber);
		dbManager.close();
		return itzulitakoa;
	}

	/**
	 * Erreklamazio bati lotutako saltzailea lortzen du.
	 *
	 * @param erreklamazioa Erreklamazioa.
	 * @return Saltzailea.
	 */
	@WebMethod
	public User getSellerByErreklamazioa(Erreklamazioa erreklamazioa) {
		dbManager.open();
		User itzulitakoa = dbManager.getSellerByErreklamazioa(erreklamazioa);
		dbManager.close();
		return itzulitakoa;
	}

	/**
	 * Erreklamazio bati lotutako eroslea lortzen du.
	 *
	 * @param erreklamazioa Erreklamazioa.
	 * @return Eroslea.
	 */
	@WebMethod
	public User getBuyerByErreklamazioa(Erreklamazioa erreklamazioa) {
		dbManager.open();
		User itzulitakoa = dbManager.getBuyerByErreklamazioa(erreklamazioa);
		dbManager.close();
		return itzulitakoa;
	}

	/**
	 * Salaketa bati lotutako salatzailea lortzen du.
	 *
	 * @param salaketa Salaketa.
	 * @return Salatzailea.
	 */
	@WebMethod
	public User getSalatzaileaBySalaketa(Salaketa salaketa) {
		dbManager.open();
		User itzulitakoa = dbManager.getSalatzaileaBySalaketa(salaketa);
		dbManager.close();
		return itzulitakoa;
	}

	/**
	 * Disputa-mezu baten igorlea lortzen du.
	 *
	 * @param disputaMezua Disputa-mezua.
	 * @return Igorlea.
	 */
	@WebMethod
	public User getIgorleaByDM(DisputaMezua disputaMezua) {
		dbManager.open();
		User itzulitakoa = dbManager.getIgorleaByDM(disputaMezua);
		dbManager.close();

		return itzulitakoa;
	}

	/**
	 * Erreklamazio bat bere identifikatzailearen arabera lortzen du.
	 *
	 * @param kexaNumber Erreklamazioaren identifikatzailea.
	 * @return Aurkitutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa lortuErreklamazioById(Integer kexaNumber) {
		dbManager.open();
		Erreklamazioa itzulitakoa = dbManager.lortuErreklamazioById(kexaNumber);
		dbManager.close();
		return itzulitakoa;
	}

	/**
	 * Salmenta bat erreserbatzen du VIP erabiltzaile batentzat.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @param vipId VIP erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saleErreserbatu(Integer saleNumber, Integer vipId) {
		dbManager.open();
		dbManager.saleErreserbatu(saleNumber, vipId);
		dbManager.close();
	}

	/**
	 * Salmenta baten erreserba kentzen du.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @param vipId VIP erabiltzailearen identifikatzailea.
	 */
	public void saleDesErreserbatu(Integer saleNumber, Integer vipId) {
		dbManager.open();
		dbManager.saleDesErreserbatu(saleNumber, vipId);
		dbManager.close();
	}

	/**
	 * Produktu bat erreserbatuta dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param vipId VIP erabiltzailearen identifikatzailea.
	 * @return Produktua erreserbatuta badago true.
	 */
	@WebMethod
	public boolean erreserbatutaDago(Integer produktoaNumber, Integer vipId) {
		dbManager.open();
		boolean emaitza = dbManager.erreserbatutaDago(produktoaNumber, vipId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Erabiltzaile bati harpidetza aplikatzen dio.
	 *
	 * @param harId Harpidetzaren identifikatzailea.
	 */
	@WebMethod
	public void harpidetu(String harId) {
		dbManager.open();
		dbManager.harpidetu(harId);
		dbManager.close();
	}

	/**
	 * Eskaera berri bat sortzen du.
	 *
	 * @param title Eskaeraren izenburua.
	 * @param desc Eskaeraren deskribapena.
	 * @param pubDate Argitalpen-data.
	 * @param eskatzaileEmail Eskatzailearen emaila.
	 * @return Sortutako eskaera.
	 */
	@WebMethod
	public Eskaera eskaeraSortu(String title, String desc, java.util.Date pubDate, String eskatzaileEmail) {
		dbManager.open();
		Eskaera eskaera = dbManager.eskaeraSortu(title, desc, pubDate, eskatzaileEmail);
		dbManager.close();
		return eskaera;
	}

	/**
	 * Eskaera guztiak lortzen ditu.
	 *
	 * @return Eskaeren zerrenda.
	 */
	@WebMethod
	public java.util.List<Eskaera> getEskaerak() {
		dbManager.open();
		java.util.List<Eskaera> zerrenda = dbManager.getEskaerak();
		dbManager.close();
		return zerrenda;
	}

	/**
	 * Eskaera bati eskaintza bat gehitzen dio.
	 *
	 * @param eskaeraId Eskaeraren identifikatzailea.
	 * @param saleId Salmentaren identifikatzailea.
	 * @param saltzaileEmail Saltzailearen emaila.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean eskaintzaGehitu(Integer eskaeraId, Integer saleId, String saltzaileEmail) {
		dbManager.open();
		boolean ondo = dbManager.eskaintzaGehitu(eskaeraId, saleId, saltzaileEmail);
		dbManager.close();
		return ondo;
	}

	/**
	 * Erabiltzaile baten eskaerak lortzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @return Erabiltzailearen eskaeren zerrenda.
	 */
	@WebMethod
	public List<Eskaera> getZureEskaerak(String email) {
		dbManager.open();
		List<Eskaera> eskaerak = dbManager.getZureEskaerak(email);
		dbManager.close();
		return eskaerak;
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
		dbManager.open();
		boolean emaitza = dbManager.saskitikEzabatu(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Produktu bat saskira gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean saskianGehitu(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.saskianGehitu(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Produktu bat saskian dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Produktua saskian badago true.
	 */
	@WebMethod
	public boolean saskianDago(Integer produktoaNumber, String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.saskianDago(produktoaNumber, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Erabiltzaile baten saskiaren zerrenda lortzen du.
	 *
	 * @param desc Bilaketako deskribapena.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Saskiko produktuen zerrenda.
	 */
	@WebMethod
	public List<Sale> getSaskiaLista(String desc, String saltzaileaId) {
		dbManager.open();
		List<Sale> emaitza = dbManager.getSaskiaLista(desc, saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Erabiltzaile baten saskiaren prezio totala lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Prezio totala.
	 */
	@WebMethod
	public float getSaskiaPrezioTotala(String saltzaileaId) {
		dbManager.open();
		float emaitza = dbManager.getSaskiaPrezioTotala(saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Saskiko produktu guztiak erosten ditu.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saskikoProduktuakErosi(String saltzaileaId) {
		dbManager.open();
		dbManager.saskikoProduktuakErosi(saltzaileaId);
		dbManager.close();
	}

	/**
	 * Erabiltzaile baten saskia husten du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saskiaHustu(String saltzaileaId) {
		dbManager.open();
		dbManager.saskiaHustu(saltzaileaId);
		dbManager.close();
	}

	/**
	 * Salmentak dituzten erabiltzaile erregistratuak lortzen ditu.
	 *
	 * @param desc Bilaketako deskribapena.
	 * @param user1 Kontsultan erabiltzen den erabiltzaile-identifikatzailea.
	 * @return Erabiltzaileen zerrenda.
	 */
	@WebMethod
	public List<Registered> getRegisteredUsersWithSales(String desc, String user1) {
		dbManager.open();
		List<Registered> erabiltzaileak = dbManager.getRegisteredUsersWithSales(desc, user1);
		dbManager.close();
		return erabiltzaileak;
	}

	/**
	 * Saski baten bidalketa-kostua lortzen du.
	 *
	 * @param erosleaId Eroslearen identifikatzailea.
	 * @param saltzaileId Saltzailearen identifikatzailea.
	 * @return Bidalketa-kostua.
	 */
	@WebMethod
	public double getSaskiaBidalketaKostua(String erosleaId, String saltzaileId) {
		dbManager.open();
		double kostua = dbManager.getSaskiaBidalketaKostua(erosleaId, saltzaileId);
		dbManager.close();
		return kostua;
	}

	/**
	 * Erabiltzaile baten datuak eguneratzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @param izenBerria Izen berria.
	 * @param kalea Kale berria.
	 * @param postaKodea Posta-kode berria.
	 * @param herrialdea Herrialde berria.
	 * @return Eguneraketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean erabiltzaileaEguneratu(String email, String izenBerria, String kalea, String postaKodea,
			String herrialdea) {
		dbManager.open();
		boolean erantzuna = dbManager.erabiltzaileaEguneratu(email, izenBerria, kalea, postaKodea, herrialdea);
		dbManager.close();
		return erantzuna;
	}

	/**
	 * Erabiltzaile bat VIP den egiaztatzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Erabiltzailea VIP bada true.
	 */
	@WebMethod
	public boolean vipDa(String saltzaileaId) {
		dbManager.open();
		boolean emaitza = dbManager.vipDa(saltzaileaId);
		dbManager.close();
		return emaitza;
	}

	/**
	 * Datu-basearekiko konexioa ixten du.
	 */
	public void close() {
		DataAccess datuSarbidea = new DataAccess();
		datuSarbidea.close();
	}
}