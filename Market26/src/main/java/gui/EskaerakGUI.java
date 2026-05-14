package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import businessLogic.BLFacade;
import domain.Eskaera;
import domain.Registered;
import domain.Sale;
import extra.BotoiBorobila;

/**
 * Eskaeren tabloia kudeatzeko leihoa.
 */
public class EskaerakGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private Registered erabiltzailea;
    private JFrame gurasoa;
    private BLFacade facade;

    private JTable tableEskaerak;
    private DefaultTableModel tableModel;
    private List<Eskaera> eskaeraList;

    /**
     * EskaerakGUI leihoaren eraikitzailea.
     * Erabiltzaileari dauden eskaerak ikusi eta eskaintzak egiteko aukera ematen dio.
     *
     * @param erabiltzailea Aplikazioa erabiltzen ari den erabiltzailea.
     * @param gurasoa Aurreko leihoa.
     */
    public EskaerakGUI(Registered erabiltzailea, JFrame gurasoa) {
        this.erabiltzailea = erabiltzailea;
        this.gurasoa = gurasoa;
        this.facade = MainGUI.getBusinessLogic();

        this.setSize(800, 600);
        this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Title"));
        this.setLayout(null);

        if (gurasoa != null) {
            this.setLocation(gurasoa.getLocation());
        }

        JLabel lblIzenburua = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Title"));
        lblIzenburua.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblIzenburua.setHorizontalAlignment(SwingConstants.CENTER);
        lblIzenburua.setBounds(150, 20, 500, 40);
        this.add(lblIzenburua);

        String[] zutabeIzenak = {
                ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"),
                ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Eskatzailea"),
                ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Data"),
                ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaintzaKop")
        };

        tableModel = new DefaultTableModel(zutabeIzenak, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEskaerak = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableEskaerak);
        scrollPane.setBounds(50, 80, 700, 320);
        this.add(scrollPane);

        datuakKargatu();

        BotoiBorobila btnEskaini = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaintzaEgin"));
        btnEskaini.setBounds(250, 430, 300, 50);
        btnEskaini.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int errenkada = tableEskaerak.getSelectedRow();

                if (errenkada < 0) {
                    JOptionPane.showMessageDialog(null,
                            ResourceBundle.getBundle("Etiquetas").getString("EskaerakGUI.AukeratuEskaeraBat"));
                    return;
                }

                Eskaera aukeratutakoEskaera = eskaeraList.get(errenkada);

                if (aukeratutakoEskaera.getEskatzailea().getEmail().equals(erabiltzailea.getEmail())) {
                    JOptionPane.showMessageDialog(null,
                            ResourceBundle.getBundle("Etiquetas").getString("EskaerakGUI.EzinDaEskaeraEgin"));
                    return;
                }

                eskaintzaEgin(aukeratutakoEskaera);
            }
        });
        this.add(btnEskaini);

        BotoiBorobila btnAtzera = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
        btnAtzera.setBounds(50, 500, 120, 40);
        btnAtzera.addActionListener(e -> {
            this.setVisible(false);
            gurasoa.setVisible(true);
        });
        this.add(btnAtzera);
    }

    /**
     * Taulako datuak datu-iturritik berriz kargatzen ditu.
     * Uneko eskaeren zerrenda hartu, eta taulan erakusten du.
     */
    private void datuakKargatu() {
        tableModel.setRowCount(0);
        eskaeraList = facade.getEskaerak();

        for (Eskaera eskaera : eskaeraList) {
            Object[] errenkada = {
                    eskaera.getTitle(),
                    eskaera.getEskatzailea().getName(),
                    eskaera.getPubDate().toString(),
                    eskaera.getEskaintzak().size()
            };
            tableModel.addRow(errenkada);
        }
    }

    /**
     * Aukeratutako eskaera bati eskaintza egiteko elkarrizketa-prozesua kudeatzen du.
     * Erabiltzailearen salgai dauden produktuen artean bat aukeratzeko aukera ematen du.
     *
     * @param eskaera Eskaintza jasoko duen eskaera.
     */
    private void eskaintzaEgin(Eskaera eskaera) {
        Date etorkizunekoData = new Date(System.currentTimeMillis() + 31536000000L);
        List<Sale> nireSalgaiak = facade.getSalesBySeller(erabiltzailea.getEmail(), "", etorkizunekoData);

        if (nireSalgaiak.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Etiquetas").getString("EskaerakGUI.EzProduktuSalgai"));
            return;
        }

        JComboBox<Sale> combo = new JComboBox<>();
        for (Sale salmenta : nireSalgaiak) {
            combo.addItem(salmenta);
        }

        Object[] mezua = {
                ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.ZureProduktuak"),
                combo
        };

        int aukera = JOptionPane.showConfirmDialog(
                this,
                mezua,
                ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaintzaEgin"),
                JOptionPane.OK_CANCEL_OPTION
        );

        if (aukera == JOptionPane.OK_OPTION) {
            Sale aukeratutakoSalmenta = (Sale) combo.getSelectedItem();

            boolean ondo = facade.eskaintzaGehitu(
                    eskaera.getEskaeraNumber(),
                    aukeratutakoSalmenta.getSaleNumber(),
                    erabiltzailea.getEmail()
            );

            if (ondo) {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaintzaBidali"));
                datuakKargatu();
            } else {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaintzaErrorea"));
            }
        }
    }
}