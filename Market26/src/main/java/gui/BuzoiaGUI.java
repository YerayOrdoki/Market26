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
 * Erabiltzailearen jakinarazpenak (buzoia) erakusten dituen interfaze grafikoa.
 */
public class BuzoiaGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // --- ATRIBUTUAK ---
    private JPanel contentPane;
    private JTable buzoiTaula;
    private DefaultTableModel taulaEredua;
    private JFrame gurasoa;
    private Registered erabiltzailea;

    private String[] zutabeIzenak = {ResourceBundle.getBundle("Etiquetas").getString("BuzoiaGUI.Data"),ResourceBundle.getBundle("Etiquetas").getString("BuzoiaGUI.Mezua")};

    // --- ERAIKITZAILEA ---
    
    /**
     * BuzoiaGUI klasearen eraikitzailea.
     * Leihoaren ezarpenak konfiguratzen ditu eta elementu grafikoak sortzen ditu.
     * * @param erabiltzailea Saioa hasi duen erabiltzailea, bere notifikazioak kargatzeko.
     * @param gurasoa       Aurreko leihoa (nondik gatozen), atzera egitean berriro erakusteko.
     */
    public BuzoiaGUI(Registered erabiltzailea, JFrame gurasoa) {
        this.erabiltzailea = erabiltzailea;
        this.gurasoa = gurasoa;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setTitle(ResourceBundle.getBundle("Etiquetas").getString("BuzoiaGUI.BuzoiaJakinarazpenak"));
        
        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel izenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("BuzoiaGUI.Jakinarazpenak"));
        izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
        izenburuEtiketa.setBounds(52, 30, 400, 30);
        contentPane.add(izenburuEtiketa);

        JScrollPane korritzePanela = new JScrollPane();
        korritzePanela.setBounds(52, 80, 690, 380);
        contentPane.add(korritzePanela);

        taulaEredua = new DefaultTableModel(null, zutabeIzenak);
        buzoiTaula = new JTable(taulaEredua);
        
        // Taula mezuen zerrenda bat bezala ikusteko konfiguratzen dugu
        buzoiTaula.setDefaultEditor(Object.class, null); // Ezin da editatu
        buzoiTaula.setRowHeight(40); // Errenkada altuagoak testua ondo sartzeko
        buzoiTaula.setFont(new Font("SansSerif", Font.PLAIN, 14));
        buzoiTaula.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        korritzePanela.setViewportView(buzoiTaula);

        // --- DATUAK KARGATU ---
        kargatuNotifikazioak();

        // Atzera egiteko botoiaren konfigurazioa
        BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
        atzeraBotoia.setBounds(646, 500, 100, 30);
        atzeraBotoia.addActionListener(e -> {
            this.setVisible(false); // Oraingo buzoiaren leihoa ezkutatzen dugu
            
            // Profilatik badator, bola gorria (jakinarazpen berriak) eguneratzeko esaten diogu
            if (gurasoa instanceof PerfilaGUI) {
                ((PerfilaGUI) gurasoa).eguneratuBuzoia(); 
            }
            
            // Jatorrizko guraso-leihoa berriro erakusten dugu (suntsitu gabe mantendu baitugu)
            if (gurasoa != null) {
                gurasoa.setVisible(true);
            }
        });
        contentPane.add(atzeraBotoia);
    }

    // --- METODOAK ---

    /**
     * Datu-basetik erabiltzailearen notifikazioak eskuratu eta taulan kargatzen ditu.
     * Behin kargatuta, notifikazio guztiak "irakurrita" bezala markatzen ditu datu-basean.
     */
    private void kargatuNotifikazioak() {
        BLFacade facade = MainGUI.getBusinessLogic();
        List<Notifikazioa> zerrenda = facade.getNotifikazioak(erabiltzailea.getEmail());

        SimpleDateFormat dataFormatua = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Notifikazioa n : zerrenda) {
            Vector<Object> errenkada = new Vector<Object>();
            errenkada.add(dataFormatua.format(n.getData().getTime()));
            errenkada.add(n.getMezua());
            taulaEredua.addRow(errenkada);
        }

        // Zutabeen tamaina doitzen dugu: data txikia izateko eta mezuak espazio gehiago edukitzeko
        buzoiTaula.getColumnModel().getColumn(0).setPreferredWidth(150);
        buzoiTaula.getColumnModel().getColumn(0).setMaxWidth(150);
        buzoiTaula.getColumnModel().getColumn(1).setPreferredWidth(540);
        
        // --- IRAKURRITA BEZALA MARKATU ---
        // Erabiltzaileak leiho hau ireki eta zerrenda kargatu ondoren, 
        // datu-baseari mezu guztiak ikusi dituela esaten diogu zuzenean.
        facade.markatuIrakurrita(erabiltzailea.getEmail());
    }
}