package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
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
 * Administratzailearen leiho nagusia. Hemen sistemako kexak, produktuak, erreklamazioak, 
 * erabiltzaileak kudeatzeko eta mezuak bidaltzeko botoiak aurkitzen dira.
 */
public class MainGUIAdmin extends JFrame {
	
	private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private static BLFacade appFacadeInterface;
    
    private String adminPosta;
	private JPanel edukiPanela = null;
	
	// Botoi batzuk jatorrizko kodean zeuden arren ez dira zuzenean eraikitzailean agertzen. Egitura mantendu egin da.
	private BotoiBorobila kexakBotoia;
	private BotoiBorobila saioaItxiBotoia;
	private BotoiBorobila produktuakBotoia;
	private BotoiBorobila erreklamazioakBotoia;
	private BotoiBorobila erabiltzaileakBotoia;
	private BotoiBorobila mezuakBotoia;

	private JRadioButton ingelesaItzulpenaBotoia;
	private JRadioButton euskaraItzulpenaBotoia;
	private JRadioButton gaztelaniaItzulpenaBotoia;
	
	private JPanel panela;
	private final ButtonGroup botoiTaldea = new ButtonGroup();
	private JPanel panela4;
	private JPanel panela3;
	private JPanel panela2;
	private JLabel profilIzenburuEtiketa;
	
	private JFrame leihoNagusia = null;
	private JFrame erreklamazioAdminLeihoa = null; // Aurretik ErositakoakGUI zena
	private JFrame saldutakoakLeihoa = null;

    // --- ERAIKITZAILEA ---

	/**
	 * MainGUIAdmin klasearen eraikitzailea.
	 * Administratzaileari zuzendutako panelak eta aukerak sortzen ditu.
	 * @param administratzailea Saioa hasi duen administratzailea.
	 * @param gurasoa           Aurreko leihoa (nondik gatozen).
	 */
	public MainGUIAdmin(Admin administratzailea, JFrame gurasoa) {
		super();

		this.adminPosta = administratzailea.getEmail();
		
        System.out.println("Admin kontu batera sartu zera");
			
		this.setSize(800, 600);
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		ingelesaItzulpenaBotoia = new JRadioButton("English");
		ingelesaItzulpenaBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ingelesaItzulpenaBotoia.setBounds(601, 55, 146, 23);
		ingelesaItzulpenaBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();				
            }
		});
		botoiTaldea.add(ingelesaItzulpenaBotoia);
		
		euskaraItzulpenaBotoia = new JRadioButton("Euskara");
		euskaraItzulpenaBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		euskaraItzulpenaBotoia.setBounds(197, 55, 159, 23);
		euskaraItzulpenaBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();				
            }
		});
		botoiTaldea.add(euskaraItzulpenaBotoia);
		
		gaztelaniaItzulpenaBotoia = new JRadioButton("Castellano");
		gaztelaniaItzulpenaBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		gaztelaniaItzulpenaBotoia.setBounds(379, 55, 191, 23);
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
		
		edukiPanela = new JPanel();
		edukiPanela.setLayout(new GridLayout(4, 1, 0, 0));
		
		panela4 = new JPanel();
		panela4.setEnabled(true);
		edukiPanela.add(panela4);
		panela4.setLayout(null);
		
		saioaItxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Logout")); 
		saioaItxiBotoia.setFont(new Font("Tahoma", Font.PLAIN, 12));
		saioaItxiBotoia.setColourRED();
		saioaItxiBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				MainGUIAdmin.this.setVisible(false);
				if(leihoNagusia == null) {
					leihoNagusia = new MainGUI(null); // Leiho nagusi hutsera bueltatu saioa itxita
					leihoNagusia.setVisible(true);
				} else {
					leihoNagusia.setVisible(true);
				}
			}
		});
		saioaItxiBotoia.setBounds(611, 18, 165, 36);
		panela4.add(saioaItxiBotoia);
		
		panela3 = new JPanel();
		panela3.setLayout(null);
		
		panela2 = new JPanel();
		panela2.setLayout(null);
		edukiPanela.add(panela4);
		
		profilIzenburuEtiketa = new JLabel("ADMIN");
		profilIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		profilIzenburuEtiketa.setForeground(Color.BLACK);
		profilIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		profilIzenburuEtiketa.setBounds(109, 54, 546, 55);
		panela4.add(profilIzenburuEtiketa);
		edukiPanela.add(panela3);
		
		kexakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.Salaketak")); 
		//"SALAKETAK KONTSULTATU"
		kexakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUIAdmin.this.setVisible(false);
				new SalaketakAdminGUI(administratzailea, MainGUIAdmin.this).setVisible(true);
				
			}
		});
		kexakBotoia.setBounds(425, 21, 209, 91);
		panela3.add(kexakBotoia);
		
		erreklamazioakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.Erreklamazioak"));
		erreklamazioakBotoia.setBounds(149, 21, 209, 91);
		panela3.add(erreklamazioakBotoia);
		erreklamazioakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUIAdmin.this.setVisible(false);
                erreklamazioAdminLeihoa = new ErreklamazioakAdminGUI(administratzailea, MainGUIAdmin.this);  
                erreklamazioAdminLeihoa.setVisible(true);
			}
		});
		edukiPanela.add(panela2);
		
		erabiltzaileakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.ErabiltzaileakKontsultatu"));
		erabiltzaileakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainGUIAdmin.this.setVisible(false);
				new QueryUsersAdminGUI(administratzailea,MainGUIAdmin.this).setVisible(true);
			}
		});
		erabiltzaileakBotoia.setBounds(147, 24, 209, 91);
		panela2.add(erabiltzaileakBotoia);
		
		mezuakBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.MezuakBidali"));
		mezuakBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUIAdmin.this.setVisible(false);
				new NotifikazioaBidaliGUI(administratzailea, MainGUIAdmin.this).setVisible(true);
			}
		});
		mezuakBotoia.setBounds(424, 24, 209, 91);
		panela2.add(mezuakBotoia);
		edukiPanela.add(panela);
		
        setContentPane(edukiPanela);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + adminPosta);
		
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
		appFacadeInterface = facade;
	}

	/**
	 * Hizkuntza aldatzen denean testuak berriro kargatzeko metodoa.
	 */
	/**
	 * Hizkuntza aldatzen denean testuak berriro kargatzeko metodoa.
	 */
	private void paintAgain() {
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + adminPosta);
		saioaItxiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIerregistratua.Logout"));
		kexakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.Salaketak"));
		//produktuakBotoia.setText(bundle.getString("MainGUIAdmin.ProduktuakKontsultatu"));
		erreklamazioakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.Erreklamazioak"));
		erabiltzaileakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.ErabiltzaileakKontsultatu"));
		mezuakBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.MezuakBidali"));
	}
}