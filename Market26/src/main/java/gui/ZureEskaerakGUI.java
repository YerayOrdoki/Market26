package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.util.List;
import java.util.ResourceBundle;

import businessLogic.BLFacade;
import domain.*;
import extra.BotoiBorobila;

public class ZureEskaerakGUI extends JFrame {

    private Registered erabiltzailea;
    private JFrame gurasoa;
    private BLFacade facade;

    private JTable tableEskaerak;
    private JTable tableEskaintzak;

    private DefaultTableModel modelEskaerak;
    private DefaultTableModel modelEskaintzak;

    private List<Eskaera> nireEskaerak;

    /**
     * Erabiltzailearen eskaerak eta jasotako eskaintzak erakusten dituen leihoa sortzen du.
     *
     * @param erabiltzailea Uneko erabiltzaile erregistratua.
     * @param gurasoa Aurreko leihoa.
     */
    public ZureEskaerakGUI(Registered erabiltzailea, JFrame gurasoa) {
        this.erabiltzailea = erabiltzailea;
        this.gurasoa = gurasoa;
        this.facade = MainGUI.getBusinessLogic();

        this.setSize(800, 600);
        this.setLayout(null);

        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }

        JLabel lbl1 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.ZureEskaerak") + ":");
        lbl1.setBounds(50, 20, 300, 25);
        this.add(lbl1);

        modelEskaerak = new DefaultTableModel(
                new String[] {
                        "ID",
                        ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"),
                        ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Data")
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEskaerak = new JTable(modelEskaerak);
        JScrollPane sp1 = new JScrollPane(tableEskaerak);
        sp1.setBounds(50, 50, 700, 150);
        this.add(sp1);

        JLabel lbl2 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Eskaintzak") + ":");
        lbl2.setBounds(50, 220, 300, 25);
        this.add(lbl2);

        modelEskaintzak = new DefaultTableModel(
                new String[] {
                        "ID",
                        ResourceBundle.getBundle("Etiquetas").getString("ZureEskaerakGUI.Produktua"),
                        ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Price"),
                        ResourceBundle.getBundle("Etiquetas").getString("ErreklamazioakAdminGUI.Saltzailea")
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEskaintzak = new JTable(modelEskaintzak);
        JScrollPane sp2 = new JScrollPane(tableEskaintzak);
        sp2.setBounds(50, 250, 700, 180);
        this.add(sp2);

        // Eskaera bat aukeratzean, hari dagozkion eskaintzak beheko taulan kargatzen dira.
        tableEskaerak.getSelectionModel().addListSelectionListener(e -> {
            int row = tableEskaerak.getSelectedRow();
            if (row != -1) {
                kargatuEskaintzak(nireEskaerak.get(row));
            }
        });

        BotoiBorobila btnOnartu = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Accept"));
        btnOnartu.setBounds(250, 450, 300, 50);
        btnOnartu.addActionListener(e -> {
            int row = tableEskaintzak.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("Etiquetas").getString("ZureEskaerakGUI.AukeratuEskaintzaBat"));
                return;
            }

            Integer saleId = (Integer) modelEskaintzak.getValueAt(row, 0);

            // Metodo honek erosketa osoa kudeatzen du: ordainketa, jakinarazpena eta eskaeraren ezabaketa.
            boolean ondo = facade.produktoaErosi(saleId, erabiltzailea.getEmail(), true);
            if (ondo) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.BuyMessage"));
                this.setVisible(false);
                gurasoa.setVisible(true);
            }
        });
        this.add(btnOnartu);

        kargatuEskaerak();

        BotoiBorobila btnAtzera = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
        btnAtzera.setBounds(50, 510, 100, 30);
        btnAtzera.addActionListener(ev -> {
            this.setVisible(false);
            gurasoa.setVisible(true);
        });
        this.add(btnAtzera);
    }

    /**
     * Erabiltzailearen eskaeren zerrenda kargatzen du goiko taulan.
     */
    private void kargatuEskaerak() {
        modelEskaerak.setRowCount(0);
        nireEskaerak = facade.getZureEskaerak(erabiltzailea.getEmail());

        for (Eskaera esk : nireEskaerak) {
            modelEskaerak.addRow(new Object[] {
                    esk.getEskaeraNumber(),
                    esk.getTitle(),
                    esk.getPubDate()
            });
        }
    }

    /**
     * Aukeratutako eskaerari lotutako eskaintzak kargatzen ditu beheko taulan.
     *
     * @param esk Aukeratutako eskaera.
     */
    private void kargatuEskaintzak(Eskaera esk) {
        modelEskaintzak.setRowCount(0);

        for (Sale s : esk.getEskaintzak()) {
            modelEskaintzak.addRow(new Object[] {
                    s.getSaleNumber(),
                    s.getTitle(),
                    s.getPrice(),
                    s.getSeller().getName()
            });
        }
    }
}