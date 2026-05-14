package gui;

import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.toedter.calendar.JCalendar;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import exceptions.*;
import configuration.UtilDate;

import extra.BotoiBorobila;

/**
 * Erabiltzaile batek erositako produktu baten inguruko erreklamazio bat sortzeko interfaze grafikoa.
 */
public class ErreklamatuGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private BLFacade facade;

    // Jatorrizko kodean agertzen diren irudi eta fitxategi aldagaiak (erabiltzen ez badira ere, egitura mantentzeko uzten dira)
    File helburuFitxategia;
    BufferedImage helburuIrudia;
    String kodetutakoFitxategia = null;
    private static final int OINARRI_TAMAINA = 250;
    private static final String OINARRI_BIDEA = "src/main/resources/images/";

    private String saltzailePosta;
    private JTextField izenburuTestuEremua = new JTextField();
    private JTextField deskribapenTestuEremua = new JTextField();
    
    private JLabel izenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"));
    private JLabel deskribapenEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description")); 
    private Calendar unekoEgutegia = null;
    private Calendar aurrekoEgutegia = null;

    private JScrollPane gertaeraKorritzePanela = new JScrollPane();
    DefaultComboBoxModel<String> egoeraAukerak = new DefaultComboBoxModel<String>();
    List<String> egoerak;

    private BotoiBorobila erreklamatuBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("ErreklamatuGUI.Erreklamatu"));
    private JLabel mezuEtiketa = new JLabel();
    private JLabel erroreEtiketa = new JLabel();
    private JFrame leihoHau;
    
    private ErositakoaShowSaleGUI gurasoa;
    private JPanel edukiPanela;

    // --- ERAIKITZAILEA ---

    /**
     * ErreklamatuGUI klasearen eraikitzailea.
     * Erreklamazio berri bat sortzeko inprimakia prestatzen du.
     * * @param salmenta   Erreklamatu nahi den produktuaren salmenta objektua.
     * @param saltzailea Erreklamazioa jartzen duen erabiltzailea (eroslea bera).
     * @param gurasoa    Aurreko leihoa (ErositakoaShowSaleGUI), erreklamatu ostean eguneratzeko.
     */
    public ErreklamatuGUI(BoughtSale salmenta, Registered saltzailea, ErositakoaShowSaleGUI gurasoa) {
        this.gurasoa = gurasoa;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(661, 403));
        this.setLocationRelativeTo(null);
        
        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }
        this.facade = MainGUI.getBusinessLogic();
        
        this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.CreateProduct"));

        izenburuEtiketa.setBounds(new Rectangle(16, 24, 92, 20));

        gertaeraKorritzePanela.setBounds(new Rectangle(25, 44, 346, 116));
        erreklamatuBotoia.setFont(new Font("Lucida Grande", Font.BOLD, 15));

        erreklamatuBotoia.setBounds(new Rectangle(74, 252, 258, 65));
        erreklamatuBotoia.setColourRED();
        
        // --- ERREKLAMAZIOA SORTZEKO LOGIKA ---
        erreklamatuBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                	Date today = UtilDate.trim(new Date());
					
			        SaleSellerBoughtContainer unekoSale=facade.getSaleSellerBoughtContainer(salmenta.getId() );

                    // Datu-basean erreklamazioa sortzen dugu jatorrizko saltzaileari zuzenduta
                	facade.erreklamazioaSortu(
                            izenburuTestuEremua.getText(), 
                            deskribapenTestuEremua.getText(),
                            saltzailea.getEmail(), 
                            unekoSale.getUser().getEmail(), 
                            today,
                            salmenta
                        );                    
                    // Erabiltzaileari mezu bat erakusten diogu dena ondo joan dela adierazteko
                    JOptionPane.showMessageDialog(null, 
                    		ResourceBundle.getBundle("Etiquetas").getString("ErreklamatuGUI.ErreklamazioaBidali"), 
                    		ResourceBundle.getBundle("Etiquetas").getString("ErreklamatuGUI.Erreklamazioa"),
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Guraso-leihoa existitzen bada, erreklamatzeko botoia desgaitzen dugu (grisez jartzeko)
                    if (ErreklamatuGUI.this.gurasoa != null) {
                        ErreklamatuGUI.this.gurasoa.desactivarBotonReclamar();
                    }
                    
                    ErreklamatuGUI.this.setVisible(false);
                } catch (MustBeLaterThanTodayException | ErreklamazioaAlreadyExistException  e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        edukiPanela = new JPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(null);

        mezuEtiketa.setBounds(new Rectangle(16, 379, 377, 20));
        mezuEtiketa.setForeground(Color.red);

        erroreEtiketa.setBounds(new Rectangle(9, 364, 384, 20));
        erroreEtiketa.setForeground(Color.red);
        
        egoerak = Utils.getStatus();
        for(String s : egoerak) {
            egoeraAukerak.addElement(s);
        }

        this.getContentPane().add(mezuEtiketa, null);
        this.getContentPane().add(erroreEtiketa, null);
        this.getContentPane().add(erreklamatuBotoia, null);
        this.getContentPane().add(izenburuEtiketa, null);
        
        deskribapenEtiketa.setBounds(16, 56, 109, 16);
        getContentPane().add(deskribapenEtiketa);
        
        izenburuTestuEremua.setBounds(114, 22, 258, 26);
        izenburuTestuEremua.setText(salmenta.getS().getTitle());
        izenburuTestuEremua.setEditable(false);
        getContentPane().add(izenburuTestuEremua);
        izenburuTestuEremua.setColumns(10);
        
        deskribapenTestuEremua.setBounds(114, 51, 350, 149);
        getContentPane().add(deskribapenTestuEremua);
        deskribapenTestuEremua.setColumns(10);
        
        BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
        atzeraBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ErreklamatuGUI.this.setVisible(false);
                //parent.setVisible(true); // Jatorrizko kodean iruzkinduta, ez da ukitu
            }
        });
        atzeraBotoia.setBounds(547, 328, 92, 29);
        edukiPanela.add(atzeraBotoia);
    }    
}