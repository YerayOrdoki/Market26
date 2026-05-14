package gui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import domain.*;
import extra.BotoiBorobila;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;

/**
 * Erabiltzaile berriak sisteman erregistratzeko interfaze grafikoa.
 */
public class RegisterGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// --- ATRIBUTUAK ---
	private JPanel edukiPanela;
	private JTextField postaEremua;
	private JTextField erabiltzaileTestuEremua;
	private JPasswordField pasahitzEremua1;
	private JPasswordField pasahitzEremua2;
	
	// Kokapen eremu berriak
	private JTextField txtKalea;
	private JTextField txtPostaKodea;
	private JTextField txtHerrialdea;
	
	private MainGUI gurasoa;

	// --- ERAIKITZAILEA ---

	/**
	 * RegisterGUI klasearen eraikitzailea.
	 * Erregistro inprimakiaren elementuak eta gertaerak konfiguratzen ditu.
	 * @param gurasoa Aurreko leihoa (Nagusia), atzera egitean berriro erakusteko.
	 */
	public RegisterGUI(MainGUI gurasoa) {
		this.gurasoa = gurasoa;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Title"));

		setBounds(100, 100, 800, 600);
		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(null);
		
		JLabel izenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Title"));
		izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		izenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		izenburuEtiketa.setBounds(149, 41, 456, 37);
		edukiPanela.add(izenburuEtiketa);
		
		JSeparator goikoBereizlea = new JSeparator();
		goikoBereizlea.setBounds(30, 117, 725, 52);
		edukiPanela.add(goikoBereizlea);
		
		// =========================================================
		// EZKERREKO ZUTABEA (Kontuaren datuak)
		// =========================================================
		
		JLabel postaEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Email"));
		postaEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 12));
		postaEtiketa.setBounds(50, 150, 150, 20);
		edukiPanela.add(postaEtiketa);
		
		postaEremua = new JTextField();
		postaEremua.setBounds(200, 150, 150, 25);
		edukiPanela.add(postaEremua);
		
		JLabel erabiltzaileEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.User"));
		erabiltzaileEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 12));
		erabiltzaileEtiketa.setBounds(50, 210, 150, 20);
		edukiPanela.add(erabiltzaileEtiketa);
		
		erabiltzaileTestuEremua = new JTextField();
		erabiltzaileTestuEremua.setBounds(200, 210, 150, 25);
		edukiPanela.add(erabiltzaileTestuEremua);
		
		JLabel pasahitzEtiketa1 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Pass1"));
		pasahitzEtiketa1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pasahitzEtiketa1.setBounds(50, 270, 150, 20);
		edukiPanela.add(pasahitzEtiketa1);
		
		pasahitzEremua1 = new JPasswordField();
		pasahitzEremua1.setBounds(200, 270, 150, 25);
		edukiPanela.add(pasahitzEremua1);
		
		JLabel pasahitzEtiketa2 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Pass2"));
		pasahitzEtiketa2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pasahitzEtiketa2.setBounds(50, 330, 150, 20);
		edukiPanela.add(pasahitzEtiketa2);
		
		pasahitzEremua2 = new JPasswordField();
		pasahitzEremua2.setBounds(200, 330, 150, 25);
		edukiPanela.add(pasahitzEremua2);
		
		// =========================================================
		// ESKUMAKO ZUTABEA (Kokapenaren datuak)
		// =========================================================
		
		JLabel lblKalea = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Kalea"));
		lblKalea.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblKalea.setBounds(420, 150, 100, 20);
		edukiPanela.add(lblKalea);
		
		txtKalea = new JTextField();
		txtKalea.setBounds(530, 150, 150, 25);
		edukiPanela.add(txtKalea);
		
		JLabel lblPostaKodea = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.PostaKodea"));
		lblPostaKodea.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPostaKodea.setBounds(420, 210, 100, 20);
		edukiPanela.add(lblPostaKodea);
		
		txtPostaKodea = new JTextField();
		txtPostaKodea.setBounds(530, 210, 150, 25);
		edukiPanela.add(txtPostaKodea);
		
		JLabel lblHerrialdea = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Herrialdea"));
		lblHerrialdea.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblHerrialdea.setBounds(420, 270, 100, 20);
		edukiPanela.add(lblHerrialdea);
		
		txtHerrialdea = new JTextField();
		txtHerrialdea.setBounds(530, 270, 150, 25);
		edukiPanela.add(txtHerrialdea);
		
		// =========================================================
		// BEHEKO BEREIZLEA ETA BOTOIAK
		// =========================================================
		
		JSeparator behekoBereizlea = new JSeparator();
		behekoBereizlea.setBounds(30, 407, 725, 52);
		edukiPanela.add(behekoBereizlea);
		
		BotoiBorobila erregistratuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Button"));
		
		// --- ERREGISTROAREN LOGIKA ---
		erregistratuBotoia.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		        String posta = postaEremua.getText();
		        String erabiltzailea = erabiltzaileTestuEremua.getText();
		        String pass1 = new String(pasahitzEremua1.getPassword());
		        String pass2 = new String(pasahitzEremua2.getPassword());
		        String kalea = txtKalea.getText();
		        String cp = txtPostaKodea.getText();
		        String herrialdea = txtHerrialdea.getText();

		        // 1. BALIOZTAPENA: Eremu hutsak (LoginGUI.Error2 etiketa berrerabiliz)
		        if (posta.isEmpty() || erabiltzailea.isEmpty() || pass1.isEmpty() || 
		            kalea.isEmpty() || cp.isEmpty() || herrialdea.isEmpty()) {
		            
		            JOptionPane.showMessageDialog(null,
		                    ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Error2"), "ERROR",
		                    JOptionPane.WARNING_MESSAGE);
		            return;
		        }

		        // 2. BALIOZTAPENA: Pasahitzak berdinak diren (RegisterGUI.Error2)
		        if (!pass1.equals(pass2)) {
		            JOptionPane.showMessageDialog(null,
		                    ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Error2"), "ERROR",
		                    JOptionPane.WARNING_MESSAGE);
		            return;
		        }

		        // 3. BALIOZTAPENA ETA ERREGISTROA		        
		        try {
		            // Kokapena balioztatu
		            Gps balioztatzeGps = new Gps(kalea, cp, herrialdea);
		            
		            BLFacade facade = MainGUI.getBusinessLogic();
		            Registered sortutakoErabiltzailea = facade.erabiltzaileaSortu(posta, erabiltzailea, pass1);
		            
		            if (sortutakoErabiltzailea != null) {
		                // Orain DataAccess-ek badaki kokapena null denean berria sortu behar duela
		                facade.erabiltzaileaEguneratu(posta, erabiltzailea, kalea, cp, herrialdea);
		                
		                // Objektua lokalean eguneratu GUI-rako
		                sortutakoErabiltzailea.setKokapena(balioztatzeGps);
		                
		                RegisterGUI.this.setVisible(false);
		                new MainGUIerregistratua(sortutakoErabiltzailea, RegisterGUI.this).setVisible(true);
		            } else {
		                JOptionPane.showMessageDialog(null,
		                        ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Error1"), "ERROR",
		                        JOptionPane.WARNING_MESSAGE);
		            }
		            
		        } catch (Exception ex) {
		            // Kokapena aurkitzen ez bada
		            JOptionPane.showMessageDialog(null, 
		                    ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.KokapenErrorea"), "Error", 
		                    JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});
		erregistratuBotoia.setBounds(166, 441, 262, 50);
		edukiPanela.add(erregistratuBotoia);
		
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.btnAtzera"));
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterGUI.this.setVisible(false);
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(627, 480, 114, 37);
		edukiPanela.add(atzeraBotoia);
	}
}