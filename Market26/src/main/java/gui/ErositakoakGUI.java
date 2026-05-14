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

/**
 * Erabiltzaileak erositako produktuak bilatu eta ikusteko interfaze grafikoa.
 */
public class ErositakoakGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	// --- ATRIBUTUAK ---
	private final JLabel produktuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	private BotoiBorobila bilatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 
	private BotoiBorobila itxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Close"));

	private JScrollPane produktuKorritzePanela = new JScrollPane();
	private JTable produktuTaula= new JTable();

	private DefaultTableModel produktuTaulaEredua;

	private JFrame leihoHau; 

	private String[] produktuZutabeIzenak = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("ErositakoakGUI.BoughtDate"),
	};
	
	private JTextField bilaketaTestuEremua;
	private PerfilaGUI gurasoa;
	private JPanel edukiPanela;
	
	// --- ERAIKITZAILEA ---

	/**
	 * ErositakoakGUI klasearen eraikitzailea.
	 * Leihoaren ezarpenak konfiguratzen ditu eta elementu grafikoak sortzen ditu.
	 * * @param saltzailea Saioa hasi duen erabiltzailea (kasu honetan produktuak erosi dituena).
	 * @param gurasoa    Aurreko leihoa (nondik gatozen), atzera egitean berriro erakusteko.
	 */
	public ErositakoakGUI(Registered saltzailea, PerfilaGUI gurasoa) {
		this.gurasoa = gurasoa;
		
		// Taulan errenkadak aukeratzeko ahalmena izateko
		produktuTaula.setRowSelectionAllowed(true);
		produktuTaula.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		leihoHau = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		this.setSize(new Dimension(800,600));
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
		
		// Hurrengo hiru lerroek taulako gelaxkak ez editagarriak izatea bermatzen dute
		produktuTaula.setDefaultEditor(Object.class, null);
		produktuTaula.editCellAt(-1, -1);
		produktuTaula.setRowHeight(35);
		produktuTaula.setFont(new Font("SansSerif", Font.PLAIN, 15));

		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
		produktuTaulaEredua.setColumnCount(4); 

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

		// Laugarren zutabea (objektua bera daukana) ezkutatzen dugu ikusgai ez egoteko
		produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3)); 

		this.getContentPane().add(produktuKorritzePanela, null);
		
		bilaketaTestuEremua = new JTextField();
		bilaketaTestuEremua.setBounds(52, 56, 501, 44);
		getContentPane().add(bilaketaTestuEremua);
		bilaketaTestuEremua.setColumns(10);
		
		// Bilaketa botoiaren logikaren konfigurazioa
		 bilatuBotoia.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent e) {
		 		try {
					produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
					produktuTaulaEredua.setColumnCount(4);

					BLFacade facade = MainGUI.getBusinessLogic();

					// Datu-basetik erabiltzaile horren erositako datuak ekartzen ditugu
					List<BoughtSale> erositakoak = facade.getErositakoak(saltzailea.getEmail());

					if (erositakoak.isEmpty()) {
						produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
						produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}
					
					// Taula kargatzen dugu produktuekin
					for (BoughtSale salmenta : erositakoak) {
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
		 		
		 		// Zutabeen tamainak berriro doitzen ditugu eta laugarren zutabea ezkutatzen dugu
				produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

				produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3)); 
		 	}
		 });
		 bilatuBotoia.setBounds(583, 54, 135, 46);
		 getContentPane().add(bilatuBotoia);
		 
		 // Saguaren gertaerak: Klik bikoitza egitean elementuaren ikuspegi xehatua irekitzeko
		 produktuTaula.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            
		            if(mouseEvent.getClickCount() == 2) {
				        JTable taula = (JTable) mouseEvent.getSource();
		            	Point puntua = mouseEvent.getPoint();
				        int errenkada = taula.rowAtPoint(puntua);
				        
				        // Ezkutuan dagoen zutabetik (3 indizea) objektu osoa berreskuratzen dugu
		            	BoughtSale aukeratutakoSalmenta = (BoughtSale) produktuTaulaEredua.getValueAt(errenkada, 3);
		            	
			            new ErositakoaShowSaleGUI(aukeratutakoSalmenta, saltzailea, ErositakoakGUI.this);
			            ErositakoakGUI.this.setVisible(false);
		            }
		        }
		 });
		
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ErositakoakGUI.this.setVisible(false);
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(646, 500, 100, 26);
		edukiPanela.add(atzeraBotoia);
	}
}