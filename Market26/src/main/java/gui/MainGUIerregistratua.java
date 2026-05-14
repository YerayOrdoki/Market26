package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;
import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import domain.*;
import extra.BotoiBorobila;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Erregistratutako erabiltzaile baten leiho nagusia. 
 * Hemendik produktuak sortu, bilatu, faboritoak ikusi, profila kudeatu, 
 * bere salmentak ikusi eta ESKAERAK (Demandak) kudeatu ditzake.
 */
public class MainGUIerregistratua extends JFrame {
	
	private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private static BLFacade appFacadeInterface;
    private String saltzaileEmaila;
	private BLFacade facade;
	private ErreserbatuakGUI zureErreserbakLeihoa;
	private JFrame queryUsersLeihoa = null;
	
	private JPanel edukiPanela = null;
	
	// Salmenten botoiak (Klasikoak)
	private BotoiBorobila salmentaSortuBotoia;
	private BotoiBorobila kontsultakEginBotoia;
	private BotoiBorobila faboritoakBotoia; 
	private BotoiBorobila zureSalmentakBotoia;
	private BotoiBorobila btnSaskia;

	// Eskaeren botoi BERRIAK
	private BotoiBorobila eskaerakIkusiBotoia;
	private BotoiBorobila eskaeraSortuBotoia;
	private BotoiBorobila zureEskaerakBotoia;
	
	// Nabigazio botoiak
	private BotoiBorobila profilaBotoia;
	private BotoiBorobila saioaItxiBotoia;

	protected JLabel aukeratuEtiketa;
	JLabel alertaGorriEtiketa = new JLabel("!!");
	
	private JRadioButton ingelesItzulpenBotoia, euskaraItzulpenBotoia, gaztelaniaItzulpenBotoia;
	private final ButtonGroup botoiTaldea = new ButtonGroup();
	
	private JPanel panela, panela5, panela4, panela3, panela2, panela1;
	public BotoiBorobila btnErreserbatuak;
	private JLabel lblSales;
	private JSeparator separator_1;
	private JLabel lblEkintzak;
	private JSeparator separator_2;
	private JLabel lblEskaerak;

    // --- ERAIKITZAILEA ---

