package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;

import domain.*;
import businessLogic.*;
import extra.*;

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
 * Aplikazioaren leiho nagusia. Erabiltzaileari hizkuntza aukeratzeko,
 * saioa hasteko, erregistratzeko edo salmentak kontsultatzeko aukerak ematen dizkio.
 */
public class MainGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private static BLFacade appFacadeInterface;

    private String saltzaileEmaila;
	private JPanel edukiPanela = null;
	private BotoiBorobila saioaHasiBotoia = null;
	private BotoiBorobila erregistratuBotoia = null;
	private BotoiBorobila kontsultakEginBotoia = null;
	
	protected JLabel aukeratuEtiketa;
	private JRadioButton ingelesaItzulpenaBotoia;
	private JRadioButton euskaraItzulpenaBotoia;
	private JRadioButton gaztelaniaItzulpenaBotoia;
	
	private JPanel panela;
	private final ButtonGroup botoiTaldea = new ButtonGroup();
	private JPanel panela1;
	private JPanel panela2;
	private JPanel panela3;
	
	private JFrame erregistroLeihoa = null;
	private JFrame loginLeihoa = null;
	private JFrame kontsultaLeihoa = null;

    // --- ERAIKITZAILEA ---

	/**
	 * MainGUI klasearen eraikitzailea.
	 * Leiho nagusiaren elementuak eta hizkuntza aukerak konfiguratzen ditu.
	 * @param saltzaile Saioa hasi duen erabiltzailea (edo null erabiltzaile anonimoa bada).
	 */
	public MainGUI(Registered saltzaile) {
		super();
		
		try {
		    this.saltzaileEmaila = saltzaile.getEmail();
		} catch (java.lang.NullPointerException e) {
			this.saltzaileEmaila = ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Unregistered");
			System.out.println("Ez duzu kontua hasi");
		}
		
		this.setSize(800, 600);
		this.setLocationRelativeTo(null); // ALDAKETA: Leihoa pantailaren erdian agertzeko
		
		aukeratuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		aukeratuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		aukeratuEtiketa.setForeground(Color.BLACK);
		aukeratuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		
		ingelesaItzulpenaBotoia = new JRadioButton("English");
		ingelesaItzulpenaBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ingelesaItzulpenaBotoia.setBounds(608, 35, 101, 21);
		ingelesaItzulpenaBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();				
            }
		});
		botoiTaldea.add(ingelesaItzulpenaBotoia);
		
		euskaraItzulpenaBotoia = new JRadioButton("Euskara");
		euskaraItzulpenaBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		euskaraItzulpenaBotoia.setBounds(121, 35, 108, 21);
		euskaraItzulpenaBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();				
            }
		});
		botoiTaldea.add(euskaraItzulpenaBotoia);
		
		gaztelaniaItzulpenaBotoia = new JRadioButton("Castellano");
		gaztelaniaItzulpenaBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		gaztelaniaItzulpenaBotoia.setBounds(352, 35, 134, 21);
		gaztelaniaItzulpenaBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				paintAgain();
			}
		});
		botoiTaldea.add(gaztelaniaItzulpenaBotoia);
	
		panela = new JPanel();
		panela.setLayout(null);
		panela.add(euskaraItzulpenaBotoia);
		panela.add(gaztelaniaItzulpenaBotoia);
		panela.add(ingelesaItzulpenaBotoia);
		
		// --- ERREGISTRATZEKO BOTOIAREN LOGIKA ---
		erregistratuBotoia = new BotoiBorobila();
		erregistratuBotoia.setBounds(146, 11, 494, 91);
		erregistratuBotoia.setFont(new Font("Tahoma", Font.PLAIN, 15));
		erregistratuBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Register"));
		erregistratuBotoia.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				MainGUI.this.setVisible(false);
				
					erregistroLeihoa = new RegisterGUI(MainGUI.this);
					erregistroLeihoa.setLocation(MainGUI.this.getLocation());
					erregistroLeihoa.setVisible(true);
				
			}
		});
		
		/* * Jatorrizko kodean iruzkinduta zegoen zatia mantendu da.
		kontsultakEginBotoia = new BotoiBorobila();
		kontsultakEginBotoia.setBounds(234, 11, 325, 91);
		kontsultakEginBotoia.setFont(new Font("Tahoma", Font.PLAIN, 15));
		kontsultakEginBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		kontsultakEginBotoia.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI(saltzaile, MainGUI.this,null);
				a.setLocation(MainGUI.this.getLocation());
				a.setVisible(true);
				MainGUI.this.setVisible(false);
			}
		});
		*/

		edukiPanela = new JPanel();
		edukiPanela.setLayout(new GridLayout(5, 1, 0, 0));
		edukiPanela.add(aukeratuEtiketa);
		//edukiPanela.add(kontsultaSortuBotoia);
		//edukiPanela.add(erregistratuBotoia);
		
		// --- SAIOA HASTEKO BOTOIAREN LOGIKA ---
		saioaHasiBotoia = new BotoiBorobila();
		saioaHasiBotoia.setBounds(148, 11, 493, 91);
		saioaHasiBotoia.setFont(new Font("Tahoma", Font.PLAIN, 15));
		saioaHasiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Login"));
		saioaHasiBotoia.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				MainGUI.this.setVisible(false);
				if(loginLeihoa == null) {
					loginLeihoa = new LoginGUI(MainGUI.this);
					loginLeihoa.setLocation(MainGUI.this.getLocation());
					loginLeihoa.setVisible(true);
				} else {
					loginLeihoa.setLocation(MainGUI.this.getLocation());
					loginLeihoa.setVisible(true);
				}
			}
		});
		
		// --- KONTSULTAK EGITEKO BOTOIAREN LOGIKA ---
		kontsultakEginBotoia = new BotoiBorobila();
		kontsultakEginBotoia.setBounds(151, 11, 490, 91);		
		kontsultakEginBotoia.setFont(new Font("Tahoma", Font.PLAIN, 15));
		kontsultakEginBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		kontsultakEginBotoia.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				MainGUI.this.setVisible(false);

					kontsultaLeihoa = new QuerySalesGUI(saltzaile, MainGUI.this, null);
					kontsultaLeihoa.setLocation(MainGUI.this.getLocation());
					kontsultaLeihoa.setVisible(true);

				
			}
		});
		
		panela3 = new JPanel();
		panela3.setLayout(null);
		panela3.add(erregistratuBotoia);
		
		panela2 = new JPanel();
		panela2.setLayout(null);
		panela2.add(saioaHasiBotoia);
		
		panela1 = new JPanel();
		panela1.setLayout(null);
		panela1.add(kontsultakEginBotoia);
		
		edukiPanela.add(panela3);
		edukiPanela.add(panela2);
		edukiPanela.add(panela1);
		edukiPanela.add(panela);

		setContentPane(edukiPanela);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + saltzaileEmaila);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
	
    // --- METODOAK ---

	/**
	 * Aplikazioaren negozio-logikaren interfazea itzultzen du.
	 * (Beste klase guztiek erabiltzen dute metodo hau, beraz izena bere horretan mantendu da).
	 * @return BLFacade negozio-logika.
	 */
	public static BLFacade getBusinessLogic() {
		return appFacadeInterface;
	}
	 
	/**
	 * Aplikazioaren negozio-logikaren interfazea ezartzen du.
	 * @param facade Ezartzeko negozio-logika.
	 */
	public static void setBussinessLogic(BLFacade facade) {
		System.out.println("null dago?"+facade);
		appFacadeInterface = facade;
	}

	/**
	 * Hizkuntza aldatzen denean (Euskara, Gaztelania, Ingelesa), leiho honetako
	 * osagaien testuak hizkuntza berrira eguneratzen ditu.
	 */
	private void paintAgain() {
		aukeratuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		kontsultakEginBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		//kontsultaSortuBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		saioaHasiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Login"));
		erregistratuBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Register"));

		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Unregistered"));
	}
}