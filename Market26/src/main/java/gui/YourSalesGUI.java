package gui;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import configuration.UtilDate;

import extra.BotoiBorobila;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Erabiltzaileak salgai jarri dituen produktuak (bere salmentak) ikusteko eta bilatzeko interfaze grafikoa.
 */
public class YourSalesGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	// --- ATRIBUTUAK ---
	private final JLabel produktuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	// Publikoa da beste klase batetik deitu ahal izateko (adibidez, salmenta ezabatzean taula freskatzeko)
	public BotoiBorobila bilatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 

	private JScrollPane produktuKorritzePanela = new JScrollPane();
	private JTable produktuTaula = new JTable();

	private DefaultTableModel produktuTaulaEredua;

	private JFrame leihoHau; 

	private String[] produktuZutabeIzenak = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"),
	};
	private JTextField bilaketaTestuEremua;
	
	private MainGUI gurasoa;
	private MainGUIerregistratua gurasoa2;
	private JPanel edukiPanela;
	private Registered saltzailea;

	// --- ERAIKITZAILEA ---

	/**
	 * YourSalesGUI klasearen eraikitzailea.
	 * Erabiltzaileak salgai jarritako produktuak taulan kargatzeko elementuak prestatzen ditu.
	 * @param saltzailea Aplikazioa erabiltzen ari den erabiltzaile erregistratua.
	 * @param gurasoa    Aurreko leihoa (nondik gatozen), MainGUI bada.
	 * @param gurasoa2   Aurreko leihoa (nondik gatozen), MainGUIerregistratua bada.
	 */
	public YourSalesGUI(Registered saltzailea, MainGUI gurasoa, MainGUIerregistratua gurasoa2) {
		this.saltzailea=saltzailea;
		this.gurasoa = gurasoa;
		this.gurasoa2 = gurasoa2;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    } else if(gurasoa2 != null) {
	    	this.setLocation(gurasoa2.getLocation());
	    }
		
		produktuTaula.setEnabled(false);
		leihoHau = this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(800, 600));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.FindProducts"));
		produktuEtiketa.setBounds(52, 108, 427, 16);
		this.getContentPane().add(produktuEtiketa);
		
		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(null);

		produktuKorritzePanela.setBounds(new Rectangle(52, 110, 691, 355));
		produktuKorritzePanela.setViewportView(produktuTaula);
		
		produktuTaulaEredua = new DefaultTableModel(null, produktuZutabeIzenak);
		produktuTaula.setModel(produktuTaulaEredua);
		produktuTaula.setRowHeight(35);
		produktuTaula.setFont(new Font("SansSerif", Font.PLAIN, 15));
		
		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
		produktuTaulaEredua.setColumnCount(4); // Objektua gordetzeko 4. zutabea

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

		// Objektua daukan zutabea bistatik ezkutatzen dugu
		produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3)); 

		this.getContentPane().add(produktuKorritzePanela, null);
		
		bilaketaTestuEremua = new JTextField();
		bilaketaTestuEremua.setBounds(78, 53, 478, 31);
		getContentPane().add(bilaketaTestuEremua);
		bilaketaTestuEremua.setColumns(10);
		
		bilatuBotoia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		// --- BILAKETA BOTOIAREN LOGIKA ---
		bilatuBotoia.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent e) {
		 		try {
		 			System.out.println("guian nuecqscwcdcd1wcwd2");
					produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
					produktuTaulaEredua.setColumnCount(4); 
					System.out.println("guian nuecqscwcdcd1wcwd1");
					BLFacade facade = MainGUI.getBusinessLogic();
					System.out.println("guian null dago"+facade);
					Date today = UtilDate.trim(new Date());
					
					// Erabiltzaileak saltzen dituen produktuak eskuratzen ditugu
					List<Sale> salmentak = facade.getSalesBySeller(saltzailea.getEmail(), bilaketaTestuEremua.getText(), today);

					if (salmentak.isEmpty()) {
					    produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
					    produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}
					
					// Taula salmentekin kargatzen dugu
					for (Sale salmenta : salmentak) {
						Vector<Object> errenkada = new Vector<Object>();
						errenkada.add(salmenta.getTitle());
						errenkada.add(salmenta.getPrice());
						errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
						errenkada.add(salmenta); // Objektua bera 3. indizean gehitzen da
						produktuTaulaEredua.addRow(errenkada);		
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		 		
				produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

				produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3)); 
		 	}
		 });
		bilatuBotoia.setBounds(591, 45, 126, 46);
		getContentPane().add(bilatuBotoia);
		
	    // --- TAULAN KLIK EGITEKO LOGIKA ---
		produktuTaula.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            
		            if(mouseEvent.getClickCount() == 2) {
				        JTable taula = (JTable) mouseEvent.getSource();
		            	Point puntua = mouseEvent.getPoint();
				        int errenkada = taula.rowAtPoint(puntua);
				        
				        // Ezkutuko zutabetik objektua eskuratu
		            	Sale aukeratutakoSalmenta = (Sale) produktuTaulaEredua.getValueAt(errenkada, 3);
		            	YourSalesGUI.this.setVisible(false);
		            	bilaketaTestuEremua.setText("");
		            	
		            	// Zure salmentaren ikuspegi zehatza ireki
			            new ShowYourSaleGUI(aukeratutakoSalmenta, saltzailea, YourSalesGUI.this);
		            }
		        }
		 });
		
		// --- ATZERA BOTOIA ---
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				YourSalesGUI.this.setVisible(false);
				bilaketaTestuEremua.setText("");
				
				// Nondik etorri garen arabera (leiho anonimoa ala erregistratua), dagokion gurasoa erakutsi
				if(gurasoa == null) {
				    gurasoa2.setVisible(true);
				} else {
					gurasoa.setVisible(true);
				}
			}
		});
		atzeraBotoia.setBounds(642, 486, 101, 37);
		edukiPanela.add(atzeraBotoia);
	}
	
	public void refreshTable() {
	    BLFacade facade = MainGUI.getBusinessLogic();
	    
	    produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
	    produktuTaulaEredua.setColumnCount(4);

	    Date today = UtilDate.trim(new Date());
	    List<Sale> salmentak = facade.getSalesBySeller(
	        saltzailea.getEmail(), 
	        bilaketaTestuEremua.getText(), 
	        today
	    );

	    if (salmentak.isEmpty()) {
	        produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
	    } else {
	        produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
	    }

	    for (Sale salmenta : salmentak) {
	        Vector<Object> errenkada = new Vector<>();
	        errenkada.add(salmenta.getTitle());
	        errenkada.add(salmenta.getPrice());
	        errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
	        errenkada.add(salmenta);
	        produktuTaulaEredua.addRow(errenkada);
	    }

	    produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
	    produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);
	    produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3));
	}
}