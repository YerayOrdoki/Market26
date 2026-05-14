package gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import extra.BotoiBorobila;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JSeparator;
import java.awt.Color;

/**
 * Erabiltzaileek sisteman saioa hasteko (Login) interfaze grafikoa.
 */
public class LoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// --- ATRIBUTUAK ---
	private JPanel edukiPanela;
	private JTextField logTestuEremua;
	private JPasswordField pasahitzEremua;
	private MainGUI gurasoa;

	// --- ERAIKITZAILEA ---

	/**
	 * LoginGUI klasearen eraikitzailea.
	 * Saioa hasteko elementu grafikoak konfiguratzen ditu eta logikaren entzuleak prestatzen ditu.
	 * @param gurasoa Aurreko leihoa (Nagusia), atzera egitean berriro erakusteko.
	 */
	public LoginGUI(MainGUI gurasoa) {
		this.gurasoa = gurasoa;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Title"));
		setBounds(100, 100, 800, 600);
		
		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(null);
		
		JLabel erabiltzaileEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.User"));
		erabiltzaileEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 14));
		erabiltzaileEtiketa.setBounds(178, 177, 101, 40);
		edukiPanela.add(erabiltzaileEtiketa);
		
		JLabel pasahitzEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Pass"));
		pasahitzEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 14));
		pasahitzEtiketa.setBounds(178, 302, 101, 35);
		edukiPanela.add(pasahitzEtiketa);
		
		logTestuEremua = new JTextField();
		logTestuEremua.setBounds(361, 178, 101, 40);
		edukiPanela.add(logTestuEremua);
		logTestuEremua.setColumns(10);
		
		BotoiBorobila saiatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Button"));
		saiatuBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		// --- SAIOA HASTEKO LOGIKA ---
		saiatuBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
				
				// Eremuak hutsik ez daudela ziurtatzen dugu
				if(!(logTestuEremua.getText().length() == 0 || pasahitzEremua.getPassword().length == 0)) {
					
					// Erabiltzailearen datuak zuzenak diren egiaztatzen dugu datu-basean
					User erabiltzailea = facade.isLogged(logTestuEremua.getText(), new String(pasahitzEremua.getPassword()));
					
					if(erabiltzailea != null) {
					    boolean banned = (erabiltzailea instanceof Registered) && ((Registered) erabiltzailea).isBanned();
					    
					    if(!banned) {
					        pasahitzEremua.setText("");
					        logTestuEremua.setText("");
					        LoginGUI.this.setVisible(false);

					        if(erabiltzailea instanceof Admin) {
					            new MainGUIAdmin((Admin) erabiltzailea, LoginGUI.this).setVisible(true);
					        } else if (erabiltzailea instanceof Registered) {
					            new MainGUIerregistratua((Registered) erabiltzailea, LoginGUI.this).setVisible(true);
					        }
					    } else {
					        JOptionPane.showMessageDialog(null,
					            ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Error3"),
					            "ERROR",
					            JOptionPane.WARNING_MESSAGE);
					    }
					} else {
						JOptionPane.showMessageDialog(null,
								ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Error"),
					            "ERROR",
					            JOptionPane.WARNING_MESSAGE);
					}
				
			} else {
				JOptionPane.showMessageDialog(null,
						ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Error2"),
			            "ERROR",
			            JOptionPane.WARNING_MESSAGE);
			}
			}
		});
		saiatuBotoia.setBounds(158, 448, 346, 40);
		edukiPanela.add(saiatuBotoia);
		
		pasahitzEremua = new JPasswordField();
		pasahitzEremua.setBounds(361, 301, 101, 37);
		edukiPanela.add(pasahitzEremua);
		
		JLabel izenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Title")); 
		izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
		izenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
		izenburuEtiketa.setBounds(96, 37, 581, 50);
		edukiPanela.add(izenburuEtiketa);
		
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera")); 
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginGUI.this.setVisible(false);
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(650, 486, 101, 35);
		edukiPanela.add(atzeraBotoia);
		
		JSeparator goikoBereizlea = new JSeparator();
		goikoBereizlea.setForeground(Color.BLACK);
		goikoBereizlea.setBackground(new Color(0, 0, 0));
		goikoBereizlea.setBounds(10, 97, 766, 35);
		edukiPanela.add(goikoBereizlea);
		
		JSeparator behekoBereizlea = new JSeparator();
		behekoBereizlea.setForeground(Color.BLACK);
		behekoBereizlea.setBackground(Color.BLACK);
		behekoBereizlea.setBounds(10, 403, 766, 35);
		edukiPanela.add(behekoBereizlea);
	}

	// --- METODOAK ---

	/*
	 * Jatorrizko kodean iruzkinduta zegoen metodoa.
	 * Logika ez ukitzeko aginduari jarraituz, bere horretan mantentzen da.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginGUI frame = new LoginGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
}