package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;// Asegúrate de importar tu nueva clase
import extra.BotoiBorobila;

/**
 * Erreklamazio baten gatazka-haria (txata) kudeatzen duen interfaze grafikoa.
 * Erosleak, saltzaileak eta administratzaileak mezuak trukatzeko eta erabakiak
 * hartzeko balio du.
 */
public class ErreklamazioHariaGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// --- ATRIBUTUAK ---
	private JPanel edukiPanela;
	private JFrame gurasoa;
	private BLFacade facade;

	private Erreklamazioa erreklamazioa;
	private User erabiltzaileAktiboa; // Aplikazioa une honetan erabiltzen ari den pertsona

	private JTextArea txatEremua;
	private JTextField mezuSarrera;
	private BotoiBorobila bidaliBotoia;
	private BotoiBorobila eskalatuBotoia;
	private BotoiBorobila onartuBotoia;
	private JLabel egoeraEtiketa;

	// --- ERAIKITZAILEA ---

	/**
	 * ErreklamazioHariaGUI klasearen eraikitzailea. Leihoaren ezarpenak
	 * konfiguratzen ditu eta erabiltzaile motaren arabera (Administratzailea ala
	 * arrunta) dagozkion aukerak eta botoiak erakusten ditu. * @param erreklamazioa
	 * Eztabaidatzen ari den erreklamazioaren objektua.
	 * 
	 * @param erabiltzaileAktiboa Saioa hasi duen erabiltzailea (Eroslea, Saltzailea
	 *                            edo Admin).
	 * @param gurasoa             Aurreko leihoa (nondik gatozen).
	 */
	public ErreklamazioHariaGUI(Erreklamazioa erreklamazioa, User erabiltzaileAktiboa, JFrame gurasoa) {
		
		this.erabiltzaileAktiboa = erabiltzaileAktiboa;
		this.gurasoa = gurasoa;
		this.facade = MainGUI.getBusinessLogic();
		this.erreklamazioa=facade.lortuErreklamazioById(erreklamazioa.getKexaNumber());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 600));
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.ErreklamazioHaria") + ":"
				+ erreklamazioa.getTitle());

		if (gurasoa != null) {
			this.setLocation(gurasoa.getLocation());
		}

		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(null);

		// --- IZENBURUA ETA EGOERA ---
		JLabel izenburuEtiketa = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Erreklamazioa") + ":"
						+ erreklamazioa.getTitle());
		izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		izenburuEtiketa.setBounds(30, 20, 500, 30);
		edukiPanela.add(izenburuEtiketa);

		 egoeraEtiketa= new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status") + ":"
				+ getEgoeraString(erreklamazioa.getEgoera()));
		egoeraEtiketa.setFont(new Font("Tahoma", Font.ITALIC, 14));
		egoeraEtiketa.setBounds(30, 50, 300, 20);
		if (erreklamazioa.getEgoera() == 1) {
			egoeraEtiketa.setForeground(Color.RED); // Eskalatuta badago, gorriz
		}
		edukiPanela.add(egoeraEtiketa);

		// --- MEZUEN HISTORIALAREN EREMUA ---
		txatEremua = new JTextArea();
		txatEremua.setEditable(false);
		txatEremua.setFont(new Font("Monospaced", Font.PLAIN, 14));
		txatEremua.setLineWrap(true);
		txatEremua.setWrapStyleWord(true);

		JScrollPane korritzePanela = new JScrollPane(txatEremua);
		korritzePanela.setBounds(30, 80, 720, 300);
		edukiPanela.add(korritzePanela);

		// Leihoa irekitzean dauden mezuak kargatzen dira
		kargatuMezuak();

		// --- MEZUA SARTZEKO EREMUA ---
		mezuSarrera = new JTextField();
		mezuSarrera.setBounds(30, 400, 580, 40);
		edukiPanela.add(mezuSarrera);

		bidaliBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Bidali"));
		bidaliBotoia.setBounds(620, 400, 130, 40);
		bidaliBotoia.addActionListener(e -> bidaliMezua());
		edukiPanela.add(bidaliBotoia);

		// --- EKINTZA BOTOIAK (BEHEKO ALDEA) ---

		// Leihoa irekitzen duen erabiltzailea Administratzailea ala Saltzailea den
		// egiaztatzen dugu
		boolean administratzaileaDa = erabiltzaileAktiboa.getClass().getSimpleName().equals("Admin");
		User laguntzaile=facade.getSellerByErreklamazioa(erreklamazioa);
		boolean saltzaileaDa = !administratzaileaDa
				&& erabiltzaileAktiboa.getEmail().equals(laguntzaile.getEmail());

		if (administratzaileaDa) {
			// ADMINISTRATZAILEAREN INTERFAZEA: Bakarrik irakurri eta erabaki dezake
			mezuSarrera.setVisible(false);
			bidaliBotoia.setVisible(false);

			BotoiBorobila eroslearenAldekoBotoia = new BotoiBorobila(
					ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.ArrazoiaI"));
			eroslearenAldekoBotoia.setColourBLUE();
			eroslearenAldekoBotoia.setBounds(30, 480, 250, 40);
			eroslearenAldekoBotoia.addActionListener(e -> {
				facade.erreklamazioaEgoeraAldatu(erreklamazioa.getKexaNumber(), 2); // 2 = Devolución
				JOptionPane.showMessageDialog(this,
						ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.DiruaItzuli"));
				itxiEtaItzuli();
			});
			edukiPanela.add(eroslearenAldekoBotoia);

			BotoiBorobila saltzailearenAldekoBotoia = new BotoiBorobila(
					ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.ArrazoiaU"));
			saltzailearenAldekoBotoia.setColourRED();
			saltzailearenAldekoBotoia.setBounds(290, 480, 250, 40);
			saltzailearenAldekoBotoia.addActionListener(e -> {
				facade.erreklamazioaEgoeraAldatu(erreklamazioa.getKexaNumber(), 3); // 3 = Denegada
				JOptionPane.showMessageDialog(this,
						ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Ukatu"));
				itxiEtaItzuli();
			});
			edukiPanela.add(saltzailearenAldekoBotoia);

		} else {
			// ERABILTZAILE ARRUNTAREN INTERFAZEA

			eskalatuBotoia = new BotoiBorobila(
					ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.AdminAbisatu"));
			eskalatuBotoia.setColourRED();
			eskalatuBotoia.setBounds(30, 480, 200, 40);
			eskalatuBotoia.addActionListener(e -> eskalatuErreklamazioa());
			edukiPanela.add(eskalatuBotoia);

			onartuBotoia = new BotoiBorobila(
					ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Onartu"));
			onartuBotoia.setBounds(250, 480, 200, 40);
			onartuBotoia.addActionListener(e -> onartuItzulketa());
			onartuBotoia.setVisible(saltzaileaDa);
			edukiPanela.add(onartuBotoia);
		}

		// Atzera Botoia
		BotoiBorobila atzeraBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
		atzeraBotoia.setBounds(620, 480, 130, 40);
		atzeraBotoia.addActionListener(e -> {
			this.setVisible(false);
			if (gurasoa != null)
				gurasoa.setVisible(true);
		});
		edukiPanela.add(atzeraBotoia);

		// Produktua Ikusi botoia
		BotoiBorobila ProduktuaIkusiBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
		ProduktuaIkusiBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (erreklamazioa != null) {

					// 1. Datu-basetik jatorrizko erosketa lortzen dugu
					User laguntzaile=facade.getBuyerByErreklamazioa(erreklamazioa);
					BoughtSale jatorrizkoSalmenta = facade.lortuErositakoSalmenta(
							laguntzaile.getEmail(), erreklamazioa.getTitle());

					if (jatorrizkoSalmenta != null) {
						// 2. Leihoa irekitzen dugu. (Eroslea pasatzen diogu erabiltzaile gisa,
						// Admina izanda ere erroreak ekiditeko "irakurtzeko soilik" izango delako).
						ErositakoaShowSaleGUI produktuLeihoa = new ErositakoaShowSaleGUI(jatorrizkoSalmenta,
								(Registered) facade.getBuyerByErreklamazioa(erreklamazioa), ErreklamazioHariaGUI.this);

						// 3. Botoiak ezkutatu, bakarrik ikusi ahal izateko
						produktuLeihoa.ezkutatuErreklamazioBotoia();

						produktuLeihoa.setVisible(true);
						ErreklamazioHariaGUI.this.setVisible(false);

					} else {
						JOptionPane.showMessageDialog(ErreklamazioHariaGUI.this,
								ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.EzAurkitu"),
								ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Error"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		ProduktuaIkusiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Produktua"));
		ProduktuaIkusiBotoia.setBounds(562, 20, 188, 40);
		edukiPanela.add(ProduktuaIkusiBotoia);

		// Negoziatzen ez badabiltza (Egoera != 0), interfazea blokeatu
		egiaztatuEgoera();
	}

	// --- METODOAK ETA LOGIKA ---

	/**
	 * Datu-basetik erreklamazioaren mezu guztiak eskuratu eta txataren eremuan
	 * bistaratzen ditu.
	 */
	private void kargatuMezuak() {
		txatEremua.setText(""); // Garbitu
		SimpleDateFormat dataFormatua = new SimpleDateFormat("dd/MM HH:mm");
		System.out.println(erreklamazioa.getMezuak());
		// Erreklamazioa objektuak List<DisputaMezua> bueltatzen duela suposatuz
		if (erreklamazioa.getMezuak() != null) {
			for (DisputaMezua m : erreklamazioa.getMezuak()) {
				
			System.out.println(m.getIgorlea());
			User igorlea=facade.getIgorleaByDM(m);
				if (igorlea != null) {
					String dataStr = dataFormatua.format(m.getData().getTime());
					System.out.println(igorlea.getName());
					String igorleaStr = (igorlea.getName()); // Nork bidaltzen duen
					txatEremua.append("[" + dataStr + "] " + igorleaStr + ":\n");
					txatEremua.append(m.getTextua() + "\n\n");
				}
			}
		}
		// Txataren korritze-barra azken mezura jaitsi automatikoki
		txatEremua.setCaretPosition(txatEremua.getDocument().getLength());
	}

	/**
	 * Erabiltzaileak idatzitako mezua datu-basean gorde eta txata freskatzen du.
	 */
	private void bidaliMezua() {
		String testua = mezuSarrera.getText().trim();
		if (testua.isEmpty())
			return;

		// Mezua gordetzeko datu-baseari dei egiten diogu (Facade bidez)
		erreklamazioa = facade.disputaMezuaGehitu(erreklamazioa.getKexaNumber(), erabiltzaileAktiboa.getEmail(),
				testua);

		mezuSarrera.setText("");
		kargatuMezuak(); // Txata berriro kargatu
	}

	/**
	 * Erreklamazioa administratzaile bati eskalatzeko metodoa. Txata blokeatu
	 * egingo da ondoren.
	 */
	private void eskalatuErreklamazioa() {
		int baieztapena = JOptionPane.showConfirmDialog(this,
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.EskalatuNahi"),
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Eskalatu"),
				JOptionPane.YES_NO_OPTION);

		if (baieztapena == JOptionPane.YES_OPTION) {
			// Datu-basean egoera 1era (Admin) aldatzen dugu
			erreklamazioa = facade.erreklamazioaEgoeraAldatu(erreklamazioa.getKexaNumber(), 1);
			JOptionPane.showMessageDialog(this,
					ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.AdminAbixatua"));
			egiaztatuEgoera(); // Interfazea eguneratu
			
		}
	}

	/**
	 * Saltzaileak erosleari dirua itzultzea onartzeko prozesua gauzatzen du.
	 */
	private void onartuItzulketa() {
		int baieztapena = JOptionPane.showConfirmDialog(this,
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.OnartuNahi"),
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Onartu"),
				JOptionPane.YES_NO_OPTION);

		if (baieztapena == JOptionPane.YES_OPTION) {
			// Datu-basean egoera 2ra (Onartuta) aldatu eta diruaren logika exekutatzen dugu
			erreklamazioa = facade.erreklamazioaEgoeraAldatu(erreklamazioa.getKexaNumber(), 2);
			JOptionPane.showMessageDialog(this,
					ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Onartua"));
			egiaztatuEgoera(); // Interfazea eguneratu
		}
	}

	/**
	 * Erreklamazioa eskalatuta edo itxita badago, botoiak eta txata blokeatzen
	 * ditu.
	 */
	/**
	 * Erreklamazioa eskalatuta edo itxita badago, botoiak eta txata blokeatzen
	 * ditu.
	 */
	private void egiaztatuEgoera() {
		this.erreklamazioa=facade.lortuErreklamazioById(erreklamazioa.getKexaNumber());
		
		if (erreklamazioa.getEgoera() != 0) { // 0 = Negoziatzen

			if (mezuSarrera != null) {
				mezuSarrera.setEnabled(false);
				mezuSarrera.setText(ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.EzinMezua"));
			}
			if (bidaliBotoia != null) {
				bidaliBotoia.setEnabled(false);
			}

			if (eskalatuBotoia != null) {
				eskalatuBotoia.setEnabled(false);
			}
			if (onartuBotoia != null) {
				onartuBotoia.setEnabled(false);
			}
		}
		eguneratuEgoeraEtiketa();
	}

	/**
	 * Erreklamazioaren egoera zenbakizko formatutik testu ulergarrira itzultzen du.
	 * * @param egoera Egoera adierazten duen zenbakia.
	 * 
	 * @return Egoeraren deskribapena euskaraz.
	 */
	private String getEgoeraString(int egoera) {
		switch (egoera) {
		case 0:
			return ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Enegoziatzen");// "Negoziatzen
																										// (Zabalik)";
		case 1:
			return ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Eitxaroten");// "Administratzailearen
																										// zain";
		case 2:
			return ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Eonartua");// "Itzulketa
																									// Onartuta";
		case 3:
			return ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioHariaGUI.Eukatua");// "Itzulketa
																									// Ukatuta";
		default:
			return "Unknown";
		}
	}

	/**
	 * Leihoa itxi eta aurreko leihora (bereziki administratzailearen kasuan)
	 * itzultzen da, taula eguneratuz.
	 */
	private void itxiEtaItzuli() {
		this.setVisible(false);
		if (gurasoa instanceof ErreklamazioakAdminGUI) {
			((ErreklamazioakAdminGUI) gurasoa).kargatuDatuak();
		}
		if (gurasoa != null)
			gurasoa.setVisible(true);
	}
	private void eguneratuEgoeraEtiketa() {
	    egoeraEtiketa.setText(
	        ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status") + ":" + 
	        getEgoeraString(erreklamazioa.getEgoera())
	    );
	    if (erreklamazioa.getEgoera() == 1) {
	        egoeraEtiketa.setForeground(Color.RED);
	    } else {
	        egoeraEtiketa.setForeground(Color.BLACK);
	    }
	}
	
}