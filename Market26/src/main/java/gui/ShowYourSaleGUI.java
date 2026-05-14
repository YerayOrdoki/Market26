package gui;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.awt.image.BufferedImage;
import java.awt.geom.GeneralPath;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import extra.BotoiBorobila;
import extra.EstrellitaButton;

/**
 * Erabiltzaileak bere salmenta propioaren ikuspegi zehatza ikusteko interfaze grafikoa.
 * Hemen erabiltzaileak bere produktua faboritoetan sartu edo sistematik ezaba dezake.
 */
public class ShowYourSaleGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private static final int OINARRI_TAMAINA = 320;
    private static final String OINARRI_BIDEA = "src/main/resources/images/";

    public JPanel irudiPanela;
    File helburuFitxategia;
    BufferedImage helburuIrudia;
    private File aukeratutakoFitxategia;
    private String irudia;

    private JTextField izenburuTestuEremua = new JTextField();
    private JTextField deskribapenTestuEremua = new JTextField();
    private JTextField prezioTestuEremua = new JTextField();

    private JLabel egoeraEtiketa = new JLabel(); 
    private JLabel deskribapenIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description")); 
    private JLabel egoeraIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
    private JLabel mezuEtiketa = new JLabel();
    private JLabel erroreEtiketa = new JLabel();
    private JLabel produktuEgoeraEremua = new JLabel();
    private final JLabel sortutakoDataIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"));

    private JScrollPane gertaerenKorritzePanela = new JScrollPane();
    DefaultComboBoxModel<String> egoeraAukerak = new DefaultComboBoxModel<String>();
    
    private BotoiBorobila itxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Close"));
    
    private JFrame leihoHau;
    private JFrame gurasoa;
    private BLFacade facade;

    // Faboritoen izarrak behar dituen aldagaiak
    private EstrellitaButton faboritoenIzarra;
    private boolean faboritoaDa;

    // --- ERAIKITZAILEA ---

    /**
     * ShowYourSaleGUI klasearen eraikitzailea.
     * Produktu baten (salmentaren) xehetasunak kargatzen ditu eta ezabatzeko zein faborito bihurtzeko aukerak ematen ditu.
     * @param salmenta  Erakutsi nahi den erabiltzailearen salmenta objektua.
     * @param saltzailea Aplikazioa erabiltzen ari den erabiltzaile erregistratua.
     * @param gurasoa    Aurreko leihoa (nondik gatozen), atzera egitean erakusteko.
     */
    public ShowYourSaleGUI(Sale salmenta, Registered saltzailea, JFrame gurasoa) { 
        this.leihoHau = this; 
        this.gurasoa = gurasoa;
        
        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }
       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(new Dimension(800, 600));
        this.facade = MainGUI.getBusinessLogic();

        getContentPane().setLayout(null);
        
        // --- FABORITOA DA ALDAGAIAREN HASIERAKETA ---
        if(saltzailea != null) {
            faboritoaDa = facade.faboritoaDa(salmenta.getSaleNumber(), saltzailea.getEmail());
        } else {
            faboritoaDa = false;
        }
        
        faboritoenIzarra = new EstrellitaButton(faboritoaDa);  // Egoera zuzenera pasa
        faboritoenIzarra.setBounds(salmenta.getTitle().length() + 200, 21, 168, 30);
        faboritoenIzarra.setVisible(saltzailea != null);
        faboritoenIzarra.setOpaque(false);
        faboritoenIzarra.setContentAreaFilled(false);
        faboritoenIzarra.setBorderPainted(false);
        faboritoenIzarra.setFocusPainted(false);
        
        faboritoenIzarra.addActionListener(e -> {
            if(faboritoaDa) {
            	faboritoaDa = false;
            	facade.faboritoaEzabatu(salmenta.getSaleNumber(), saltzailea.getEmail());
            } else {
            	faboritoaDa = true;
            	facade.faboritoaGehitu(salmenta.getSaleNumber(), saltzailea.getEmail());
            }
            faboritoenIzarra.setFavorito(faboritoaDa);  // Izarraren ikuspegia eguneratu
        });
        getContentPane().add(faboritoenIzarra);

        izenburuTestuEremua.setBounds(37, 21, 370, 26);
        izenburuTestuEremua.setBorder(null);
        izenburuTestuEremua.setText(salmenta.getTitle());
        izenburuTestuEremua.setEditable(false);
        izenburuTestuEremua.setColumns(10);
        izenburuTestuEremua.setFont(new Font("Tahoma", Font.BOLD, 20));
        izenburuTestuEremua.setHorizontalAlignment(SwingConstants.LEFT);
        izenburuTestuEremua.setHighlighter(null);
        izenburuTestuEremua.setFocusable(false);
        getContentPane().add(izenburuTestuEremua);
        
        deskribapenIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 15));
        deskribapenIzenburuEtiketa.setBounds(37, 66, 109, 16);
        getContentPane().add(deskribapenIzenburuEtiketa);
        
        deskribapenTestuEremua.setFont(new Font("Tahoma", Font.PLAIN, 13));
        deskribapenTestuEremua.setBounds(37, 98, 701, 95);
        deskribapenTestuEremua.setText(salmenta.getDescription());
        deskribapenTestuEremua.setEditable(false);
        deskribapenTestuEremua.setBackground(Color.LIGHT_GRAY);
        deskribapenTestuEremua.setColumns(10);
        getContentPane().add(deskribapenTestuEremua);

        prezioTestuEremua.setBounds(636, 21, 102, 27);
        prezioTestuEremua.setFont(new Font("Arial", Font.BOLD, 19));
        prezioTestuEremua.setSelectedTextColor(new Color(0,128,128));
        prezioTestuEremua.setForeground(new Color(0,128,128));
        prezioTestuEremua.setBorder(null);
        prezioTestuEremua.setText(Float.toString(salmenta.getPrice()) + "€");
        prezioTestuEremua.setEditable(false);
        prezioTestuEremua.setHighlighter(null);
        prezioTestuEremua.setFocusable(false);
        getContentPane().add(prezioTestuEremua);
        
        egoeraIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 15));
        egoeraIzenburuEtiketa.setBounds(37, 234, 140, 25);
        getContentPane().add(egoeraIzenburuEtiketa);

        egoeraEtiketa.setBounds(200, 281, 289, 16);
        egoeraEtiketa.setText(new SimpleDateFormat("dd-MM-yyyy").format(salmenta.getPubDate().getTime()));
        egoeraEtiketa.setFont(new Font("Dialog", Font.PLAIN, 15));
        getContentPane().add(egoeraEtiketa);
        
        erroreEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 13));
        erroreEtiketa.setBounds(56, 261, 320, 20);
        erroreEtiketa.setForeground(Color.red);
        getContentPane().add(erroreEtiketa);

        gertaerenKorritzePanela.setBounds(new Rectangle(25, 44, 346, 116));

        irudiPanela = new JPanel();
        irudiPanela.setBounds(426, 261, 312, 230);
        getContentPane().add(irudiPanela);
        irudiPanela.add(mezuEtiketa);
        mezuEtiketa.setForeground(Color.red);
        
        itxiBotoia.setBounds(10, 496, 114, 30);
        itxiBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                leihoHau.setVisible(false);
                gurasoa.setVisible(true);
            }
        });
        getContentPane().add(itxiBotoia);

        // --- IRUDIA KARGATZEKO PROZESUA ---
        BLFacade logikaNegozioa = MainGUI.getBusinessLogic();
        String fitxategia = salmenta.getFileName();
        irudiPanela.removeAll();
        irudiPanela.setLayout(new BorderLayout(0, 0)); 

        if (fitxategia != null && !fitxategia.isEmpty()) {
           Image irudiaDeskargatuta = logikaNegozioa.downloadImage(fitxategia);
            if (irudiaDeskargatuta != null) {
                helburuIrudia = rescale((BufferedImage)irudiaDeskargatuta);
                irudiPanela.add(new JLabel(new ImageIcon(helburuIrudia)), BorderLayout.CENTER);
            }
        	
        } else {
            // Irudirik aurkitzen ez bada, defektuzkoa jarriko da
            mezuEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ImageError"));
            mezuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
            irudiPanela.add(mezuEtiketa, BorderLayout.NORTH);

            try {
                java.net.URL irudiURLa = getClass().getResource("/images/default.jpg");
                
                if (irudiURLa != null) {
                    ImageIcon ikonoLehenetsia = new ImageIcon(irudiURLa);
                    Image eskalatutakoLehenetsia = ikonoLehenetsia.getImage().getScaledInstance(OINARRI_TAMAINA, OINARRI_TAMAINA, Image.SCALE_SMOOTH);
                    irudiPanela.add(new JLabel(new ImageIcon(eskalatutakoLehenetsia)), BorderLayout.CENTER);
                } else {
                    System.err.println("ez da default.jpg argazkia aurkitu");
                    irudiPanela.setBackground(Color.GRAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
                irudiPanela.setBackground(Color.GRAY);
            }
        }
        
        irudiPanela.revalidate();
        irudiPanela.repaint();

        System.out.println("status: " + salmenta.getStatus());
        produktuEgoeraEremua = new JLabel(Utils.getStatus(salmenta.getStatus()));
        produktuEgoeraEremua.setFont(new Font("Tahoma", Font.PLAIN, 15));
        produktuEgoeraEremua.setBounds(190, 231, 138, 30);
        getContentPane().add(produktuEgoeraEremua);

        // --- PRODUKTUA KENDU (EZABATU) BOTOIA ---
        BotoiBorobila kenduBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("ShowYourSaleGUI.RemoveProductTitle"));
        kenduBotoia.setFont(new Font("Tahoma", Font.PLAIN, 16));
        kenduBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                // Datu-basetik salmenta kentzeko eskaera
                facade.removeSale(salmenta.getSaleNumber(), saltzailea.getEmail());
                
                JOptionPane.showMessageDialog(null, 
						ResourceBundle.getBundle("Etiquetas").getString("ShowYourSaleGUI.RemovedProduct"), 
						ResourceBundle.getBundle("Etiquetas").getString("ShowYourSaleGUI.RemoveProductTitle"), 
				        JOptionPane.INFORMATION_MESSAGE);
                
                // Guraso leihoa QuerySalesGUI motakoa bada, bilaketa exekutatu taula berritzeko
                if (gurasoa instanceof QuerySalesGUI) {
                    ((QuerySalesGUI) gurasoa).jButtonSearch.doClick(); 
                }
                if (gurasoa instanceof YourSalesGUI) {
                    ((YourSalesGUI) gurasoa).refreshTable();; 
                }

                leihoHau.setVisible(false);
                gurasoa.setVisible(true);
            }
        });
        kenduBotoia.setBounds(90, 353, 211, 81);
        getContentPane().add(kenduBotoia);

        JSeparator bereizlea = new JSeparator();
        bereizlea.setBounds(16, 56, 721, 16);
        getContentPane().add(bereizlea);
        
        kenduBotoia.setVisible(saltzailea != null);
       
        sortutakoDataIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 15));
        sortutakoDataIzenburuEtiketa.setBounds(37, 281, 200, 16);
        getContentPane().add(sortutakoDataIzenburuEtiketa);
    } 

    // --- METODOAK ---

    /**
     * Irudi bat eskalatzeko erabiltzen den metodo laguntzailea.
     * @param jatorrizkoIrudia Eskalatu nahi den irudia.
     * @return Eskalatutako irudi berria.
     */
    public BufferedImage rescale(BufferedImage jatorrizkoIrudia) {
        BufferedImage eskalatutakoIrudia = new BufferedImage(OINARRI_TAMAINA, OINARRI_TAMAINA, BufferedImage.TYPE_INT_RGB);
        Graphics2D grafikoak = eskalatutakoIrudia.createGraphics();
        grafikoak.drawImage(jatorrizkoIrudia, 0, 0, OINARRI_TAMAINA, OINARRI_TAMAINA, null);
        grafikoak.dispose();
        return eskalatutakoIrudia;
    }
}