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
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Erabiltzaile batek gordetako erreserbatutako produktuak erakusten dituen
 * interfaze grafikoa.
 */
public class ErreserbatuakGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// --- ATRIBUTUAK ---

	private final JLabel produktuEtiketa = new JLabel(
			ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));

	private BotoiBorobila bilatuBotoia = new BotoiBorobila(
			ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search"));
	private BotoiBorobila itxiBotoia = new BotoiBorobila(
			ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private BotoiBorobila btnErreserbatuakKendu;

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
	 * ErreserbatuakGUI klasearen eraikitzailea.
	 * Taula bat sortzen du erabiltzailearen erreserbekin eta ekintza-botoiak
	 * konfiguratzen ditu.
	 *
	 * @param saltzailea Aplikazioa erabiltzen ari den erabiltzaile erregistratua.
	 * @param gurasoa Aurreko leihoa, atzera egitean berriro erakusteko.
	 */
	public ErreserbatuakGUI(Registered saltzailea, MainGUIerregistratua gurasoa) {
		this.gurasoa = gurasoa;
		this.saltzailea = saltzailea;

		// Taulan errenkada bat edo gehiago aukeratzeko aukera ematen da.
		produktuTaula.setRowSelectionAllowed(true);
		produktuTaula.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		leihoHau = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);

		if (gurasoa != null) {
			this.setLocation(gurasoa.getLocation());
		}

		this.setSize(new Dimension(800, 600));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("ErreserbakGUI.Title"));
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

		// Hurrengo lerroek taula ez editagarria izatea bermatzen dute.
		produktuTaula.setDefaultEditor(Object.class, null);
		produktuTaula.editCellAt(-1, -1);
		produktuTaula.setRowHeight(35);
		produktuTaula.setFont(new Font("SansSerif", Font.PLAIN, 15));

		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);

		// 4. zutabean objektua bera gordetzen da, gero ezkutatzeko.
		produktuTaulaEredua.setColumnCount(4);

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

		// Laugarren zutabea erabiltzaileari ezkutatzen zaio, baina ereduan mantentzen da.
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
					Date gaur = UtilDate.trim(new Date());

					Registered erabiltzaileEguneratua = facade.getSellerByEmail(saltzailea.getEmail());
					List<Sale> erreserbatuak = erabiltzaileEguneratua.getVip().getErreserbak();

					if (erreserbatuak.isEmpty()) {
						btnErreserbatuakKendu.setVisible(false);
						produktuEtiketa.setText(
								ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
						btnErreserbatuakKendu.setVisible(true);
						produktuEtiketa.setText(
								ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}

					for (Sale salmenta : erreserbatuak) {
						if (salmenta == null) {
							continue;
						}
						if (salmenta.getPubDate() != null) {
							Vector<Object> errenkada = new Vector<Object>();
							errenkada.add(salmenta.getTitle());
							errenkada.add(salmenta.getPrice());
							errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
							errenkada.add(salmenta);
							produktuTaulaEredua.addRow(errenkada);
						} else {
							// Produktua ezabatua izan bada, lotura zaharra kentzen da.
							facade.faboritoaEzabatu(salmenta.getSaleNumber(), saltzailea.getEmail());
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(10);
				produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);

				// Objektuaren zutabea berriro ezkutatzen da taula berreraiki ondoren.
				produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3));
			}
		});
		bilatuBotoia.setBounds(583, 54, 135, 46);
		getContentPane().add(bilatuBotoia);

		// --- ERRESERBA KENDU BOTOIAREN LOGIKA ---

		btnErreserbatuakKendu = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("ErreserbakGUI.RemoveErreserbak"));
		btnErreserbatuakKendu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int[] aukeratutakoErrenkadak = produktuTaula.getSelectedRows();

				if (aukeratutakoErrenkadak.length > 0) {
					BLFacade facade = MainGUI.getBusinessLogic();
					int kontagailua = 0;

					for (int errenkada : aukeratutakoErrenkadak) {
						Sale salmenta = (Sale) produktuTaulaEredua.getValueAt(errenkada, 3);
						facade.saleDesErreserbatu(salmenta.getSaleNumber(), saltzailea.getVip().getId());
						kontagailua++;
					}

					// Taula berriz kargatzeko, bilaketa-botoiaren ekintza bera berrerabiltzen da.
					bilatuBotoia.doClick();
					JOptionPane.showMessageDialog(null,
							kontagailua + " "
									+ ResourceBundle.getBundle("Etiquetas")
											.getString("ErreserbakGUI.EzabatutakoErreserbaKop"));
				} else {
					JOptionPane.showMessageDialog(null,
							ResourceBundle.getBundle("Etiquetas")
									.getString("FavoritoGUI.EzDagoEzerAukeratuta"));
				}
			}
		});
		btnErreserbatuakKendu.setBounds(52, 473, 185, 22);
		getContentPane().add(btnErreserbatuakKendu);
		btnErreserbatuakKendu.setVisible(false);

		// --- TAULAN KLIK EGITEKO LOGIKA ---

		produktuTaula.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {

				if (mouseEvent.getClickCount() == 2) {
					JTable taula = (JTable) mouseEvent.getSource();
					Point puntua = mouseEvent.getPoint();
					int errenkada = taula.rowAtPoint(puntua);

					// Ezkutuko zutabetik objektu osoa berreskuratzen da.
					Sale aukeratutakoSalmenta = (Sale) produktuTaulaEredua.getValueAt(errenkada, 3);
					bilaketaTestuEremua.setText("");

					// Salmentaren xehetasun-leihoa irekitzen da.
					new ShowSaleGUI(aukeratutakoSalmenta, saltzailea, ErreserbatuakGUI.this, null);
				}
			}
		});

		// --- ATZERA BOTOIA ---

		BotoiBorobila atzeraBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera"));
		atzeraBotoia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ErreserbatuakGUI.this.setVisible(false);
				bilaketaTestuEremua.setText("");
				gurasoa.setVisible(true);
			}
		});
		atzeraBotoia.setBounds(646, 500, 100, 26);
		edukiPanela.add(atzeraBotoia);
	}

	/**
	 * Taulako edukia berritzen du datu-baseko une honetako erreserbekin.
	 * Metodo hau erabilgarria da leihoa berreraiki gabe edukia eguneratzeko.
	 */
	public void refreshTable() {
		BLFacade facade = MainGUI.getBusinessLogic();

		produktuTaulaEredua.setDataVector(null, produktuZutabeIzenak);
		produktuTaulaEredua.setColumnCount(4);

		Registered erabiltzaileEguneratua = facade.getSellerByEmail(saltzailea.getEmail());
		List<Sale> erreserbatuak = erabiltzaileEguneratua.getVip().getErreserbak();

		if (erreserbatuak.isEmpty()) {
			btnErreserbatuakKendu.setVisible(false);
			produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
		} else {
			btnErreserbatuakKendu.setVisible(true);
			produktuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
		}

		for (Sale salmenta : erreserbatuak) {
			if (salmenta == null) {
				continue;
			}
			if (salmenta.getPubDate() != null) {
				Vector<Object> errenkada = new Vector<>();
				errenkada.add(salmenta.getTitle());
				errenkada.add(salmenta.getPrice());
				errenkada.add(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
				errenkada.add(salmenta);
				produktuTaulaEredua.addRow(errenkada);
			} else {
				facade.faboritoaEzabatu(salmenta.getSaleNumber(), erabiltzaileEguneratua.getEmail());
			}
		}

		produktuTaula.getColumnModel().getColumn(0).setPreferredWidth(200);
		produktuTaula.getColumnModel().getColumn(1).setPreferredWidth(70);
		produktuTaula.getColumnModel().removeColumn(produktuTaula.getColumnModel().getColumn(3));
	}
}