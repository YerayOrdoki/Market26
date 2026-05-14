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
import exceptions.*;
import extra.BotoiBorobila;

/**
 * Administratzaileari eskalatu zaizkion erreklamazioak erakusten dituen interfaze grafikoa.
 */
public class ErreklamazioakAdminGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // --- ATRIBUTUAK ---
    private JPanel edukiPanela;
    private JTable erreklamazioTaula;
    private DefaultTableModel taulaEredua;
    private JFrame gurasoa;
    private Admin administratzailea;

    private String[] zutabeIzenak = {ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Titulua"),
    		ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Eroslea"),
    		ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Saltzailea"),
    		ResourceBundle.getBundle("Etiquetas").getString("BuzoiaGUI.Data")
   			};

    // --- ERAIKITZAILEA ---

    /**
     * ErreklamazioakAdminGUI klasearen eraikitzailea.
     * Leihoaren ezarpenak konfiguratzen ditu eta datuen taula kargatzen du.
     * * @param admin   Saioa hasi duen administratzailea.
     * @param gurasoa Aurreko leihoa (nondik gatozen), atzera egitean berriro erakusteko.
     */
    public ErreklamazioakAdminGUI(Admin admin, JFrame gurasoa) {
        this.administratzailea = admin;
        this.gurasoa = gurasoa;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setTitle(ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Erreklamazioak"));
        
        //"Administratzailea - Erreklamazio Eskalatuak"
        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }

        edukiPanela = new JPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(null);

        JLabel izenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.ErreklamazioakTitle"));
        izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 16));
        izenburuEtiketa.setBounds(52, 30, 400, 30);
        edukiPanela.add(izenburuEtiketa);

        JScrollPane korritzePanela = new JScrollPane();
        korritzePanela.setBounds(52, 80, 690, 380);
        edukiPanela.add(korritzePanela);

        taulaEredua = new DefaultTableModel(null, zutabeIzenak);
        erreklamazioTaula = new JTable(taulaEredua);
        
        // Taulako gelaxkak ez editatzeko
        erreklamazioTaula.setDefaultEditor(Object.class, null); 
        erreklamazioTaula.setRowHeight(30);
        erreklamazioTaula.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        korritzePanela.setViewportView(erreklamazioTaula);

        // --- DATUAK KARGATU ---
        kargatuDatuak();

        // Klik bikoitza egitean, erreklamazioaren gatazka-haria irekitzeko ekintza
        erreklamazioTaula.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int errenkada = erreklamazioTaula.rowAtPoint(e.getPoint());
                    
                    // Ezkutuan utzi dugun 5. zutabetik (indizea 4) erreklamazio objektua berreskuratzen dugu
                    Erreklamazioa aukeratutakoErreklamazioa = (Erreklamazioa) taulaEredua.getValueAt(errenkada, 4); 
                    
                    // Erreklamazio haria irekitzen dugu ADMINISTRATZAILEA erabiltzaile aktibo gisa pasatuz
                    new ErreklamazioHariaGUI(aukeratutakoErreklamazioa, administratzailea, ErreklamazioakAdminGUI.this).setVisible(true);
                    ErreklamazioakAdminGUI.this.setVisible(false);
                }
            }
        });

        BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera"));
        atzeraBotoia.setBounds(646, 500, 100, 30);
        atzeraBotoia.addActionListener(e -> {
            this.setVisible(false);
            if (gurasoa != null) gurasoa.setVisible(true);
        });
        edukiPanela.add(atzeraBotoia);
    }

    // --- METODOAK ---

    /**
     * Taulako datuak freskatzeko metodoa (adibidez, administratzailea gatazka ebatzi eta bueltatzen denean).
     * Datu-basetik eskalatutako erreklamazioak ekartzen ditu eta taulan sartzen ditu.
     */
    public void kargatuDatuak() {
        taulaEredua.setDataVector(null, zutabeIzenak);
        
        // 5 zutabe jartzen ditugu erreklamazio objektua ezkutuan gordetzeko
        taulaEredua.setColumnCount(5); 

        BLFacade facade = MainGUI.getBusinessLogic();
        List<Erreklamazioa> zerrenda = facade.getErreklamazioEskalatuak();

        SimpleDateFormat dataFormatua = new SimpleDateFormat("dd-MM-yyyy");
       
        for (Erreklamazioa erreklamazioa : zerrenda) {
        	
            Vector<Object> errenkada = new Vector<Object>();
            errenkada.add(erreklamazioa.getTitle());
            errenkada.add(facade.getBuyerByErreklamazioa(erreklamazioa).getEmail());
            errenkada.add(facade.getSellerByErreklamazioa(erreklamazioa).getEmail());
            errenkada.add(dataFormatua.format(erreklamazioa.getPubDate().getTime()));
            errenkada.add(erreklamazioa); // Objektua bera, ezkutuan egongo dena
            taulaEredua.addRow(errenkada);
        }

        // Azken zutabea (objektua daukana, 4 indizea) erabiltzailearen bistatik kentzen dugu
        erreklamazioTaula.getColumnModel().removeColumn(erreklamazioTaula.getColumnModel().getColumn(4));
    }
}