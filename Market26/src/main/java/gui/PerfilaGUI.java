package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import domain.*;
import extra.BotoiBorobila;

/**
 * Erabiltzailearen profilaren leiho nagusia.
 * Hemendik buzoia, historiala eta diru-kudeaketa atzitu daitezke.
 */
public class PerfilaGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// --- ATRIBUTUAK ---

	private static BLFacade appFacadeInterface;
	private BLFacade facade;

	private String saltzaileEmaila;
	private JPanel edukiPanela = null;

	private BotoiBorobila erositakoakBotoia;
	private BotoiBorobila saldutakoakBotoia;
	private BotoiBorobila saioaItxiBotoia;
	private BotoiBorobila diruaSartuAteraBotoia;
	private BotoiBorobila atzeraBotoia;
	private BotoiBorobila mugimenduakBotoia;
	private BotoiBorobila buzoiaBotoia;

	// Ezarpenen panela eta osagaiak
	private BotoiBorobila ezarpenakBotoia;
	private BotoiBorobila itxiEzarpenakBotoia;
	private JPanel ezarpenakPanela;
	private JTextField txtIzena;

	// Kokapenaren datuak
	private JTextField txtKalea;
	private JTextField txtPostaKodea;
	private JTextField txtHerrialdea;

	private JLabel lblIzenaEzarpenak;
	private JLabel lblKalea;
	private JLabel lblPostaKodea;
	private JLabel lblHerrialdea;

	private JButton gordeBotoia;

	private JRadioButton ingelesItzulpenBotoia;
	private JRadioButton euskaraItzulpenBotoia;
	private JRadioButton gaztelaniaItzulpenBotoia;

	private JPanel panela;
	private final ButtonGroup botoiTaldea = new ButtonGroup();
	private JPanel panela4;
	private JPanel panela3;
	private JPanel panela2;

	private JLabel profilIzenburuEtiketa;

	private JFrame leihoNagusia = null;
	private JFrame erositakoakLeihoa = null;
	private JFrame saldutakoakLeihoa = null;
	private JFrame mugimenduakLeihoa = null;

	JLabel lblSaldoa;
	JLabel alertaGorriEtiketa = new JLabel("!!");
	private JLabel historialaIzenburuEtiketa;
	private JLabel diruKudeaketaIzenburuEtiketa;
	private BotoiBorobila btnHarpidetu;
	boolean harpidetuta = false;

	// --- ERAIKITZAILEA ---

	/**
	 * Perfilaren leihoa sortzen du eta bere osagai guztiak hasieratzen ditu.
	 *
	 * @param erabiltzailea Une honetan saioa hasita duen erabiltzailea.
	 * @param gurasoa Aurreko leihoa.
	 */
	public PerfilaGUI(Registered erabiltzailea, JFrame gurasoa) {
		super();

		this.saltzaileEmaila = erabiltzailea.getEmail();
		this.facade = gui.MainGUI.getBusinessLogic();
		this.setSize(800, 600);

		if (gurasoa != null) {
			this.setLocation(gurasoa.getLocation());
		}

		// --- HIZKUNTZA BOTOIAK ---

		ingelesItzulpenBotoia = new JRadioButton("English");
		ingelesItzulpenBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ingelesItzulpenBotoia.setBounds(602, 68, 146, 23);
		ingelesItzulpenBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();
			}
		});
		botoiTaldea.add(ingelesItzulpenBotoia);

		euskaraItzulpenBotoia = new JRadioButton("Euskara");
		euskaraItzulpenBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		euskaraItzulpenBotoia.setBounds(198, 68, 159, 23);
		euskaraItzulpenBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();
			}
		});
		botoiTaldea.add(euskaraItzulpenBotoia);

		gaztelaniaItzulpenBotoia = new JRadioButton("Castellano");
		gaztelaniaItzulpenBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		gaztelaniaItzulpenBotoia.setBounds(380, 68, 191, 23);
		gaztelaniaItzulpenBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				paintAgain();
			}
		});
		botoiTaldea.add(gaztelaniaItzulpenBotoia);

		panela = new JPanel();
		panela.setLayout(null);
		panela.add(euskaraItzulpenBotoia);
		panela.add(gaztelaniaItzulpenBotoia);
		panela.add(ingelesItzulpenBotoia);

		edukiPanela = new JPanel();
		edukiPanela.setLayout(new GridLayout(4, 1, 0, 0));

		panela4 = new JPanel();
		panela4.setLayout(null);

		// --- EZARPENAK BOTOIA ---

		ezarpenakBotoia = new BotoiBorobila("\\u2699");
		ezarpenakBotoia.setFont(new Font("Tahoma", Font.PLAIN, 20));
		ezarpenakBotoia.setBounds(10, 10, 50, 40);
		ezarpenakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ezarpenakPanela.setVisible(!ezarpenakPanela.isVisible());
			}
		});
		panela4.add(ezarpenakBotoia);

		// --- SAIOA IXTEKO BOTOIA ---

		saioaItxiBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Logout"));
		saioaItxiBotoia.setFont(new Font("Tahoma", Font.BOLD, 12));
		saioaItxiBotoia.setColourRED();
		saioaItxiBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				PerfilaGUI.this.setVisible(false);
				if (leihoNagusia == null) {
					leihoNagusia = new MainGUI(null);
					leihoNagusia.setVisible(true);
				} else {
					leihoNagusia.setVisible(true);
				}
			}
		});
		saioaItxiBotoia.setBounds(611, 10, 165, 40);
		panela4.add(saioaItxiBotoia);

		// --- BUZOIKO ALERTA ---

		alertaGorriEtiketa.setBounds(747, 63, 30, 38);
		alertaGorriEtiketa.setForeground(Color.RED);
		panela4.add(alertaGorriEtiketa);

		boolean berririkDaude = facade.badituIrakurriGabekoak(saltzaileEmaila);
		alertaGorriEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 18));
		alertaGorriEtiketa.setVisible(berririkDaude);

		// --- BUZOIA BOTOIA ---

		buzoiaBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Buzoia"));
		buzoiaBotoia.setBounds(611, 61, 166, 40);
		panela4.add(buzoiaBotoia);
		buzoiaBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PerfilaGUI.this.setVisible(false);
				new BuzoiaGUI(erabiltzailea, PerfilaGUI.this).setVisible(true);
			}
		});
		edukiPanela.add(panela4);

		// --- HARPIDETZA BOTOIA ---

		if (facade.vipDa(erabiltzailea.getEmail())) {
			harpidetuta = true;
		}

		btnHarpidetu = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Harpidetu"));
		btnHarpidetu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!harpidetuta) {
					UIManager.put("OptionPane.yesButtonText",
							ResourceBundle.getBundle("Etiquetas").getString("OptionPane.Yes"));
					UIManager.put("OptionPane.noButtonText",
							ResourceBundle.getBundle("Etiquetas").getString("OptionPane.No"));

					int opzioa1 = JOptionPane.showConfirmDialog(
							PerfilaGUI.this,
							ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Harpidetu?"),
							"",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					if (opzioa1 == JOptionPane.YES_OPTION) {
						if (facade.diruaAtera(10.00, erabiltzailea.getEmail())) {
							facade.harpidetu(erabiltzailea.getEmail());
							harpidetuta = true;
							btnHarpidetu.setText(
									ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Harpidetu2"));
							btnHarpidetu.setEnabled(false);
						} else {
							JOptionPane.showMessageDialog(
									null,
									ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.NotEnoughMoney"),
									"ERROR",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
				paintAgain();
			}
		});

		if (harpidetuta) {
			btnHarpidetu.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Harpidetu2"));
		}

		btnHarpidetu.setLocation(70, 10);
		btnHarpidetu.setEnabled(!facade.vipDa(erabiltzailea.getEmail()));
		Dimension hobetsitakoTamaina = btnHarpidetu.getPreferredSize();
		btnHarpidetu.setSize(166, 40);
		btnHarpidetu.setColourGREEN();
		panela4.add(btnHarpidetu);

		// --- PROFILAREN IZENBURUA ---

		profilIzenburuEtiketa = new JLabel(erabiltzailea.getName());
		profilIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		profilIzenburuEtiketa.setForeground(Color.BLACK);
		profilIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		profilIzenburuEtiketa.setBounds(109, 54, 546, 55);
		panela4.add(profilIzenburuEtiketa);

		JSeparator separator = new JSeparator();
		separator.setBackground(new Color(0, 0, 0));
		separator.setBounds(10, 118, 766, 11);
		panela4.add(separator);

		// Panel honek saldoaren atzeko nabarmendura biribildua margotzen du.
		JPanel saldoPanela = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.dispose();
			}
		};
		saldoPanela.setBackground(new Color(255, 255, 128));
		saldoPanela.setBounds(10, 61, 165, 40);
		panela4.add(saldoPanela);
		saldoPanela.setLayout(null);

		lblSaldoa = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldo") + " : "
						+ String.format("%.2f", facade.getSellerByEmail(saltzaileEmaila).getSaldoa()) + "€");
		lblSaldoa.setBounds(10, 11, 299, 14);
		saldoPanela.add(lblSaldoa);

		// --- HISTORIALAREN PANELA ---

		panela3 = new JPanel();
		panela3.setLayout(null);

		historialaIzenburuEtiketa = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Historialak"));
		historialaIzenburuEtiketa.setBounds(113, -13, 546, 55);
		panela3.add(historialaIzenburuEtiketa);
		historialaIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		historialaIzenburuEtiketa.setForeground(Color.BLACK);
		historialaIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));

		erositakoakBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Erositakoak"));
		erositakoakBotoia.setBounds(147, 39, 209, 91);
		panela3.add(erositakoakBotoia);
		erositakoakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PerfilaGUI.this.setVisible(false);
				if (erositakoakLeihoa == null) {
					erositakoakLeihoa = new ErositakoakGUI(erabiltzailea, PerfilaGUI.this);
					erositakoakLeihoa.setVisible(true);
				} else {
					erositakoakLeihoa.setVisible(true);
				}
			}
		});

		saldutakoakBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldutakoak"));
		saldutakoakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saldutakoakLeihoa = new SaldutakoakGUI(erabiltzailea, PerfilaGUI.this);
				saldutakoakLeihoa.setVisible(true);
			}
		});
		saldutakoakBotoia.setBounds(424, 39, 209, 91);
		panela3.add(saldutakoakBotoia);

		edukiPanela.add(panela3);

		// --- DIRU-KUDEAKETAREN PANELA ---

		panela2 = new JPanel();
		panela2.setLayout(null);

		diruKudeaketaIzenburuEtiketa = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.DiruKudeaketa"));
		diruKudeaketaIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		diruKudeaketaIzenburuEtiketa.setForeground(Color.BLACK);
		diruKudeaketaIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		diruKudeaketaIzenburuEtiketa.setBounds(118, -10, 546, 55);
		panela2.add(diruKudeaketaIzenburuEtiketa);

		diruaSartuAteraBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.DiruaSartuAtera"));
		diruaSartuAteraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PerfilaGUI.this.setVisible(false);
				new DiruaKudeatuGUI(erabiltzailea, PerfilaGUI.this).setVisible(true);
			}
		});
		diruaSartuAteraBotoia.setBounds(147, 39, 209, 91);
		panela2.add(diruaSartuAteraBotoia);

		mugimenduakBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Mugimenduak"));
		mugimenduakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mugimenduakLeihoa = new MugimenduakGUI(erabiltzailea, PerfilaGUI.this);
				mugimenduakLeihoa.setVisible(true);
			}
		});
		mugimenduakBotoia.setBounds(424, 39, 209, 91);
		panela2.add(mugimenduakBotoia);

		edukiPanela.add(panela2);

		// --- ATZERA BOTOIAREN PANELA ---

		atzeraBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera"));
		atzeraBotoia.setBounds(10, 92, 101, 37);
		panela.add(atzeraBotoia);

		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PerfilaGUI.this.setVisible(false);
				if (gurasoa instanceof MainGUIerregistratua) {
					MainGUIerregistratua lagun = (MainGUIerregistratua) gurasoa;
					Registered erabiltzaileEguneratua = facade.getSellerByEmail(saltzaileEmaila);
					lagun.btnErreserbatuak.setVisible(erabiltzaileEguneratua.getVip() != null);
				}
				((MainGUIerregistratua) gurasoa).eguneratuBuzoia();
				gurasoa.setVisible(true);
			}
		});
		edukiPanela.add(panela);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBackground(new Color(0, 0, 0));
		separator_1.setBounds(10, 11, 766, 11);
		panela.add(separator_1);

		// --- EZARPENAK PANELA ---

		ezarpenakPanela = new JPanel();
		ezarpenakPanela.setBackground(new Color(240, 240, 240));
		ezarpenakPanela.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		ezarpenakPanela.setLayout(null);
		ezarpenakPanela.setBounds(0, 0, 250, 600);
		ezarpenakPanela.setVisible(false);

		itxiEzarpenakBotoia = new BotoiBorobila("\\u2190");
		itxiEzarpenakBotoia.setFont(new Font("Tahoma", Font.BOLD, 16));
		itxiEzarpenakBotoia.setBounds(10, 10, 45, 30);
		itxiEzarpenakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ezarpenakPanela.setVisible(false);
			}
		});
		ezarpenakPanela.add(itxiEzarpenakBotoia);

		// --- EZARPENETAKO EREMUAK ---

		lblIzenaEzarpenak = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Izena") + ":");
		lblIzenaEzarpenak.setBounds(20, 50, 210, 20);
		ezarpenakPanela.add(lblIzenaEzarpenak);

		txtIzena = new JTextField(erabiltzailea.getName());
		txtIzena.setBounds(20, 70, 210, 25);
		ezarpenakPanela.add(txtIzena);

		lblKalea = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Kalea") + ":");
		lblKalea.setBounds(20, 110, 210, 20);
		ezarpenakPanela.add(lblKalea);

		txtKalea = new JTextField("");
		txtKalea.setBounds(20, 130, 210, 25);
		ezarpenakPanela.add(txtKalea);

		lblPostaKodea = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.PostaKodea") + ":");
		lblPostaKodea.setBounds(20, 170, 210, 20);
		ezarpenakPanela.add(lblPostaKodea);

		txtPostaKodea = new JTextField("");
		txtPostaKodea.setBounds(20, 190, 210, 25);
		ezarpenakPanela.add(txtPostaKodea);

		lblHerrialdea = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Herrialdea") + ":");
		lblHerrialdea.setBounds(20, 230, 210, 20);
		ezarpenakPanela.add(lblHerrialdea);

		txtHerrialdea = new JTextField("");
		txtHerrialdea.setBounds(20, 250, 210, 25);
		ezarpenakPanela.add(txtHerrialdea);

		gordeBotoia = new JButton(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Gorde"));
		gordeBotoia.setBounds(70, 310, 110, 30);
		gordeBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (saltzaileEmaila.equals("") || txtIzena.getText().equals("") || txtKalea.getText().equals("")
						|| txtPostaKodea.getText().equals("") || txtHerrialdea.getText().equals("")) {

					JOptionPane.showMessageDialog(
							null,
							ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Error2"),
							"ERROR",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				try {
					boolean ondo = facade.erabiltzaileaEguneratu(
							saltzaileEmaila,
							txtIzena.getText(),
							txtKalea.getText(),
							txtPostaKodea.getText(),
							txtHerrialdea.getText());

					if (ondo) {
						erabiltzailea.setName(txtIzena.getText());
						profilIzenburuEtiketa.setText(txtIzena.getText());
						JOptionPane.showMessageDialog(
								null,
								ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.GordeOndo"));
						ezarpenakPanela.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(
								null,
								ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.KokapenErrorea"));
					}
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(
							null,
							ResourceBundle.getBundle("Etiquetas").getString(ex.getMessage()),
							"Error de Ubicación",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		ezarpenakPanela.add(gordeBotoia);

		// JLayeredPane erabiltzen da ezarpen-panela gainetik erakusteko, oinarrizko diseinua aldatu gabe.
		getLayeredPane().add(edukiPanela, JLayeredPane.DEFAULT_LAYER);
		getLayeredPane().add(ezarpenakPanela, JLayeredPane.PALETTE_LAYER);
		edukiPanela.setBounds(0, 0, 786, 563);

		setTitle(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.MainTitle"));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}

	// --- METODO PUBLIKOAK ---

	/**
	 * Buzoian irakurri gabeko mezurik dagoen egiaztatzen du eta alerta eguneratzen du.
	 */
	public void eguneratuBuzoia() {
		boolean berririkDaude = facade.badituIrakurriGabekoak(saltzaileEmaila);
		alertaGorriEtiketa.setVisible(berririkDaude);
	}

	// --- METODO PRIBATUAK ---

	/**
	 * Leihoaren testu guztiak berriz margotzen ditu unean uneko hizkuntzaren arabera.
	 */
	private void paintAgain() {
		ResourceBundle.clearCache();

		historialaIzenburuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Historialak"));
		erositakoakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Erositakoak"));
		saldutakoakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldutakoak"));
		diruKudeaketaIzenburuEtiketa
				.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.DiruKudeaketa"));
		mugimenduakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Mugimenduak"));
		diruaSartuAteraBotoia
				.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.DiruaSartuAtera"));
		atzeraBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera"));
		saioaItxiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Logout"));
		lblSaldoa.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldo") + " : "
				+ facade.getSellerByEmail(saltzaileEmaila).getSaldoa() + "€");
		buzoiaBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Buzoia"));

		if (harpidetuta) {
			btnHarpidetu.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Harpidetu2"));
		} else {
			btnHarpidetu.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Harpidetu"));
		}

		lblIzenaEzarpenak.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Izena") + ":");
		lblKalea.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Kalea") + ":");
		lblPostaKodea.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.PostaKodea") + ":");
		lblHerrialdea.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Herrialdea") + ":");
		gordeBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Gorde"));

		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.MainTitle"));
	}
}