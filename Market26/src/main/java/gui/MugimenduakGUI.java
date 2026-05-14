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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Erabiltzailearen kontuko mugimenduak (erosketak, diru-sarrerak eta diru-irteerak) ikusteko interfaze grafikoa.
 */
public class MugimenduakGUI extends JFrame {
	
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
			ResourceBundle.getBundle("Etiquetas").getString("ErositakoakGUI.BoughtDate"),
	};
	private JTextField bilaketaTestuEremua;
	
	private PerfilaGUI gurasoa;
	private JPanel edukiPanela;
	
	// --- ERAIKITZAILEA ---

	/**
	 * MugimenduakGUI klasearen eraikitzailea.
	 * Leihoaren elementu grafikoak konfiguratzen ditu eta datuen taula prestatzen du.
	 * @param saltzailea Aplikazioa erabiltzen ari den erabiltzaile erregistratua.
	 * @param gurasoa    Aurreko leihoa (nondik gatozen), atzera egitean erakusteko.
	 */
	public MugimenduakGUI(Registered saltzailea, PerfilaGUI gurasoa) {
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
		
		// Hurrengo hiru lerroek aukeraketa ez editatzeko gai izatea laguntzen dute
		produktuTaula.setDefaultEditor(Object.class, null);
		produktuTaula.editCellAt(-1, -1);
		produktuTaula.setRowHeight(35);
		produktuTaula.setFont(new Font("SansSerif", Font.PLAIN, 15));

		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
		produktuTaulaEredua.setColumnCount(4); 

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

		// 4. zutabea ez da erakutsiko (objektua bera daukana ezkutatzeko)
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
					
					// Datu-basetik erabiltzailearen mugimenduak ekartzen ditugu bilaketa irizpideen arabera
					List<Mugimendua> mugimenduak = facade.getMugimenduakBySeller(saltzailea.getEmail(), bilaketaTestuEremua.getText(), today);
					System.out.println(mugimenduak);
					
					if (mugimenduak.isEmpty()) {
					    produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
					    produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}
					
					// Taula mugimenduez betetzen da
					for (Mugimendua mugimendua : mugimenduak) {
						Vector<Object> errenkada = new Vector<Object>();
						
						// Produktu bat erosita bada (objektua badauka)
						if(mugimendua.getProduktua() != null) {
							errenkada.add(mugimendua.getProduktua().getS().getTitle());
							errenkada.add(mugimendua.getDiruKantitatea());
							errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(mugimendua.getData().getTime()));
							errenkada.add(mugimendua); 
						} else {
							// Produktu barik, diru-sarrera edo diru-irteera arrunta bada
							if(mugimendua.getMugimenduMota() == 1) {
								errenkada.add(ResourceBundle.getBundle("Etiquetas").getString("MugimenduakGUI.DiruSarrera"));
							} else {
								errenkada.add(ResourceBundle.getBundle("Etiquetas").getString("MugimenduakGUI.DiruIrteera"));
							}
							errenkada.add(mugimendua.getDiruKantitatea());
							errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(mugimendua.getData().getTime()));
							errenkada.add(mugimendua); 
						}
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
		 
		 produktuTaula.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			    @Override
			    public Component getTableCellRendererComponent(JTable table, Object value,
			            boolean isSelected, boolean hasFocus, int row, int column) {
			        
			        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			        
			        Mugimendua mugimendua = (Mugimendua) produktuTaulaEredua.getValueAt(row, 3);
			        
			        if (isSelected) {
			            c.setBackground(table.getSelectionBackground());
			        } else if (mugimendua != null && mugimendua.getMugimenduMota() == 1) {
			            c.setBackground(new Color(150, 255, 150)); // Berdea - diru sarrera
			        } else if (mugimendua != null && mugimendua.getMugimenduMota() != 1) {
			            c.setBackground(new Color(255, 150, 150)); // Gorria - diru irteera
			        } else {
			            c.setBackground(Color.WHITE);
			        }
			        
			        return c;
			    }
			});
		 
		 // --- TAULAN KLIK EGITEKO LOGIKA ---
		 produktuTaula.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            // Klik bikoitza bada
		            if(mouseEvent.getClickCount() == 2) {
		                JTable taula = (JTable) mouseEvent.getSource();
		                Point puntua = mouseEvent.getPoint();
		                int errenkada = taula.rowAtPoint(puntua);

		                // Ezkutuko zutabetik Mugimendua objektua berreskuratzen da
		                Mugimendua mugimendua = (Mugimendua) produktuTaulaEredua.getValueAt(errenkada, 3);
		                
		                // Bakarrik produktuei lotutako mugimenduak irekitzen dira beste leiho batean (Diru-sarrerak eta irteerak baztertuta)
		                if (mugimendua.getProduktua() != null) {
		                    BoughtSale salmenta = mugimendua.getProduktua();
		                    new ErositakoaShowSaleGUI(salmenta, saltzailea, MugimenduakGUI.this);
		                    MugimenduakGUI.this.setVisible(false);
		                }
		            }
		        }
		 });
		
		 // --- ATZERA BOTOIA ---
		BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MugimenduakGUI.this.setVisible(false);
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(646, 500, 100, 26);
		edukiPanela.add(atzeraBotoia);
	}
}