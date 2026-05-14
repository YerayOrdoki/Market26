package businessLogic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.EntityExistsException;

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
 * Negozio-logikaren eragiketak definitzen dituen interfazea.
 */
@WebService
public interface BLFacade {

	/**
	 * Saltzaile bati produktu-salmenta berri bat gehitzen dio.
	 *
	 * @param title Produktuaren izenburua.
	 * @param description Produktuaren deskribapena.
	 * @param status Produktuaren egoera.
	 * @param price Salmenta-prezioa.
	 * @param pubDate Argitalpen-data.
	 * @param sellerEmail Saltzailearen emaila.
	 * @param file Produktuari lotutako fitxategia.
	 * @return Sortutako salmenta.
	 * @throws FileNotUploadedException Fitxategia ezin bada igo.
	 * @throws MustBeLaterThanTodayException Data baliogabea bada.
	 * @throws SaleAlreadyExistException Salmenta lehendik existitzen bada.
	 */
	@WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail,
			File file) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException;

	/**
	 * Testu baten arabera salmentak bilatzen ditu.
	 *
	 * @param desc Bilatzeko testua.
	 * @return Testu hori duten salmenten zerrenda.
	 */
	@WebMethod
	public List<Sale> getSales(String desc);

	/**
	 * Tituluan testu jakin bat duten eta data jakin bat baino lehen edo egun
	 * berean argitaratutako salmentak lortzen ditu.
	 *
	 * @param desc Bilatzeko testua.
	 * @param pubDate Argitalpen-data muga.
	 * @return Baldintzak betetzen dituzten salmenten zerrenda.
	 */
	@WebMethod
	public List<Sale> getPublishedSales(String desc, Date pubDate);

	/**
	 * Datu-basea hasierako datuekin inicializatzen du.
	 *
	 * Metodo hau soilik erabiltzen da konfigurazio-fitxategian hala adierazita
	 * dagoenean.
	 */
	@WebMethod
	public void initializeBD();

	/**
	 * Irudi bat deskargatzen du izenaren arabera.
	 *
	 * @param imageName Irudiaren izena.
	 * @return Kargatutako irudia.
	 */
	@WebMethod
	public Image downloadImage(String imageName);

	/**
	 * Erabiltzaile baten saioa balidatzen du.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @param pass Erabiltzailearen pasahitza.
	 * @return Erabiltzailea, datuak zuzenak badira.
	 */
	@WebMethod
	public User isLogged(String email, String pass);

	/**
	 * Erabiltzaile erregistratu berri bat sortzen du.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @param erabiltzailea Erabiltzaile-izena.
	 * @param pass Pasahitza.
	 * @return Sortutako erabiltzaile erregistratua.
	 * @throws EntityExistsException Erabiltzailea lehendik existitzen bada.
	 */
	@WebMethod
	public Registered erabiltzaileaSortu(String email, String erabiltzailea, String pass) throws EntityExistsException;

	/**
	 * Saltzaile bat bere izenaren arabera bilatzen du.
	 *
	 * @param izena Saltzailearen izena.
	 * @return Aurkitutako saltzailea.
	 */
	@WebMethod
	public Registered getSellerByName(String izena);

	/**
	 * Saltzaile bat bere emailaren arabera bilatzen du.
	 *
	 * @param email Saltzailearen emaila.
	 * @return Aurkitutako saltzailea.
	 */
	@WebMethod
	public Registered getSellerByEmail(String email);

	/**
	 * Erabiltzaile baten gogokoen zerrenda lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Gogokoen zerrenda.
	 */
	@WebMethod
	public List<Sale> getFaboritoak(String saltzaileaId);

	/**
	 * Erabiltzaile baten erositako produktuen zerrenda lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Erositako produktuen zerrenda.
	 */
	@WebMethod
	public List<BoughtSale> getErositakoak(String saltzaileaId);

	/**
	 * Produktu bat erosten du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Eroslea edo erabiltzailearen identifikatzailea.
	 * @param bidalketaOrdaindu Bidalketa ordaindu den ala ez.
	 * @return Erosketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean produktoaErosi(Integer produktoaNumber, String saltzaileaId, boolean bidalketaOrdaindu);

	/**
	 * Erositako salmenta bat lortzen du.
	 *
	 * @param erosleEmail Eroslearen emaila.
	 * @param title Salmentaren izenburua.
	 * @return Erositako salmenta.
	 */
	@WebMethod
	BoughtSale lortuErositakoSalmenta(String erosleEmail, String title);

	/**
	 * Produktu bat gogokoetan dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Produktua gogokoetan badago true.
	 */
	@WebMethod
	public boolean faboritoaDa(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Produktu bat gogokoetara gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean faboritoaGehitu(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Produktu bat erositakoen zerrendara gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void erositakoaGehitu(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Produktu bat gogokoetatik ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean faboritoaEzabatu(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Saltzaile baten salmentak lortzen ditu.
	 *
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @param query Bilaketako testua.
	 * @param date Data-iragazkia.
	 * @return Salmenten zerrenda.
	 */
	@WebMethod
	public List<Sale> getSalesBySeller(String saltzaileaId, String query, Date date);

	/**
	 * Salmenta bat ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean removeSale(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Erabiltzaile baten kontutik dirua ateratzen du.
	 *
	 * @param kantitatea Atera beharreko kopurua.
	 * @param userId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean diruaAtera(Double kantitatea, String userId);

	/**
	 * Erabiltzaile baten kontuan dirua sartzen du.
	 *
	 * @param kantitatea Sartu beharreko kopurua.
	 * @param userId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void diruaSartu(Double kantitatea, String userId);

	/**
	 * Saltzaile batek saldutako produktuak lortzen ditu.
	 *
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @param query Bilaketako testua.
	 * @param date Data-iragazkia.
	 * @return Saltzaileak saldutako produktuen zerrenda.
	 */
	@WebMethod
	public List<BoughtSale> getSoldBySeller(String saltzaileaId, String query, Date date);

	/**
	 * Erabiltzaile baten mugimenduak lortzen ditu.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @param query Bilaketako testua.
	 * @param date Data-iragazkia.
	 * @return Mugimenduen zerrenda.
	 */
	@WebMethod
	public List<Mugimendua> getMugimenduakBySeller(String saltzaileaId, String query, Date date);

	/**
	 * Erreklamazio berri bat sortzen du.
	 *
	 * @param title Erreklamazioaren izenburua.
	 * @param deskribapena Erreklamazioaren deskribapena.
	 * @param sortzaileEmaila Sortzailearen emaila.
	 * @param jasotzaileEmaila Jasotzailearen emaila.
	 * @param pubDate Argitalpen-data.
	 * @param produktoa Erositako produktua.
	 * @throws MustBeLaterThanTodayException Data baliogabea bada.
	 * @throws ErreklamazioaAlreadyExistException Erreklamazioa lehendik existitzen bada.
	 */
	@WebMethod
	public void erreklamazioaSortu(String title, String deskribapena, String sortzaileEmaila,
			String jasotzaileEmaila, Date pubDate, BoughtSale produktoa)
			throws MustBeLaterThanTodayException, ErreklamazioaAlreadyExistException;

	/**
	 * Erreklamazio bat lortzen du.
	 *
	 * @param erosleEmail Eroslearen emaila.
	 * @param title Erreklamazioaren izenburua.
	 * @return Aurkitutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa lortuErreklamazioa(String erosleEmail, String title);

	/**
	 * Erreklamazio bati disputa-mezu bat gehitzen dio.
	 *
	 * @param idErreklamazioa Erreklamazioaren identifikatzailea.
	 * @param emailIgorlea Mezua bidaltzen duenaren emaila.
	 * @param testua Mezuaren edukia.
	 * @return Eguneratutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa disputaMezuaGehitu(Integer idErreklamazioa, String emailIgorlea, String testua);

	/**
	 * Erreklamazio baten egoera aldatzen du.
	 *
	 * @param idErreklamazioa Erreklamazioaren identifikatzailea.
	 * @param egoeraBerria Egoera berria.
	 * @return Eguneratutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa erreklamazioaEgoeraAldatu(Integer idErreklamazioa, int egoeraBerria);

	/**
	 * Eskalatutako erreklamazio guztiak lortzen ditu.
	 *
	 * @return Erreklamazioen zerrenda.
	 */
	@WebMethod
	public List<Erreklamazioa> getErreklamazioEskalatuak();

	/**
	 * Erabiltzaile bati notifikazio bat bidaltzen dio.
	 *
	 * @param email Jasotzailearen emaila.
	 * @param mezua Bidali beharreko mezua.
	 */
	@WebMethod
	public void notifikazioaBidali(String email, String mezua);

	/**
	 * Erabiltzaile batek irakurri gabeko notifikazioak dituen egiaztatzen du.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @return Irakurri gabeko notifikazioak badaude true.
	 */
	@WebMethod
	boolean badituIrakurriGabekoak(String email);

	/**
	 * Erabiltzaile baten notifikazioak irakurritzat markatzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 */
	@WebMethod
	public void markatuIrakurrita(String email);

	/**
	 * Erabiltzaile baten notifikazioak lortzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @return Notifikazioen zerrenda.
	 */
	@WebMethod
	public List<Notifikazioa> getNotifikazioak(String email);

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
			throws MustBeLaterThanTodayException, ErreklamazioaAlreadyExistException;

	/**
	 * Salaketa baten egoera aldatzen du.
	 *
	 * @param idErreklamazioa Salaketaren identifikatzailea.
	 * @param egoeraBerria Egoera berria.
	 */
	@WebMethod
	public void salaketaEgoeraAldatu(Integer idErreklamazioa, int egoeraBerria);

	/**
	 * Produktu bat salatuta dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 * @return Produktua salatuta badago true.
	 */
	@WebMethod
	public boolean salatutaDago(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Salaketa guztiak lortzen ditu.
	 *
	 * @return Salaketen zerrenda.
	 */
	@WebMethod
	public List<Salaketa> getSalaketak();

	/**
	 * Produktu bat ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Saltzailearen identifikatzailea.
	 */
	@WebMethod
	public void produktoaEzabatu(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Erabiltzaile-kontu bat banatzen du.
	 *
	 * @param registeredId Erregistratutako erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void kontuaBaneatu(String registeredId);

	/**
	 * Erabiltzaile-kontu bati debekua kentzen dio.
	 *
	 * @param registeredId Erregistratutako erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void kontuaDesbaneatu(String registeredId);

	/**
	 * Erregistratutako erabiltzaileen zerrenda lortzen du.
	 *
	 * @param desc Bilaketako testua.
	 * @return Erabiltzaileen zerrenda.
	 */
	@WebMethod
	public List<Registered> getRegisteredUsers(String desc);

	/**
	 * Salmenta, saltzailea eta erosketa lotzen dituen edukiontzia lortzen du.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @return Lotutako datuen edukiontzia.
	 */
	@WebMethod
	public SaleSellerBoughtContainer getSaleSellerBoughtContainer(Integer saleNumber);

	/**
	 * Erreklamazio bati lotutako saltzailea lortzen du.
	 *
	 * @param erreklamazioa Erreklamazioa.
	 * @return Saltzailea.
	 */
	@WebMethod
	public User getSellerByErreklamazioa(Erreklamazioa erreklamazioa);

	/**
	 * Erreklamazio bati lotutako eroslea lortzen du.
	 *
	 * @param erreklamazioa Erreklamazioa.
	 * @return Eroslea.
	 */
	@WebMethod
	public User getBuyerByErreklamazioa(Erreklamazioa erreklamazioa);

	/**
	 * Salaketa bati lotutako salatzailea lortzen du.
	 *
	 * @param salaketa Salaketa.
	 * @return Salatzailea.
	 */
	@WebMethod
	public User getSalatzaileaBySalaketa(Salaketa salaketa);

	/**
	 * Disputa-mezu baten igorlea lortzen du.
	 *
	 * @param disputaMezua Disputa-mezua.
	 * @return Igorlea.
	 */
	@WebMethod
	public User getIgorleaByDM(DisputaMezua disputaMezua);

	/**
	 * Erreklamazio bat bere identifikatzailearen arabera lortzen du.
	 *
	 * @param kexaNumber Erreklamazioaren identifikatzailea.
	 * @return Aurkitutako erreklamazioa.
	 */
	@WebMethod
	public Erreklamazioa lortuErreklamazioById(Integer kexaNumber);

	/**
	 * Salmenta bat erreserbatzen du.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @param vipId VIP erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saleErreserbatu(Integer saleNumber, Integer vipId);

	/**
	 * Salmenta baten erreserba kentzen du.
	 *
	 * @param saleNumber Salmentaren identifikatzailea.
	 * @param vipId VIP erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saleDesErreserbatu(Integer saleNumber, Integer vipId);

	/**
	 * Produktu bat erreserbatuta dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param vipId VIP erabiltzailearen identifikatzailea.
	 * @return Produktua erreserbatuta badago true.
	 */
	@WebMethod
	public boolean erreserbatutaDago(Integer produktoaNumber, Integer vipId);

	/**
	 * Erabiltzaile bati harpidetza aplikatzen dio.
	 *
	 * @param harId Harpidetzaren identifikatzailea.
	 */
	@WebMethod
	public void harpidetu(String harId);

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
	public Eskaera eskaeraSortu(String title, String desc, java.util.Date pubDate, String eskatzaileEmail);

	/**
	 * Eskaera guztiak lortzen ditu.
	 *
	 * @return Eskaeren zerrenda.
	 */
	@WebMethod
	public java.util.List<Eskaera> getEskaerak();

	/**
	 * Eskaera bati eskaintza bat gehitzen dio.
	 *
	 * @param eskaeraId Eskaeraren identifikatzailea.
	 * @param saleId Salmentaren identifikatzailea.
	 * @param saltzaileEmail Saltzailearen emaila.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean eskaintzaGehitu(Integer eskaeraId, Integer saleId, String saltzaileEmail);

	/**
	 * Erabiltzaile baten eskaerak lortzen ditu.
	 *
	 * @param email Erabiltzailearen emaila.
	 * @return Erabiltzailearen eskaeren zerrenda.
	 */
	@WebMethod
	public List<Eskaera> getZureEskaerak(String email);

	/**
	 * Produktu bat saskitik ezabatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean saskitikEzabatu(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Produktu bat saskira gehitzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Eragiketa ondo egin den ala ez.
	 */
	@WebMethod
	public boolean saskianGehitu(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Produktu bat saskian dagoen egiaztatzen du.
	 *
	 * @param produktoaNumber Produktuaren identifikatzailea.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Produktua saskian badago true.
	 */
	@WebMethod
	public boolean saskianDago(Integer produktoaNumber, String saltzaileaId);

	/**
	 * Erabiltzaile baten saskiko produktuen zerrenda lortzen du.
	 *
	 * @param desc Bilaketako testua.
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Saskiko produktuen zerrenda.
	 */
	@WebMethod
	public List<Sale> getSaskiaLista(String desc, String saltzaileaId);

	/**
	 * Erabiltzaile baten saskiaren prezio totala lortzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Prezio totala.
	 */
	@WebMethod
	public float getSaskiaPrezioTotala(String saltzaileaId);

	/**
	 * Saskiko produktu guztiak erosten ditu.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saskikoProduktuakErosi(String saltzaileaId);

	/**
	 * Erabiltzaile baten saskia husten du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 */
	@WebMethod
	public void saskiaHustu(String saltzaileaId);

	/**
	 * Salmentak dituzten erabiltzaile erregistratuak lortzen ditu.
	 *
	 * @param desc Bilaketako testua.
	 * @param user1 Kontsultan erabiltzen den erabiltzaile-identifikatzailea.
	 * @return Erabiltzaileen zerrenda.
	 */
	@WebMethod
	public List<Registered> getRegisteredUsersWithSales(String desc, String user1);

	/**
	 * Saski baten bidalketa-kostua lortzen du.
	 *
	 * @param erosleaId Eroslearen identifikatzailea.
	 * @param saltzaileId Saltzailearen identifikatzailea.
	 * @return Bidalketa-kostua.
	 */
	@WebMethod
	public double getSaskiaBidalketaKostua(String erosleaId, String saltzaileId);

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
			String herrialdea);

	/**
	 * Erabiltzaile bat VIP den egiaztatzen du.
	 *
	 * @param saltzaileaId Erabiltzailearen identifikatzailea.
	 * @return Erabiltzailea VIP bada true.
	 */
	@WebMethod
	public boolean vipDa(String saltzaileaId);

}