	public MainGUIerregistratua(Registered erabiltzailea, JFrame gurasoa) {
		super();
		this.saltzaileEmaila = erabiltzailea.getEmail();
		this.facade = gui.MainGUI.getBusinessLogic();
		
		// Leihoaren tamaina estandarra (6. lerroa sartzeko egokitua)
		this.setSize(800, 650); 
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		// --- HIZKUNTZA BOTOIAK ---
		ingelesItzulpenBotoia = new JRadioButton("English");
		ingelesItzulpenBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ingelesItzulpenBotoia.setBounds(598, 56, 146, 23);
		ingelesItzulpenBotoia.addActionListener(e -> {
			Locale.setDefault(new Locale("en"));
			paintAgain();				
		});
		
		euskaraItzulpenBotoia = new JRadioButton("Euskara");
		euskaraItzulpenBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		euskaraItzulpenBotoia.setBounds(113, 56, 159, 23);
		euskaraItzulpenBotoia.addActionListener(e -> {
			Locale.setDefault(new Locale("eus"));
			paintAgain();				
		});
		
		gaztelaniaItzulpenBotoia = new JRadioButton("Castellano");
		gaztelaniaItzulpenBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		gaztelaniaItzulpenBotoia.setBounds(343, 56, 191, 23);
		gaztelaniaItzulpenBotoia.addActionListener(e -> {
			Locale.setDefault(new Locale("es"));
			paintAgain();
		});
	
		botoiTaldea.add(ingelesItzulpenBotoia);
		botoiTaldea.add(euskaraItzulpenBotoia);
		botoiTaldea.add(gaztelaniaItzulpenBotoia);
	
		panela = new JPanel(null);
		panela.add(euskaraItzulpenBotoia);
		panela.add(gaztelaniaItzulpenBotoia);
		panela.add(ingelesItzulpenBotoia);
		
		// --- EDUKI PANELA (6 lerroko GridLayout-a eskaerak sartzeko) ---
		edukiPanela = new JPanel();
		edukiPanela.setLayout(new GridLayout(5, 1, 0, 0)); 
		
		// PANELA 4: Goiburua (Profila, Buzoia, Logout)
		panela4 = new JPanel(null);
		aukeratuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption")); 
		aukeratuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		aukeratuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		aukeratuEtiketa.setBounds(121, 47, 546, 55);
		panela4.add(aukeratuEtiketa);
		
		profilaBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Perfila")); 
		profilaBotoia.setBounds(10, 19, 158, 37);
		profilaBotoia.addActionListener(e -> {
			this.setVisible(false);
            new PerfilaGUI(erabiltzailea, this).setVisible(true);
		});
		panela4.add(profilaBotoia);
		
		saioaItxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Logout")); 
		saioaItxiBotoia.setColourRED();
		saioaItxiBotoia.setBounds(611, 18, 165, 36);
		saioaItxiBotoia.addActionListener(e -> {
			this.setVisible(false);
			new MainGUI(null).setVisible(true);
		});
		panela4.add(saioaItxiBotoia);
		
		alertaGorriEtiketa.setBounds(133, 24, 29, 30);
		alertaGorriEtiketa.setForeground(Color.RED);
		alertaGorriEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panela4.add(alertaGorriEtiketa);
		eguneratuBuzoia();
		edukiPanela.add(panela4);
		
		separator_2 = new JSeparator();
		separator_2.setForeground(Color.BLACK);
		separator_2.setBackground(Color.BLACK);
		separator_2.setBounds(10, 99, 768, 13);
		panela4.add(separator_2);
		
		
		
		
		
		
		
		// PANELA 5: ESKAERAK (Hiru botoi horizontalki)
		panela5 = new JPanel(null);
		eskaerakIkusiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Title")); 
		eskaerakIkusiBotoia.setBounds(10, 43, 218, 68);
		eskaerakIkusiBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		eskaerakIkusiBotoia.addActionListener(e -> {
			this.setVisible(false);
			new EskaerakGUI(erabiltzailea, this).setVisible(true);
		});
		panela5.add(eskaerakIkusiBotoia);
		
		eskaeraSortuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaeraSortu"));
		eskaeraSortuBotoia.setBounds(286, 43, 218, 69);
		eskaeraSortuBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		eskaeraSortuBotoia.addActionListener(e -> {
			this.setVisible(false);
			new CreateEskaeraGUI(erabiltzailea, this).setVisible(true);
		});
		panela5.add(eskaeraSortuBotoia);
		
		zureEskaerakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.ZureEskaerak"));
		zureEskaerakBotoia.setBounds(560, 43, 218, 69);
		zureEskaerakBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		zureEskaerakBotoia.addActionListener(e -> {
			this.setVisible(false);
			new ZureEskaerakGUI(erabiltzailea, this).setVisible(true);
		});
		panela5.add(zureEskaerakBotoia);
		edukiPanela.add(panela5);
		
		lblEskaerak = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Eskaerak")); //$NON-NLS-1$ //$NON-NLS-2$
		lblEskaerak.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblEskaerak.setBounds(0, 0, 788, 25);
		lblEskaerak.setHorizontalAlignment(SwingConstants.CENTER);
		panela5.add(lblEskaerak);
		
		
		Registered r=facade.getSellerByEmail(saltzaileEmaila);
		//btnErreserbatuak.setVisible(r.getVip()!=null);null bada akatsa ez emateko guira itzulyzean jarri behar da.
		// PANELA 3: Produktu Salmenta
		panela3 = new JPanel(null);
		salmentaSortuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		salmentaSortuBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		salmentaSortuBotoia.setBounds(10, 43, 218, 68);
		salmentaSortuBotoia.addActionListener(e -> {
			this.setVisible(false);
			new CreateSaleGUI(erabiltzailea, this).setVisible(true);
		});
		panela3.add(salmentaSortuBotoia);
		
		zureSalmentakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.YourSales")); 
		zureSalmentakBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		zureSalmentakBotoia.setBounds(560, 43, 218, 69);
		zureSalmentakBotoia.addActionListener(e -> {
			this.setVisible(false);
			new YourSalesGUI(erabiltzailea, null, this).setVisible(true);
		});
		panela3.add(zureSalmentakBotoia);
		edukiPanela.add(panela3);
		kontsultakEginBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		kontsultakEginBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		kontsultakEginBotoia.setBounds(286, 43, 218, 69);
		panela3.add(kontsultakEginBotoia);
		
		lblSales = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Ofertak"));
		lblSales.setHorizontalAlignment(SwingConstants.CENTER);
		lblSales.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblSales.setBounds(0, 0, 788, 25);
		panela3.add(lblSales);
		kontsultakEginBotoia.addActionListener(e -> {
			this.setVisible(false);
			new QuerySalesGUI(erabiltzailea, null, this).setVisible(true);
		});
		
		// PANELA 2: Produktuak Kontsultatu
		panela2 = new JPanel(null);
		edukiPanela.add(panela2);
		faboritoakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Favorites"));
		faboritoakBotoia.setFont(new Font("Tahoma", Font.BOLD, 14));
		faboritoakBotoia.setBounds(10, 43, 218, 68);
		panela2.add(faboritoakBotoia);
		
		btnSaskia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Saskia"));
		btnSaskia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUIerregistratua.this.setVisible(false);
				queryUsersLeihoa = new QueryUsersGUI(erabiltzailea, MainGUIerregistratua.this);
				queryUsersLeihoa.setVisible(true);
			}
		});
		btnSaskia.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSaskia.setBounds(286, 43, 218, 69);
		panela2.add(btnSaskia);
		
		btnErreserbatuak = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Erreserbak"));
		btnErreserbatuak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUIerregistratua.this.setVisible(false);
				zureErreserbakLeihoa= new ErreserbatuakGUI(erabiltzailea, MainGUIerregistratua.this);
				zureErreserbakLeihoa.setVisible(true);
			}
		});
		btnErreserbatuak.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnErreserbatuak.setBounds(560, 43, 218, 69);
		panela2.add(btnErreserbatuak);
		
		lblEkintzak = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Ekintzak"));
		lblEkintzak.setHorizontalAlignment(SwingConstants.CENTER);
		lblEkintzak.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblEkintzak.setBounds(0, 0, 788, 25);
		panela2.add(lblEkintzak);
		faboritoakBotoia.addActionListener(e -> {
		    this.setVisible(false);
		    new FaboritoGUI(erabiltzailea, this).setVisible(true);
		});
		Registered lagun=facade.getSellerByEmail(saltzaileEmaila);
		btnErreserbatuak.setVisible(lagun.getVip()!=null);
		
		/*btnErreserbatuak = new JButton("erreserbatuak"); //$NON-NLS-1$ //$NON-NLS-2$
		btnErreserbatuak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUIerregistratua.this.setVisible(false);
				zureErreserbakLeihoa= new ErreserbatuakGUI(erabiltzailea, MainGUIerregistratua.this);
				zureErreserbakLeihoa.setVisible(true);
			}
		});
		btnErreserbatuak.setBounds(331, 24, 105, 27);
		panela4.add(btnErreserbatuak);
		Registered lagun=facade.getSellerByEmail(saltzaileEmaila);
		btnErreserbatuak.setVisible(lagun.getVip()!=null);*/
		
		// PANELA 1: Faboritoak
		//panela1 = new JPanel(null);
		//edukiPanela.add(panela1);
		
		// Azken lerroa: Hizkuntzak
		edukiPanela.add(panela);
		
		separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLACK);
		separator_1.setBackground(Color.BLACK);
		separator_1.setBounds(10, 11, 768, 13);
		panela.add(separator_1);
		
		setContentPane(edukiPanela);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + saltzaileEmaila);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { System.exit(1); }
		});
	}
	
    // --- METODOAK ---

	public static BLFacade getBusinessLogic() { return appFacadeInterface; }
	public static void setBussinessLogic(BLFacade facade) { appFacadeInterface = facade; }
	
	public void eguneratuBuzoia() {
		alertaGorriEtiketa.setVisible(facade.badituIrakurriGabekoak(saltzaileEmaila));
	}
	
	private void paintAgain() {
		ResourceBundle rb = ResourceBundle.getBundle("Etiquetas");
		zureSalmentakBotoia.setText(rb.getString("MainGUIerregistratua.YourSales"));
		profilaBotoia.setText(rb.getString("PerfilaGUI.MainTitle"));
		saioaItxiBotoia.setText(rb.getString("MainGUIerregistratua.Logout"));
		aukeratuEtiketa.setText(rb.getString("MainGUI.SelectOption"));
		kontsultakEginBotoia.setText(rb.getString("MainGUI.QuerySales"));
		salmentaSortuBotoia.setText(rb.getString("MainGUI.CreateSale"));
		faboritoakBotoia.setText(rb.getString("MainGUI.Favorites"));
		btnSaskia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Saskia"));
		btnErreserbatuak.setText(rb.getString("MainGUIerregistratua.Erreserbak"));
		lblEskaerak.setText(rb.getString("MainGUIerregistratua.Eskaerak"));
		lblSales.setText(rb.getString("MainGUIerregistratua.Ofertak"));
		lblEkintzak.setText(rb.getString("MainGUIerregistratua.Ekintzak"));
		// Eskaeren ataleko testuak
		eskaerakIkusiBotoia.setText(rb.getString("EskaeraGUI.Title"));
		eskaeraSortuBotoia.setText(rb.getString("EskaeraGUI.EskaeraSortu"));
		zureEskaerakBotoia.setText(rb.getString("EskaeraGUI.ZureEskaerak"));
		
		this.setTitle(rb.getString("MainGUI.MainTitle") + ": " + saltzaileEmaila);
	}
}
