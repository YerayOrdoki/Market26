package gui;

import javax.swing.*;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import extra.BotoiBorobila;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Erabiltzailearen dirua kudeatzeko (sartu edo atera) interfaze grafikoa.
 */
public class DiruaKudeatuGUI extends JFrame {
	
    private static final long serialVersionUID = 1L;

    // --- ATRIBUTUAK ---
    private String saltzaileEmaila;
    private BLFacade facade;

    private JPanel edukiPanela;
    private JPanel panela;
    private JPanel panela4;
    private JPanel panela3;
    private JPanel panela2;
    private final ButtonGroup botoiTaldea = new ButtonGroup();
    
    private JLabel profilIzenburuEtiketa;
    private JTextField kantitateTestuEremua;
    private JTextField txartelTestuEremua;

    
    private JFrame gurasoa;
    // --- ERAIKITZAILEA ---

    /**
     * DiruaKudeatuGUI klasearen eraikitzailea.
     * Leihoaren ezarpenak konfiguratzen ditu eta elementu grafikoak sortzen ditu.
     * * @param erabiltzailea Saioa hasi duen erabiltzailea.
     * @param gurasoa       Aurreko leihoa (nondik gatozen), atzera egitean berriro erakusteko.
     */
    public DiruaKudeatuGUI(Registered erabiltzailea, JFrame gurasoa) {
        this.gurasoa=gurasoa;

        this.saltzaileEmaila = erabiltzailea.getEmail();
        
        System.out.println("Ez zaude erregistratuta");
            
        this.setSize(800, 600);
        
        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }
        
        this.facade = MainGUI.getBusinessLogic();
    
        panela = new JPanel();
        panela.setLayout(null);
        
        edukiPanela = new JPanel();
        edukiPanela.setLayout(new GridLayout(4, 1, 0, 0));
        
        panela4 = new JPanel();
        panela4.setEnabled(true);
        edukiPanela.add(panela4);
        panela4.setLayout(null);
        
        panela3 = new JPanel();
        panela3.setLayout(null);
        
        panela2 = new JPanel();
        panela2.setLayout(null);
        edukiPanela.add(panela4);
        
        profilIzenburuEtiketa = new JLabel((ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.DiruKudeaketa")));
        profilIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
        profilIzenburuEtiketa.setForeground(Color.BLACK);
        profilIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
        profilIzenburuEtiketa.setBounds(109, 54, 546, 55);
        panela4.add(profilIzenburuEtiketa);
        
        JLabel saldoEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldo") + " : " + facade.getSellerByEmail(erabiltzailea.getEmail()).getSaldoa() + "€");
        saldoEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 13));
        saldoEtiketa.setBounds(316, 97, 141, 33);
        panela4.add(saldoEtiketa);
        edukiPanela.add(panela3);
        
