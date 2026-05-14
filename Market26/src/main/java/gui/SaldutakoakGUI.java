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
 * Erabiltzaileak saldutako produktuak bilatu eta ikusteko interfaze grafikoa.
 */
public class SaldutakoakGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	// --- ATRIBUTUAK ---
	private final JLabel produktuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	private BotoiBorobila bilatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 
	private BotoiBorobila itxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Close"));

	private JScrollPane produktuKorritzePanela = new JScrollPane();
	private JTable produktuTaula = new JTable();

	private DefaultTableModel produktuTaulaEredua;

	private JFrame leihoHau; 

	private String[] produktuZutabeIzenak = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("SaldutakoakGUI.SoldDate"),
	};
	private JTextField bilaketaTestuEremua;
	
	private PerfilaGUI gurasoa;
	private JPanel edukiPanela;
	
	// --- ERAIKITZAILEA ---

	/**
	 * SaldutakoakGUI klasearen eraikitzailea.
	 * Leihoaren elementuak konfiguratzen ditu eta erabiltzaileak saldutako produktuak taulan kargatzeko prestatzen du.
	 * @param saltzailea Aplikazioa erabiltzen ari den erabiltzaile erregistratua.
	 * @param gurasoa    Aurreko leihoa (PerfilaGUI), atzera egiterakoan erakusteko.
	 */
	public SaldutakoakGUI(Registered saltzailea, PerfilaGUI gurasoa) {
		this.gurasoa = gurasoa;
		
		// Taulan errenkada anitz aukeratzeko ahalmena izateko
		produktuTaula.setRowSelectionAllowed(true);
		produktuTaula.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		leihoHau = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		this.setSize(new Dimension(800, 600));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("ErositakoakGUI.FindProducts"));
		produktuEtiketa.setBounds(52, 108, 427, 16);
		this.getContentPane().add(produktuEtiketa);

		itxiBotoia.setBounds(new Rectangle(220, 379, 130, 30));

		itxiBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leihoHau.setVisible(false);
			}
		});		
		
		this.getContentPane().add(itxiBotoia, null);
		
		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(null);

		produktuKorritzePanela.setBounds(new Rectangle(52, 137, 691, 327));

		produktuKorritzePanela.setViewportView(produktuTaula);
		produktuTaulaEredua = new DefaultTableModel(null, produktuZutabeIzenak);
		
		produktuTaula.setModel(produktuTaulaEredua);
		
		// Hurrengo hiru lerroek gelaxkak ez editagarriak izatea bermatzen dute
		produktuTaula.setDefaultEditor(Object.class, null);
		produktuTaula.editCellAt(-1, -1);
		produktuTaula.setRowHeight(35);
		produktuTaula.setFont(new Font("SansSerif", Font.PLAIN, 15));

		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
		produktuTaulaEredua.setColumnCount(4); 

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

		// Laugarren zutabea ez da erakusten (objektu osoa gordetzeko erabiltzen da)
		produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3)); 

		this.getContentPane().add(produktuKorritzePanela, null);
		
		bilaketaTestuEremua = new JTextField();
		bilaketaTestuEremua.setBounds(52, 56, 501, 44);
		getContentPane().add(bilaketaTestuEremua);
		bilaketaTestuEremua.setColumns(10);
		
		// --- BILAKETA BOTOIAREN LOGIKA ---
		bilatuBotoia.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent e) {
		 		try {
					produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
					produktuTaulaEredua.setColumnCount(4);

					BLFacade facade = MainGUI.getBusinessLogic();
					Date today = UtilDate.trim(new Date());
					

					// Erabiltzaileak saldutako elementuak eskuratzen ditugu
					System.out.println(saltzailea.getEmail());
					List<BoughtSale> saldutakoak = facade.getSoldBySeller(saltzailea.getEmail(), bilaketaTestuEremua.getText(), today);

					if (saldutakoak.isEmpty()) {
					    produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
					    produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}
					System.out.println(saldutakoak);
					// Taula salmentekin betetzen dugu
					for (BoughtSale salmenta : saldutakoak) {
						
						Vector<Object> errenkada = new Vector<Object>();
						errenkada.add(salmenta.getS().getTitle());
						errenkada.add(salmenta.getS().getPrice());
						errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getErosketaData().getTime()));
						errenkada.add(salmenta); 
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
		 bilatuBotoia.setBounds(583, 54, 135, 46);
		 getContentPane().add(bilatuBotoia);
		 
		 // --- TAULAN KLIK BIKOITZA EGITEKO LOGIKA ---
		 produktuTaula.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            
		            if(mouseEvent.getClickCount() == 2) {
				        JTable taula = (JTable) mouseEvent.getSource();
		            	Point puntua = mouseEvent.getPoint();
				        int errenkada = taula.rowAtPoint(puntua);
				        
				        // Ezkutuko zutabetik objektua lortu eta xehetasunen leihoa ireki
		            	BoughtSale aukeratutakoSalmenta = (BoughtSale) produktuTaulaEredua.getValueAt(errenkada, 3);
			            
		            	new ErositakoaShowSaleGUI(aukeratutakoSalmenta, saltzailea, SaldutakoakGUI.this);
			            SaldutakoakGUI.this.setVisible(false);
		            }
		        }
		 });
		
		 // --- ATZERA BOTOIA ---
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaldutakoakGUI.this.setVisible(false);
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(646, 500, 100, 26);
		edukiPanela.add(atzeraBotoia);
	}
}