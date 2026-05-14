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
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SaskiaGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JLabel jLabelProducts = new JLabel(
			ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));

	public BotoiBorobila jButtonSearch = new BotoiBorobila(
			ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search"));

	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts = new JTable();
	private DefaultTableModel tableModelProducts;
	private JFrame thisFrame;

	private String[] columnNamesProducts = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"),
	};

	private JTextField jTextFieldSearch;
	private ShowRegisteredGUI parent;
	private JPanel contentPane;
	private JLabel prezioa = new JLabel();

	private Registered saltzaile;

	private final JLabel bidalketaKostuaTXT = new JLabel(
			ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Bidalketa"));
	private final JLabel bidalketaKostuaPrezioa = new JLabel();

	/**
	 * Saskiaren leihoa sortzen du eta bere osagai guztiak hasieratzen ditu.
	 *
	 * @param saltzaile Uneko erabiltzaile erregistratua.
	 * @param parent Aurreko ShowRegisteredGUI leihoa.
	 * @param parent2 Itzultzeko erabiliko den aurreko leihoa.
	 */
	public SaskiaGUI(Registered saltzaile, ShowRegisteredGUI parent, JFrame parent2) {
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("SaskiaGUI.Title"));
		this.saltzaile = saltzaile;

		BLFacade facade = MainGUI.getBusinessLogic();
		System.out.println(">> SaskiaGUI => saltzaile: " + saltzaile.getEmail());

		this.parent = parent;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (parent != null) {
			this.setLocation(parent.getLocation());
		}

		tableProducts.setEnabled(false);
		thisFrame = this;
		this.setSize(new Dimension(800, 600));

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		jLabelProducts.setBounds(46, 95, 427, 16);
		contentPane.add(jLabelProducts);

		scrollPanelProducts.setBounds(new Rectangle(52, 110, 691, 355));
		scrollPanelProducts.setViewportView(tableProducts);

		tableModelProducts = new DefaultTableModel(null, columnNamesProducts);
		tableProducts.setModel(tableModelProducts);
		tableProducts.setDefaultEditor(Object.class, null);
		tableProducts.editCellAt(-1, -1);
		tableProducts.setRowHeight(35);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 15));

		tableModelProducts.setDataVector(null, columnNamesProducts);

		// 4. zutabean objektu osoa gordetzen da, gero ikuspegitik ezkutatzeko.
		tableModelProducts.setColumnCount(4);

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3));

		contentPane.add(scrollPanelProducts);

		jTextFieldSearch = new JTextField();
		jTextFieldSearch.setBounds(78, 53, 478, 31);
		jTextFieldSearch.setColumns(10);
		contentPane.add(jTextFieldSearch);

		jButtonSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
		jButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saskiaKargatu(saltzaile, jTextFieldSearch.getText());
			}
		});
		jButtonSearch.setBounds(591, 45, 126, 46);
		contentPane.add(jButtonSearch);

		tableProducts.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					JTable taula = (JTable) mouseEvent.getSource();
					Point puntua = mouseEvent.getPoint();
					int errenkada = taula.rowAtPoint(puntua);

					Sale salmenta = (Sale) tableModelProducts.getValueAt(errenkada, 3);
					SaskiaGUI.this.setVisible(false);
					jTextFieldSearch.setText("");
					new ShowSaleGUI(salmenta, saltzaile, SaskiaGUI.this, null);
				}
			}
		});

		BotoiBorobila btnAtzera = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera"));
		btnAtzera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaskiaGUI.this.setVisible(false);
				jTextFieldSearch.setText("");
				parent.refreshTable();
				parent.setVisible(true);
			}
		});
		btnAtzera.setBounds(687, 498, 101, 37);
		contentPane.add(btnAtzera);

		BotoiBorobila btnErosiProduktuak = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("SaskiaGUI.erosiProduk"));
		btnErosiProduktuak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Registered saltzaileEguneratua = facade.getSellerByEmail(saltzaile.getEmail());

				double prezioTotala = facade.getSaskiaPrezioTotala(saltzaileEguneratua.getEmail());
				List<Sale> saskia = facade.getSaskiaLista("", saltzaileEguneratua.getEmail());

				if (saskia.isEmpty()) {
					JOptionPane.showMessageDialog(
							SaskiaGUI.this,
							ResourceBundle.getBundle("Etiquetas").getString("SaskiaGUI.SaskiaHutsa"),
							"ERROR",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				double bidalketaKostua = 0.0;
				if (!saskia.isEmpty() && saltzaileEguneratua.getVip() == null) {
					bidalketaKostua = facade.getSaskiaBidalketaKostua(
							saltzaileEguneratua.getEmail(),
							saskia.get(0).getSeller().getEmail());
				}

				double guztizkoKostua = prezioTotala + bidalketaKostua;

				System.out.println("=== DEBUG EROSKETA ===");
				System.out.println("Saldoa: " + saltzaileEguneratua.getSaldoa());
				System.out.println("PrezioTotala: " + prezioTotala);
				System.out.println("BidalketaKostua: " + bidalketaKostua);
				System.out.println("GuztizkoKostua: " + guztizkoKostua);
				System.out.println("VIP: " + saltzaileEguneratua.getVip());
				System.out.println("=====================");

				if (saltzaileEguneratua.getSaldoa() < guztizkoKostua) {
					JOptionPane.showMessageDialog(
							SaskiaGUI.this,
							ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Error2"),
							"ERROR",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				facade.saskikoProduktuakErosi(saltzaileEguneratua.getEmail());
				JOptionPane.showMessageDialog(
						SaskiaGUI.this,
						saskia.size() + " "
								+ ResourceBundle.getBundle("Etiquetas").getString("SaskiaGUI.ProductoakErosi"),
						"",
						JOptionPane.INFORMATION_MESSAGE);

				if (parent2 instanceof QueryUsersGUI) {
					((QueryUsersGUI) parent2).refreshTable();
				} else if (parent2 instanceof ShowRegisteredGUI) {
					((ShowRegisteredGUI) parent2).refreshTable();
				}

				SaskiaGUI.this.setVisible(false);
				parent2.setVisible(true);
			}
		});
		btnErosiProduktuak.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnErosiProduktuak.setBounds(408, 476, 256, 75);
		contentPane.add(btnErosiProduktuak);

		prezioa.setText("" + facade.getSaskiaPrezioTotala(saltzaile.getEmail()) + "€");
		prezioa.setFont(new Font("Tahoma", Font.PLAIN, 20));
		prezioa.setBounds(319, 502, 154, 40);
		contentPane.add(prezioa);

		JLabel prezioTotalaEtiketa = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("SaskiaGUI.Totala"));
		prezioTotalaEtiketa.setFont(new Font("Tahoma", Font.BOLD, 17));
		prezioTotalaEtiketa.setBounds(32, 502, 287, 40);
		contentPane.add(prezioTotalaEtiketa);

		bidalketaKostuaTXT.setFont(new Font("Tahoma", Font.BOLD, 17));
		bidalketaKostuaTXT.setBounds(32, 476, 212, 24);
		contentPane.add(bidalketaKostuaTXT);

		List<Sale> saskia = facade.getSaskiaLista("", saltzaile.getEmail());
		if (!saskia.isEmpty()) {
			bidalketaKostuaPrezioa.setText("" + facade.getSaskiaBidalketaKostua(
					saltzaile.getEmail(),
					saskia.get(0).getSeller().getEmail()) + "€");
		} else {
			bidalketaKostuaPrezioa.setText("0.0€");
		}

		bidalketaKostuaPrezioa.setFont(new Font("Tahoma", Font.PLAIN, 17));
		bidalketaKostuaPrezioa.setBounds(220, 470, 154, 40);
		contentPane.add(bidalketaKostuaPrezioa);

		saskiaKargatu(saltzaile, "");
		this.setVisible(true);
	}

	/**
	 * Bilaketa-eremua garbitzen du.
	 */
	public void Garbitu() {
		jTextFieldSearch.setText("");
	}

	/**
	 * Taulako eta prezioen informazioa berritzen du.
	 */
	public void refreshTable() {
		BLFacade facade = MainGUI.getBusinessLogic();

		saskiaKargatu(saltzaile, jTextFieldSearch.getText());
		prezioa.setText("" + facade.getSaskiaPrezioTotala(saltzaile.getEmail()) + "€");

		List<Sale> saskia = facade.getSaskiaLista("", saltzaile.getEmail());
		if (!saskia.isEmpty()) {
			bidalketaKostuaPrezioa.setText("" + facade.getSaskiaBidalketaKostua(
					saltzaile.getEmail(),
					saskia.get(0).getSeller().getEmail()) + "€");
		} else {
			bidalketaKostuaPrezioa.setText("0.0€");
		}
	}

	/**
	 * Saskiko produktuak kargatzen ditu taulan, bilaketa-testuaren arabera iragazita.
	 *
	 * @param saltzaile Uneko erabiltzailea.
	 * @param desc Bilaketarako testua.
	 */
	private void saskiaKargatu(Registered saltzaile, String desc) {
		try {
			tableModelProducts.setDataVector(null, columnNamesProducts);
			tableModelProducts.setColumnCount(4);

			BLFacade facade = MainGUI.getBusinessLogic();
			List<Sale> sales = facade.getSaskiaLista(desc, saltzaile.getEmail());

			if (sales.isEmpty()) {
				jLabelProducts.setText(
						ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
			} else {
				jLabelProducts.setText(
						ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
			}

			for (Sale sale : sales) {
				// Produktua jada erosia edo ezabatua badago, ez da taulan erakusten.
				if (sale == null || sale.getTitle() == null || sale.getPubDate() == null) {
					continue;
				}

				Vector<Object> row = new Vector<>();
				row.add(sale.getTitle());
				row.add(sale.getPrice());
				row.add(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPubDate().getTime()));
				row.add(sale);
				tableModelProducts.addRow(row);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);

		// 4. zutabean gordetako objektua ezkutatzen da, baina ereduan erabilgarri uzten da.
		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3));
	}
}