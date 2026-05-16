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
 * Erositako produktu baten informazio zehatza erakusten duen interfaze grafikoa.
 * Era berean, erreklamazioak egiteko edo ikusteko aukera kudeatzen du.
 */
public class ErositakoaShowSaleGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private static final int OINARRI_TAMAINA = 320;
    private static final String OINARRI_BIDEA = "src/main/resources/images/";

    // Panela publikoa da jatorrizko kodean
    public JPanel irudiPanela;

    private File helburuFitxategia;
    private BufferedImage helburuIrudia;
    private File aukeratutakoFitxategia;
    private String irudia;

    private JTextField izenburuTestuEremua = new JTextField();
    private JTextField deskribapenTestuEremua = new JTextField();
    private JTextField prezioTestuEremua = new JTextField();
    
    private JLabel deskribapenIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description")); 
    private JLabel produktuEgoeraIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
    private JLabel egoeraEtiketa = new JLabel(); 
    private JLabel mezuEtiketa = new JLabel();
    private JLabel erroreEtiketa = new JLabel();
    private JLabel egoeraEremua = new JLabel();
    
    private final JLabel erosketaDataEtiketa = new JLabel(""); //$NON-NLS-1$ //$NON-NLS-2$
    private final JLabel argitalpenDataIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"));
    private final JLabel erosketaDataIzenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ErositakoakGUI.BoughtDate"));

    private JScrollPane gertaeraKorritzePanela = new JScrollPane();
    DefaultComboBoxModel<String> egoeraAukerak = new DefaultComboBoxModel<String>();
    
    private BotoiBorobila itxiBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Close"));
    private BotoiBorobila erreklamatuBotoia;

    private JFrame leihoHau;
    private JFrame gurasoa;
    private BLFacade facade;
    
    private boolean faboritoaDa;
    private boolean erreklamatuta;


    // --- ERAIKITZAILEA ---

    /**
     * ErositakoaShowSaleGUI klasearen eraikitzailea.
     * Produktuaren xehetasunak (irudia, testuak, datak) kargatzen ditu eta erreklamazioen
     * egoeraren arabera dagokion botoia (erreklamatu / ikusi) erakusten du.
     * * @param salmenta   Aukeratutako erositako produktuaren objektua.
     * @param saltzailea Leihoa ikusten ari den erabiltzailea (kasu honetan eroslea izan ohi da, edo saltzailea bera).
     * @param gurasoa    Aurreko leihoa, atzera egiterakoan erakusteko.
     */
    public ErositakoaShowSaleGUI(BoughtSale salmenta, Registered saltzailea, JFrame gurasoa) { 
        this.leihoHau = this; 
        this.gurasoa = gurasoa;
        
        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }
       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(new Dimension(800, 600));
        this.facade = MainGUI.getBusinessLogic();
        SaleSellerBoughtContainer unekoSale=facade.getSaleSellerBoughtContainer(salmenta.getId() );


        getContentPane().setLayout(null);

        izenburuTestuEremua.setBounds(37, 21, 370, 26);
        izenburuTestuEremua.setBorder(null);
        izenburuTestuEremua.setText(unekoSale.getSale().getTitle());
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
        deskribapenTestuEremua.setText(unekoSale.getSale().getDescription());
        deskribapenTestuEremua.setEditable(false);
        deskribapenTestuEremua.setBackground(Color.LIGHT_GRAY);
        deskribapenTestuEremua.setColumns(10);
        getContentPane().add(deskribapenTestuEremua);

        prezioTestuEremua.setBounds(621, 21, 100, 27);
        prezioTestuEremua.setFont(new Font("Arial", Font.BOLD, 19));
        prezioTestuEremua.setSelectedTextColor(new Color(0,128,128));
        prezioTestuEremua.setForeground(new Color(0,128,128));
        prezioTestuEremua.setBorder(null);
        prezioTestuEremua.setText(Float.toString(unekoSale.getSale().getPrice()) + "€");
        prezioTestuEremua.setEditable(false);
        getContentPane().add(prezioTestuEremua);
        
        produktuEgoeraIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 15));
        produktuEgoeraIzenburuEtiketa.setBounds(37, 234, 140, 25);
        getContentPane().add(produktuEgoeraIzenburuEtiketa);

        egoeraEtiketa.setBounds(200, 281, 289, 16);
        egoeraEtiketa.setText(new SimpleDateFormat("dd-MM-yyyy").format(unekoSale.getSale().getPubDate().getTime()));
        egoeraEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        getContentPane().add(egoeraEtiketa);
        
        erroreEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 13));
        erroreEtiketa.setBounds(56, 261, 320, 20);
        erroreEtiketa.setForeground(Color.red);
        getContentPane().add(erroreEtiketa);

        gertaeraKorritzePanela.setBounds(new Rectangle(25, 44, 346, 116));

        irudiPanela = new JPanel();
        irudiPanela.setBounds(426, 261, 312, 230);
        getContentPane().add(irudiPanela);
        irudiPanela.add(mezuEtiketa);
        mezuEtiketa.setForeground(Color.red);

        itxiBotoia.setBounds(20, 507, 114, 30);
        itxiBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                leihoHau.setVisible(false);
                gurasoa.setVisible(true);
            }
        });
        getContentPane().add(itxiBotoia);

        // --- IRUDIA KARGATZEKO PROZESUA ---
        BLFacade logikaNegozioa = MainGUI.getBusinessLogic();
        String fitxategia = unekoSale.getSale().getFileName();
        irudiPanela.removeAll();
        irudiPanela.setLayout(new BorderLayout(0, 0)); 

        // Irudia badago, deskargatu eta erakutsi egiten dugu
        if (fitxategia != null && !fitxategia.isEmpty()) {
            Image irudiaDeskargatuta = logikaNegozioa.downloadImage(fitxategia);
            if (irudiaDeskargatuta != null) {
                helburuIrudia = rescale((BufferedImage) irudiaDeskargatuta);
                irudiPanela.add(new JLabel(new ImageIcon(helburuIrudia)), BorderLayout.CENTER);
            }
        	 java.net.URL irudiURLa = getClass().getResource("/images/default.jpg");
             
             
        } else {
            // Irudirik ezean, errore mezua eta lehenetsitako argazkia kargatzen ditugu
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

        System.out.println("status: " + unekoSale.getSale().getStatus());
        egoeraEremua = new JLabel(Utils.getStatus(unekoSale.getSale().getStatus()));
        egoeraEremua.setFont(new Font("Tahoma", Font.PLAIN, 15));
        egoeraEremua.setBounds(116, 231, 138, 30);
        getContentPane().add(egoeraEremua);

        JSeparator bereizlea = new JSeparator();
        bereizlea.setBounds(10, 58, 721, 16);
        getContentPane().add(bereizlea);
        
        erosketaDataEtiketa.setBounds(200, 322, 109, 14);
        erosketaDataEtiketa.setText(new SimpleDateFormat("dd-MM-yyyy").format(unekoSale.getBought() .getErosketaData().getTime()));
        erosketaDataEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        getContentPane().add(erosketaDataEtiketa);
        
        argitalpenDataIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 15));
        argitalpenDataIzenburuEtiketa.setBounds(37, 281, 200, 16);
        getContentPane().add(argitalpenDataIzenburuEtiketa);
        
        erosketaDataIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 15));
        erosketaDataIzenburuEtiketa.setBounds(37, 317, 323, 25);
        getContentPane().add(erosketaDataIzenburuEtiketa);
        
        // --- ERREKLAMAZIOEN KUDEAKETA LOGIKA ---
        System.out.println(saltzailea);
        User u=facade.getSellerByEmail(saltzailea.getEmail());
        // Leihoa ikusten ari den erabiltzailea produktuaren jatorrizko saltzailea ote den egiaztatzen dugu
        boolean saltzaileaDa = u.getEmail().equals(unekoSale.getUser().getEmail());
        
        System.out.println(saltzaileaDa);
        // Datu-basean produktuari lotutako erreklamazio bat badagoen bilatzen dugu
        Erreklamazioa erreklamazioAktiboa = facade.lortuErreklamazioa(u.getEmail(), unekoSale.getSale().getTitle());

        if (erreklamazioAktiboa == null) {
            // ERREKLAMAZIORIK EZ BADA EXISTITZEN
            if (!saltzaileaDa) {
                // Eroslea bada, erreklamazioa sortzeko botoi gorria erakusten diogu
                erreklamatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("ErreklamatuGUI.Erreklamatu"));
                erreklamatuBotoia.setColourRED();
                erreklamatuBotoia.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new ErreklamatuGUI(salmenta, saltzailea, ErositakoaShowSaleGUI.this).setVisible(true);
                    }
                });
                erreklamatuBotoia.setBounds(93, 391, 186, 62);
                getContentPane().add(erreklamatuBotoia);
            } else {
                // Saltzailea bada, ondo dagoela adierazten duen testu soil bat erakusten diogu
                JLabel erreklamazioGabeEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ErositakoaShowSaleGUI.EzDagoErreklamaziorik"));
                erreklamazioGabeEtiketa.setFont(new Font("Tahoma", Font.ITALIC, 14));
                erreklamazioGabeEtiketa.setForeground(Color.GRAY);
                erreklamazioGabeEtiketa.setBounds(93, 391, 300, 62);
                getContentPane().add(erreklamazioGabeEtiketa);
            }

        } else {
            // ERREKLAMAZIOA EXISTITZEN BADA (Berdin dio saltzailea ala eroslea zaren)
            // Erreklamazioaren haria (txata) ikusteko botoi urdina erakusten dugu
            erreklamatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("ErositakoaShowSaleGUI.IkusiErreklamazioa"));
            erreklamatuBotoia.setColourBLUE(); 
            erreklamatuBotoia.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Abriendo hilo de mensajes...");
                    new ErreklamazioHariaGUI(erreklamazioAktiboa, saltzailea, ErositakoaShowSaleGUI.this).setVisible(true);
                    leihoHau.setVisible(false);
                }
            });
            erreklamatuBotoia.setBounds(93, 391, 186, 62);
            getContentPane().add(erreklamatuBotoia);
        }
    } 
    
    // --- METODOAK ---
   
    /**
     * Irudi bat eskalatzeko erabiltzen den metodo laguntzailea.
     * Tamaina finko batean birmoldatzen du irudia.
     * * @param jatorrizkoIrudia Eskalatu nahi den jatorrizko irudia.
     * @return Eskalatutako irudi berria.
     */
    public BufferedImage rescale(BufferedImage jatorrizkoIrudia) {
        BufferedImage eskalatutakoIrudia = new BufferedImage(OINARRI_TAMAINA, OINARRI_TAMAINA, BufferedImage.TYPE_INT_RGB);
        Graphics2D grafikoak = eskalatutakoIrudia.createGraphics();
        grafikoak.drawImage(jatorrizkoIrudia, 0, 0, OINARRI_TAMAINA, OINARRI_TAMAINA, null);
        grafikoak.dispose();
        return eskalatutakoIrudia;
    }
    
    /**
     * Erreklamatzeko botoia desgaitzeko (grisez jartzeko) metodoa.
     * Beste GUI batzuetatik dei daiteke prozesu bat amaitzean.
     */
    public void desactivarBotonReclamar() {
        if (erreklamatuBotoia != null) {
            erreklamatuBotoia.setEnabled(false);
            erreklamatuBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("ErreklamatuGUI.Erreklamatu")); // Aukerakoa: erabiltzaileak zergatik dagoen grisez jakin dezan
        }
    }
    
    /**
     * Erreklamatzeko / Ikusteko botoia erabat ezkutatzen du,
     * leihoa "Irakurtzeko soilik" moduan uzteko.
     */
    public void ezkutatuErreklamazioBotoia() {
        if (erreklamatuBotoia != null) {
            erreklamatuBotoia.setVisible(false);
        }
    }
}