package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import extra.BotoiBorobila;

public class NotifikazioaBidaliGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel edukiPanela;
    private JFrame gurasoa;
    private BLFacade facade;

    private JTextArea mezuArea;
    private JComboBox<String> erabiltzaileCombo;
    private JCheckBox deneiCheckbox;

    public NotifikazioaBidaliGUI(Admin administratzailea, JFrame gurasoa) {
        this.gurasoa = gurasoa;
        this.facade = MainGUI.getBusinessLogic();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setTitle(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Title"));

        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }

        edukiPanela = new JPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(null);

        JLabel izenburuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Header"));
        izenburuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 18));
        izenburuEtiketa.setBounds(50, 30, 300, 30);
        edukiPanela.add(izenburuEtiketa);

        // --- HARTZAILEA AUKERATU ---
        JLabel hartzaileEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Recipient"));
        hartzaileEtiketa.setFont(new Font("Tahoma", Font.BOLD, 14));
        hartzaileEtiketa.setBounds(50, 80, 100, 30);
        edukiPanela.add(hartzaileEtiketa);

        erabiltzaileCombo = new JComboBox<String>();
        erabiltzaileCombo.setBounds(150, 80, 350, 30);
        kargatuErabiltzaileak(); // Datu-basetik erabiltzaileak kargatu
        edukiPanela.add(erabiltzaileCombo);

        deneiCheckbox = new JCheckBox(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.SendToAll"));
        deneiCheckbox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        deneiCheckbox.setBounds(520, 80, 200, 30);
        deneiCheckbox.addActionListener(e -> {
            // Checkbox-a markatzean, desplegablea blokeatu egiten da
            erabiltzaileCombo.setEnabled(!deneiCheckbox.isSelected());
        });
        edukiPanela.add(deneiCheckbox);

        // --- MEZUA IDATZI ---
        JLabel mezuEtiketa = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Message"));
        mezuEtiketa.setFont(new Font("Tahoma", Font.BOLD, 14));
        mezuEtiketa.setBounds(50, 140, 100, 30);
        edukiPanela.add(mezuEtiketa);

        mezuArea = new JTextArea();
        mezuArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mezuArea.setLineWrap(true);
        mezuArea.setWrapStyleWord(true);
        
        JScrollPane korritzePanela = new JScrollPane(mezuArea);
        korritzePanela.setBounds(50, 180, 680, 220);
        edukiPanela.add(korritzePanela);

        // --- BOTOIAK ---
        BotoiBorobila bidaliBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Send"));
        bidaliBotoia.setColourBLUE();
        bidaliBotoia.setBounds(300, 440, 200, 50);
        bidaliBotoia.addActionListener(e -> bidaliNotifikazioa());
        edukiPanela.add(bidaliBotoia);

        BotoiBorobila atzeraBotoia = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Back"));
        atzeraBotoia.setBounds(630, 500, 100, 30);
        atzeraBotoia.addActionListener(e -> {
            this.setVisible(false);
            if (gurasoa != null) gurasoa.setVisible(true);
        });
        edukiPanela.add(atzeraBotoia);
    }

    // --- METODOAK ---

    /**
     * Datu-basetik sistemako erabiltzaile guztiak eskuratu eta ComboBox-ean sartzen ditu.
     */
    private void kargatuErabiltzaileak() {
        List<Registered> erabiltzaileak = facade.getRegisteredUsers("");
        for (Registered user : erabiltzaileak) {
            // Posta elektronikoa erakutsiko dugu, identifikadore bakarra delako
            erabiltzaileCombo.addItem(user.getEmail());
        }
    }

    /**
     * Notifikazioa bidaltzeko logika. Erabiltzaile bati ala denei bidaltzea kudeatzen du.
     */
    private void bidaliNotifikazioa() {
        String mezua = mezuArea.getText().trim();
        
        if (mezua.isEmpty()) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.ErrorEmpty"), ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (deneiCheckbox.isSelected()) {
            // GUZTIEI BIDALI
            List<Registered> erabiltzaileak = facade.getRegisteredUsers("");
            for (Registered user : erabiltzaileak) {
                facade.notifikazioaBidali(user.getEmail(), mezua);
            }
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.SuccessAll"));
            
        } else {
            // BAKAR BATI BIDALI
            if (erabiltzaileCombo.getSelectedItem() != null) {
                String aukeratutakoEmaila = erabiltzaileCombo.getSelectedItem().toString();
                facade.notifikazioaBidali(aukeratutakoEmaila, mezua);
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.SuccessSingle1") + aukeratutakoEmaila + ResourceBundle.getBundle("Etiquetas").getString("NotifikazioaBidaliGUI.SuccessSingle2"));
            }
        }
        
        // Garbitu testua hurrengo mezurako
        mezuArea.setText("");
    }
}