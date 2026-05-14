package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import extra.BotoiBorobila;

/**
 * Administratzaileari eskalatu zaizkion erreklamazioak erakusten dituen
 * interfaze grafikoa.
 */
public class SalaketakAdminGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// --- ATRIBUTUAK ---
	private JPanel edukiPanela;
	private JTable salaketaTaula;
	private DefaultTableModel taulaEredua;
	private JFrame gurasoa;
	private Admin administratzailea;

	private String[] zutabeIzenak = { ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Titulua"),
			ResourceBundle.getBundle("Etiquetas").getString("SalaketakAdminGUI.SalaketaMota"),
			ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Saltzailea"),
			ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Salatzailea"),
			ResourceBundle.getBundle("Etiquetas").getString("BuzoiaGUI.Data") };

	// --- ERAIKITZAILEA ---

	/**
	 * ErreklamazioakAdminGUI klasearen eraikitzailea. Leihoaren ezarpenak
	 * konfiguratzen ditu eta datuen taula kargatzen du. * @param admin Saioa hasi
	 * duen administratzailea.
	 * 
	 * @param gurasoa Aurreko leihoa (nondik gatozen), atzera egitean berriro
	 *                erakusteko.
	 */
	public SalaketakAdminGUI(Admin admin, JFrame gurasoa) {
		this.administratzailea = admin;
		this.gurasoa = gurasoa;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 600));
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("SalaketakAdminGUI.Title"));

		if (gurasoa != null) {
			this.setLocation(gurasoa.getLocation());
		}

		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(null);

		JLabel izenburuEtiketa = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.SalaketaZerrenda"));
		izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 16));
		izenburuEtiketa.setBounds(52, 30, 400, 30);
		edukiPanela.add(izenburuEtiketa);

		JScrollPane korritzePanela = new JScrollPane();
		korritzePanela.setBounds(52, 80, 690, 380);
		edukiPanela.add(korritzePanela);

		taulaEredua = new DefaultTableModel(null, zutabeIzenak);
		salaketaTaula = new JTable(taulaEredua);

		// Taulako gelaxkak ez editatzeko
		salaketaTaula.setDefaultEditor(Object.class, null);
		salaketaTaula.setRowHeight(30);
		salaketaTaula.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		korritzePanela.setViewportView(salaketaTaula);

		// --- DATUAK KARGATU ---
		kargatuDatuak();

		// Klik bikoitza egitean, erreklamazioaren gatazka-haria irekitzeko ekintza
		salaketaTaula.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int errenkada = salaketaTaula.rowAtPoint(e.getPoint());

					// Ezkutuan utzi dugun 5. zutabetik (indizea 4) erreklamazio objektua
					// berreskuratzen dugu
					Salaketa aukeratutakoSalaketa = (Salaketa) taulaEredua.getValueAt(errenkada, 5);

					// Erreklamazio haria irekitzen dugu ADMINISTRATZAILEA erabiltzaile aktibo gisa
					// pasatuz
					new ShowSaleGUI((Sale) aukeratutakoSalaketa.getProduktoa(), admin, SalaketakAdminGUI.this,
							aukeratutakoSalaketa).setVisible(true);
					SalaketakAdminGUI.this.setVisible(false);
				}
			}
		});

		BotoiBorobila atzeraBotoia = new BotoiBorobila(
				ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
		atzeraBotoia.setBounds(646, 500, 100, 30);
		atzeraBotoia.addActionListener(e -> {
			this.setVisible(false);
			if (gurasoa != null)
				gurasoa.setVisible(true);
		});
		edukiPanela.add(atzeraBotoia);
	}

	// --- METODOAK ---

	/**
	 * Taulako datuak freskatzeko metodoa (adibidez, administratzailea gatazka
	 * ebatzi eta bueltatzen denean). Datu-basetik eskalatutako erreklamazioak
	 * ekartzen ditu eta taulan sartzen ditu.
	 */
	public void kargatuDatuak() {
	    taulaEredua.setDataVector(null, zutabeIzenak);

	    // 5 zutabe jartzen ditugu erreklamazio objektua ezkutuan gordetzeko
	    taulaEredua.setColumnCount(6);

	    BLFacade facade = MainGUI.getBusinessLogic();
	    List<Salaketa> zerrenda = facade.getSalaketak();
	    System.out.println(zerrenda);
	    SimpleDateFormat dataFormatua = new SimpleDateFormat("dd-MM-yyyy");

	    for (Salaketa salaketa : zerrenda) {
	        System.out.println(salaketa);

	        // NULL EGIAZTAPENA: salaketa edo produktoa null bada, saltatu
	        if (salaketa == null || salaketa.getProduktoa() == null) {
	            System.out.println("ABISUA: salaketa edo produktoa null da, saltatu egiten da.");
	            continue;
	        }

	        System.out.println(((Sale) salaketa.getProduktoa()));

	        SaleSellerBoughtContainer laguntzaile = facade.getSaleSellerBoughtContainer(
	                salaketa.getProduktoa().getSaleNumber());
	        
	        // laguntzaile null bada ere saltatu
	        if (laguntzaile == null) {
	            System.out.println("ABISUA: laguntzaile null da, saltatu egiten da.");
	            continue;
	        }

	        Registered uneko = laguntzaile.getUser();
	        User u = facade.getSalatzaileaBySalaketa(salaketa);
	        System.out.println(uneko);

	        if (uneko != null && u != null) {
	            Vector<Object> errenkada = new Vector<Object>();
	            errenkada.add(((Sale) salaketa.getProduktoa()).getTitle());
	            errenkada.add(salaketa.getMota());
	            errenkada.add(uneko.getEmail());
	            errenkada.add(u.getEmail());
	            errenkada.add(dataFormatua.format(salaketa.getPubDate().getTime()));
	            errenkada.add(salaketa); // Objektua bera, ezkutuan egongo dena
	            taulaEredua.addRow(errenkada);
	        }
	    }

	    // Azken zutabea (objektua daukana, 5 indizea) erabiltzailearen bistatik kentzen dugu
	    salaketaTaula.getColumnModel().removeColumn(salaketaTaula.getColumnModel().getColumn(5));
	    egokituZutabeZabalera();
	}

	private void egokituZutabeZabalera() {
		for (int zutabe = 0; zutabe < salaketaTaula.getColumnCount(); zutabe++) {
			int zabalMax = salaketaTaula.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(salaketaTaula,
							salaketaTaula.getColumnModel().getColumn(zutabe).getHeaderValue(), false, false, -1, zutabe)
					.getPreferredSize().width;

			for (int errenkada = 0; errenkada < salaketaTaula.getRowCount(); errenkada++) {
				int zabalera = salaketaTaula
						.getCellRenderer(errenkada, zutabe).getTableCellRendererComponent(salaketaTaula,
								salaketaTaula.getValueAt(errenkada, zutabe), false, false, errenkada, zutabe)
						.getPreferredSize().width;
				zabalMax = Math.max(zabalMax, zabalera);
			}
			salaketaTaula.getColumnModel().getColumn(zutabe).setPreferredWidth(zabalMax + 10);
		}
	}
	public void freskatuTaula() {
	    // Lehenik zutabeak berrezarri, bestela removeColumn crash egin dezake
	    taulaEredua.setDataVector(null, zutabeIzenak);
	    taulaEredua.setColumnCount(6);

	    // Zutabeak berriz gehitu taularen ereduan daudenak erabiltzen
	    while (salaketaTaula.getColumnModel().getColumnCount() > 0) {
	        salaketaTaula.getColumnModel().removeColumn(
	            salaketaTaula.getColumnModel().getColumn(0));
	    }

	    // Datuak berriro kargatu
	    kargatuDatuak();
	}
}