        JLabel datuakIzenburuEtiketa = new JLabel((ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Datuak"))); 
        datuakIzenburuEtiketa.setBounds(113, -13, 546, 55);
        panela3.add(datuakIzenburuEtiketa);
        datuakIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
        datuakIzenburuEtiketa.setForeground(Color.BLACK);
        datuakIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
        
        kantitateTestuEremua = new JTextField();
        kantitateTestuEremua.setFont(new Font("Tahoma", Font.PLAIN, 13));
        kantitateTestuEremua.setToolTipText((ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Kantitatea")));
        kantitateTestuEremua.setBounds(230, 67, 116, 36);
        panela3.add(kantitateTestuEremua);
        kantitateTestuEremua.setColumns(10);
        
        txartelTestuEremua = new JTextField();
        txartelTestuEremua.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txartelTestuEremua.setToolTipText(ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.TxartelZbk"));
        txartelTestuEremua.setBounds(427, 66, 116, 38);
        panela3.add(txartelTestuEremua);
        txartelTestuEremua.setColumns(10);
        
        JLabel kantitateEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Kantitatea"));
        kantitateEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 13));
        kantitateEtiketa.setBounds(230, 45, 187, 12);
        panela3.add(kantitateEtiketa);
        
        JLabel txartelEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Txartela16Digito"));
        txartelEtiketa.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txartelEtiketa.setBounds(427, 44, 116, 12);
        panela3.add(txartelEtiketa);
        edukiPanela.add(panela2);
        
        JLabel ekintzaIzenburuEtiketa = new JLabel((ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Ekintza")));
        ekintzaIzenburuEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
        ekintzaIzenburuEtiketa.setForeground(Color.BLACK);
        ekintzaIzenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
        ekintzaIzenburuEtiketa.setBounds(118, -10, 546, 55);
        panela2.add(ekintzaIzenburuEtiketa);
        
        JCheckBox diruaAteraLaukia = new JCheckBox(ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.DiruaAtera"));
        diruaAteraLaukia.setFont(new Font("Tahoma", Font.PLAIN, 14));
        diruaAteraLaukia.setBounds(234, 65, 113, 34);
        botoiTaldea.add(diruaAteraLaukia);
        panela2.add(diruaAteraLaukia);
        
        JCheckBox diruaSartuLaukia = new JCheckBox((ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.DiruaSartu")));
        diruaSartuLaukia.setFont(new Font("Tahoma", Font.PLAIN, 14));
        diruaSartuLaukia.setBounds(438, 65, 113, 34);
        botoiTaldea.add(diruaSartuLaukia);
        panela2.add(diruaSartuLaukia);
        
        edukiPanela.add(panela);
        
        BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); 
        atzeraBotoia.setBounds(38, 55, 101, 37);
        panela.add(atzeraBotoia);
        atzeraBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DiruaKudeatuGUI.this.setVisible(false);
                gurasoa.setVisible(true);
                
                // Aukerak garbitzeko
                botoiTaldea.clearSelection();
                kantitateTestuEremua.setText(""); 
                txartelTestuEremua.setText("");
                
                // Diruaren labela automatikoki eguneratzeko (PerfilaGUI-ko lblSaldoa erabiltzen du)
                ((PerfilaGUI) gurasoa).lblSaldoa.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldo") + " : " + facade.getSellerByEmail(erabiltzailea.getEmail()).getSaldoa() + "€");
            }
        });
        
        BotoiBorobila jarraituBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.DiruaSartuAtera"));
        jarraituBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean botoiakAukeratuta = botoiTaldea.getSelection() != null;
                String kantitateaStr = kantitateTestuEremua.getText().trim();
                String txartelaStr = txartelTestuEremua.getText().trim();
                
                // Txartelaren formatua garbitzen dugu hutsuneak eta gidoiak kenduz
                String txartelGarbia = txartelaStr.replace(" ", "").replace("-", "");
                
                // Formatu egokia daukala ziurtatzen dugu (kantitatea zenbakia izatea eta txartelak 16 digitu izatea)
                if (kantitateaStr.matches("\\d+([.,]\\d+)?") && txartelGarbia.matches("\\d{16}") && botoiakAukeratuta) {
                    // Komak puntuetara aldatzen ditugu double bezala parseatu ahal izateko
                    String kantitateFormatua = kantitateaStr.replace(",", ".");
                    double kantitateZenbakia = Double.parseDouble(kantitateFormatua);
                    boolean ondoEginDa = true;
                    
                    // Erabiltzaileak dirua atera ala sartu nahi duen egiaztatzen dugu
                    if (diruaAteraLaukia.isSelected()) {
                        ondoEginDa = facade.diruaAtera(kantitateZenbakia, erabiltzailea.getEmail());
                    } else {
                        facade.diruaSartu(kantitateZenbakia, erabiltzailea.getEmail());
                    }
                    
                    if (ondoEginDa) {
                        JOptionPane.showMessageDialog(null, 
                                ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Ondo"), 
                                ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.OndoTitle"), 
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, 
                                ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Error2"), 
                                ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Error2Title"), 
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    saldoEtiketa.setText(ResourceBundle.getBundle("Etiquetas").getString("PerfilaGUI.Saldo") + " : " + facade.getSellerByEmail(erabiltzailea.getEmail()).getSaldoa() + "€");
                    botoiTaldea.clearSelection();
                    kantitateTestuEremua.setText(""); 
                    txartelTestuEremua.setText("");
                } else {
                       JOptionPane.showMessageDialog(null, 
                                ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Error1"), 
                                ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Error1Title"), 
                                JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        jarraituBotoia.setBounds(307, 10, 185, 81);
        panela.add(jarraituBotoia);
        jarraituBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("DiruaKudeatuGUI.Continue"));
        
        setContentPane(edukiPanela);
        setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + saltzaileEmaila);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
    }

}