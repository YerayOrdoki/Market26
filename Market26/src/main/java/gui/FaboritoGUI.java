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
 * Erabiltzaile batek gordetako produktu faboritoak erakusten dituen interfaze grafikoa.
 */
public class FaboritoGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	// --- ATRIBUTUAK ---
	private final JLabel produktuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	private BotoiBorobila bilatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 
	private BotoiBorobila itxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private BotoiBorobila faboritoaKenduBotoia;

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
	private MainGUIerregistratua gurasoa;
	private JPanel edukiPanela;
	
	private Registered saltzailea;
	
	// --- ERAIKITZAILEA ---

	/**
	 * FaboritoGUI klasearen eraikitzailea.
	 * Taula bat sortzen du erabiltzailearen faboritoekin eta ekintza-botoiak konfiguratzen ditu.
	 * * @param saltzailea Aplikazioa erabiltzen ari den erabiltzaile erregistratua.
	 * @param gurasoa    Aurreko leihoa (nondik gatozen), atzera egitean erakusteko.
	 */
	public FaboritoGUI(Registered saltzailea, MainGUIerregistratua gurasoa) {
		this.saltzailea=saltzailea;
		this.gurasoa = gurasoa;
		
		// Taulan errenkada bat edo gehiago aukeratzeko ahalmena izateko
		produktuTaula.setRowSelectionAllowed(true);
		produktuTaula.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		leihoHau = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		
		if (gurasoa != null) {
	        this.setLocation(gurasoa.getLocation());
	    }
		
		this.setSize(new Dimension(800,600));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("FaboritoGUI.FindProducts"));
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
		
		// Hurrengo hiru lerroek aukeraketa ez editatzeko gai izatea laguntzen dute
		produktuTaula.setDefaultEditor(Object.class, null);
		produktuTaula.editCellAt(-1, -1);
		produktuTaula.setRowHeight(35);
		produktuTaula.setFont(new Font("SansSerif", Font.PLAIN, 15));

		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
		
		// 4 zutabe jartzen ditugu objektua ezkutuan gordetzeko
		produktuTaulaEredua.setColumnCount(4); 

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

		// Laugarren zutabea ez da ikusten erabiltzailearentzat (objektua bera daukana)
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
					produktuTaulaEredua.setColumnCount(4); // 4. zutabe ezkutua gehitzen da

					BLFacade facade = MainGUI.getBusinessLogic();
					Date gaur = UtilDate.trim(new Date());

					// Datu-basetik erabiltzailearen faboritoak ekartzen ditugu
					List<Sale> faboritoak = facade.getFaboritoak(saltzailea.getEmail());

					if (faboritoak.isEmpty()) {
						faboritoaKenduBotoia.setVisible(false);
						produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
						faboritoaKenduBotoia.setVisible(true);
						produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}
					
					// Taula produktuez betetzen da
					for (Sale salmenta : faboritoak) {
                        if(salmenta.getPubDate() != null) {
							Vector<Object> errenkada = new Vector<Object>();
							errenkada.add(salmenta.getTitle());
							errenkada.add(salmenta.getPrice());
							errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
							errenkada.add(salmenta); // 3. indizean objektua bera gordetzen da
							produktuTaulaEredua.addRow(errenkada);	
						} else {
							// Argitalpen datarik ez badu, faboritoetatik ezabatzen da zuzenean (Produktua ezabatua izan denean adibidez)
                         	facade.faboritoaEzabatu(salmenta.getSaleNumber(), saltzailea.getEmail());      
                        }	
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		 		
				produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

				produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3)); // Objektuaren zutabea ezkutatu
		 	}
		 });
		 bilatuBotoia.setBounds(583, 54, 135, 46);
		 getContentPane().add(bilatuBotoia);

		 // --- FABORITOA KENDU BOTOIAREN LOGIKA ---
		 faboritoaKenduBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.RemoveFav"));
		 faboritoaKenduBotoia.addActionListener(new ActionListener() {
		     public void actionPerformed(ActionEvent e) {
		         
		    	 int[] aukeratutakoIlarak = produktuTaula.getSelectedRows();  // Ilara anitzak hartzeko aukera
		         
		         if (aukeratutakoIlarak.length > 0) {
		             BLFacade facade = MainGUI.getBusinessLogic();
		             int kontagailua = 0;
		             
		             for (int ilara : aukeratutakoIlarak) {
		                 Sale salmenta = (Sale) produktuTaulaEredua.getValueAt(ilara, 3);
		                 if(facade.faboritoaDa(salmenta.getSaleNumber(), saltzailea.getEmail())) {
		                	 facade.faboritoaEzabatu(salmenta.getSaleNumber(), saltzailea.getEmail());
		                 } else {
		                	 facade.faboritoaGehitu(salmenta.getSaleNumber(), saltzailea.getEmail());
		                 }
		                 
		                 kontagailua++;
		             }
		             
		             bilatuBotoia.doClick();  // Taula errefreskatzeko bilaketa botoia simulatzen da
		             JOptionPane.showMessageDialog(null, kontagailua + " " + ResourceBundle.getBundle("Etiquetas").getString("FavoritoGUI.EzabatutakoFaboritoKop"));
		         } else {
		             JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("FavoritoGUI.EzDagoEzerAukeratuta"));
		         }
		     }
		 });
		 faboritoaKenduBotoia.setBounds(52, 473, 185, 22);
		 getContentPane().add(faboritoaKenduBotoia);
		 faboritoaKenduBotoia.setVisible(false);
		
	    // --- TAULAN KLIK EGITEKO LOGIKA ---
		 produktuTaula.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            
		            if(mouseEvent.getClickCount() == 2) {
				        JTable taula = (JTable) mouseEvent.getSource();
		            	Point puntua = mouseEvent.getPoint();
				        int errenkada = taula.rowAtPoint(puntua);
				        
				        // Ezkutuko zutabetik objektua berreskuratzen dugu
		            	Sale aukeratutakoSalmenta = (Sale) produktuTaulaEredua.getValueAt(errenkada, 3);
		            	bilaketaTestuEremua.setText("");
		            	
		            	// Produktuaren ikuspegi zehatza irekitzen da
			            new ShowSaleGUI(aukeratutakoSalmenta, saltzailea, FaboritoGUI.this, null);
		            }
		        }
		 });
		
		 // --- ATZERA BOTOIA ---
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FaboritoGUI.this.setVisible(false);
				bilaketaTestuEremua.setText("");
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(646, 500, 100, 26);
		edukiPanela.add(atzeraBotoia);
	}
	public void refreshTable() {
	    BLFacade facade = MainGUI.getBusinessLogic();

	    produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
	    produktuTaulaEredua.setColumnCount(4);

	    List<Sale> faboritoak = facade.getFaboritoak(saltzailea.getEmail());

	    if (faboritoak.isEmpty()) {
	        faboritoaKenduBotoia.setVisible(false);
	        produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
	    } else {
	        faboritoaKenduBotoia.setVisible(true);
	        produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
	    }

	    for (Sale salmenta : faboritoak) {
	        if (salmenta.getPubDate() != null) {
	            Vector<Object> errenkada = new Vector<>();
	            errenkada.add(salmenta.getTitle());
	            errenkada.add(salmenta.getPrice());
	            errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
	            errenkada.add(salmenta);
	            produktuTaulaEredua.addRow(errenkada);
	        } else {
	            facade.faboritoaEzabatu(salmenta.getSaleNumber(), saltzailea.getEmail());
	        }
	    }

	    produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
	    produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);
	    produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3));
	}
}