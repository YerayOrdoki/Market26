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

public class ShowSaleGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int baseSize = 320;
    private static final String basePath = "src/main/resources/images/";

    File targetFile;
    BufferedImage targetImg;
    public JPanel panel_1;

    private JTextField fieldTitle = new JTextField();
    private JTextField fieldDescription = new JTextField();
    private JTextField fieldPrice = new JTextField();
    private JTextField textField;

    JLabel labelStatus = new JLabel();
    private JLabel jLabelDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description"));
    private JLabel jLabelProductStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
    private JLabel jLabelMsg = new JLabel();
    private JLabel jLabelError = new JLabel();
    private JLabel statusField = new JLabel();
    private final JLabel sortutakoDataLabel = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"));

    private File selectedFile;
    private String irudia;

    private JScrollPane scrollPaneEvents = new JScrollPane();
    DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
    private BotoiBorobila jButtonClose = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("Close"));

    private JFrame thisFrame;
    private JFrame parent;
    private BLFacade facade;

    Salaketa salaketa;
    BotoiBorobila btnSalatu;

    boolean salatutaDago = true;
    boolean erreserbatuta = false;

    // Izarraren egoerarekin lotutako atributuak
    private EstrellitaButton faboritoak;
    private boolean isFavorito;
    private boolean saskianDago;

    /**
     * Produktu baten xehetasunak erakusten dituen leihoa sortzen du.
     *
     * @param sale Erakutsi nahi den produktua.
     * @param saltzaile Uneko erabiltzailea.
     * @param parent Aurreko leihoa.
     * @param salaketa Lotutako salaketa, badagoenean.
     */
    public ShowSaleGUI(Sale sale, User saltzaile, JFrame parent, Salaketa salaketa) {
        this.salaketa = salaketa;
        thisFrame = this;
        this.parent = parent;

        if (parent != null) {
            this.setLocation(parent.getLocation());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(new Dimension(800, 600));
        this.facade = MainGUI.getBusinessLogic();

        System.out.println("guian null dago" + facade);
        System.out.println(saltzaile);

        SaleSellerBoughtContainer unekoSale = facade.getSaleSellerBoughtContainer(sale.getSaleNumber());

        getContentPane().setLayout(null);

        if (saltzaile instanceof Registered) {
            salatutaDago = facade.salatutaDago(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
        }

        if (saltzaile instanceof Registered) {
            isFavorito = facade.faboritoaDa(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
        } else {
            isFavorito = false;
        }

        faboritoak = new EstrellitaButton(isFavorito);
        faboritoak.setBounds(unekoSale.getSale().getTitle().length() + 200, 21, 168, 30);
        faboritoak.setVisible(saltzaile != null && !(saltzaile instanceof Admin));
        faboritoak.setOpaque(false);
        faboritoak.setContentAreaFilled(false);
        faboritoak.setBorderPainted(false);
        faboritoak.setFocusPainted(false);

        faboritoak.addActionListener(e -> {
            if (isFavorito) {
                isFavorito = false;
                facade.faboritoaEzabatu(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
            } else {
                isFavorito = true;
                facade.faboritoaGehitu(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
            }
            faboritoak.setFavorito(isFavorito);
        });
        getContentPane().add(faboritoak);

        fieldTitle.setBounds(37, 21, 370, 26);
        fieldTitle.setBorder(null);
        fieldTitle.setText(unekoSale.getSale().getTitle());
        fieldTitle.setEditable(false);
        fieldTitle.setColumns(10);
        fieldTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        fieldTitle.setHorizontalAlignment(SwingConstants.LEFT);
        fieldTitle.setEditable(false);
        fieldTitle.setHighlighter(null);
        fieldTitle.setFocusable(false);
        getContentPane().add(fieldTitle);

        jLabelDescription.setFont(new Font("Tahoma", Font.BOLD, 15));
        jLabelDescription.setBounds(37, 66, 109, 16);
        getContentPane().add(jLabelDescription);

        fieldDescription.setFont(new Font("Tahoma", Font.PLAIN, 13));
        fieldDescription.setBounds(37, 98, 701, 76);
        fieldDescription.setText(unekoSale.getSale().getDescription());
        fieldDescription.setEditable(false);
        fieldDescription.setBackground(Color.LIGHT_GRAY);
        fieldDescription.setColumns(10);
        getContentPane().add(fieldDescription);

        fieldPrice.setBounds(636, 185, 102, 27);
        fieldPrice.setFont(new Font("Arial", Font.BOLD, 19));
        fieldPrice.setSelectedTextColor(new Color(0, 128, 128));
        fieldPrice.setForeground(new Color(0, 128, 128));
        fieldPrice.setBorder(null);
        fieldPrice.setText(Float.toString(unekoSale.getSale().getPrice()) + "€");
        fieldPrice.setEditable(false);
        fieldPrice.setHighlighter(null);
        fieldPrice.setFocusable(false);
        getContentPane().add(fieldPrice);

        jLabelProductStatus.setFont(new Font("Tahoma", Font.BOLD, 15));
        jLabelProductStatus.setBounds(37, 207, 140, 25);
        getContentPane().add(jLabelProductStatus);

        labelStatus.setBounds(200, 254, 289, 16);
        labelStatus.setText(new SimpleDateFormat("dd-MM-yyyy").format(unekoSale.getSale().getPubDate().getTime()));
        labelStatus.setFont(new Font("Dialog", Font.PLAIN, 15));
        getContentPane().add(labelStatus);

        jLabelError.setFont(new Font("Tahoma", Font.PLAIN, 13));
        jLabelError.setBounds(56, 261, 320, 20);
        jLabelError.setForeground(Color.red);
        getContentPane().add(jLabelError);

        scrollPaneEvents.setBounds(new Rectangle(25, 44, 346, 116));

        panel_1 = new JPanel();
        panel_1.setBounds(426, 261, 312, 230);
        getContentPane().add(panel_1);
        panel_1.add(jLabelMsg);
        jLabelMsg.setForeground(Color.red);

        jButtonClose.setBounds(16, 512, 114, 30);
        jButtonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (parent instanceof ErreserbatuakGUI) {
                    ((ErreserbatuakGUI) parent).refreshTable();
                }
                if (parent instanceof FaboritoGUI) {
                    ((FaboritoGUI) parent).refreshTable();
                }
                thisFrame.setVisible(false);
                parent.setVisible(true);
            }
        });
        getContentPane().add(jButtonClose);

        BLFacade facadeLocal = MainGUI.getBusinessLogic();
        String file = unekoSale.getSale().getFileName();
        panel_1.removeAll();
        panel_1.setLayout(new BorderLayout(0, 0));

        if (file != null && !file.isEmpty()) {
            Image img = facadeLocal.downloadImage(file);
            if (img != null) {
                targetImg = rescale((BufferedImage) img);
                panel_1.add(new JLabel(new ImageIcon(targetImg)), BorderLayout.CENTER);
            }
        } else {
            jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ImageError"));
            jLabelMsg.setHorizontalAlignment(SwingConstants.CENTER);
            panel_1.add(jLabelMsg, BorderLayout.NORTH);

            try {
                java.net.URL imgURL = getClass().getResource("/images/default.jpg");

                if (imgURL != null) {
                    ImageIcon defaultIcon = new ImageIcon(imgURL);
                    Image scaledDefault = defaultIcon.getImage().getScaledInstance(baseSize, baseSize, Image.SCALE_SMOOTH);
                    panel_1.add(new JLabel(new ImageIcon(scaledDefault)), BorderLayout.CENTER);
                } else {
                    System.err.println("ez da default.jpg argazkia aurkitu");
                    panel_1.setBackground(Color.GRAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
                panel_1.setBackground(Color.GRAY);
            }
        }

        panel_1.revalidate();
        panel_1.repaint();

        System.out.println("status: " + unekoSale.getSale().getStatus());
        statusField = new JLabel(Utils.getStatus(unekoSale.getSale().getStatus()));
        statusField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        statusField.setBounds(190, 204, 138, 30);
        getContentPane().add(statusField);

        JSeparator separator = new JSeparator();
        separator.setBounds(16, 56, 721, 16);
        getContentPane().add(separator);

        sortutakoDataLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        sortutakoDataLabel.setBounds(37, 254, 200, 16);
        getContentPane().add(sortutakoDataLabel);

        btnSalatu = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Reportar"));
        btnSalatu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!unekoSale.getUser().getEmail().equals(saltzaile.getEmail())) {
                    new SalaketaGUI(sale, saltzaile, ShowSaleGUI.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ProductIsYoursReport"),
                            ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ReportProductTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnSalatu.setBounds(636, 512, 114, 30);
        btnSalatu.setVisible(saltzaile instanceof Registered);
        btnSalatu.setEnabled(salatutaDago);
        if (!salatutaDago) {
            btnSalatu.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Reported"));
        }
        btnSalatu.setColourRED();
        getContentPane().add(btnSalatu);

        BotoiBorobila btnSaleDelete = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.DeleteProduct"));
        btnSaleDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (salaketa != null) {
                    facade.salaketaEgoeraAldatu(salaketa.getKexaNumber(), 2);
                }
                facade.produktoaEzabatu(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
                JOptionPane.showMessageDialog(
                        null,
                        ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ProductDeleted"),
                        ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.DeleteProductTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
                ShowSaleGUI.this.setVisible(false);
                if (parent instanceof ShowRegisteredAdminGUI) {
                    ((ShowRegisteredAdminGUI) parent).refreshTable();
                }
                if (parent instanceof SalaketakAdminGUI) {
                    ((SalaketakAdminGUI) parent).kargatuDatuak();
                }
                parent.setVisible(true);
            }
        });
        btnSaleDelete.setBounds(157, 407, 179, 42);
        btnSaleDelete.setColourRED();
        btnSaleDelete.setVisible(saltzaile instanceof Admin);
        getContentPane().add(btnSaleDelete);

        BotoiBorobila salaketaEzeztatu = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.CancelReport"));
        salaketaEzeztatu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                facade.salaketaEgoeraAldatu(salaketa.getKexaNumber(), 1);
                ShowSaleGUI.this.setVisible(false);
                if (parent instanceof SalaketakAdminGUI) {
                    ((SalaketakAdminGUI) parent).kargatuDatuak();
                }
                parent.setVisible(true);
            }
        });
        salaketaEzeztatu.setVisible(saltzaile instanceof Admin && salaketa != null);
        salaketaEzeztatu.setBounds(157, 472, 179, 42);
        getContentPane().add(salaketaEzeztatu);

        BotoiBorobila erosiBotoia = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Buy"));
        erosiBotoia.setFont(new Font("Tahoma", Font.PLAIN, 16));

        boolean zureaDA = (saltzaile != null && unekoSale.getUser().getEmail().equals(saltzaile.getEmail()));
        erosiBotoia.setEnabled(!zureaDA);
        if (zureaDA) {
            erosiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.BuyError"));
        }

        erosiBotoia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!zureaDA) {
                    if (!facade.produktoaErosi(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail(), true)) {
                        JOptionPane.showMessageDialog(
                                null,
                                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.NotBoughtMessage"),
                                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Buy"),
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.BuyMessage"),
                                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.BuyTitle"),
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    if (parent instanceof QuerySalesGUI) {
                        ((QuerySalesGUI) parent).jButtonSearch.doClick();
                    }
                    if (parent instanceof SaskiaGUI) {
                        ((SaskiaGUI) parent).refreshTable();
                    }
                    if (parent instanceof ShowRegisteredGUI) {
                        ((ShowRegisteredGUI) parent).refreshTable();
                    }
                    if (parent instanceof FaboritoGUI) {
                        ((FaboritoGUI) parent).refreshTable();
                    }
                    if (parent instanceof ErreserbatuakGUI) {
                        ((ErreserbatuakGUI) parent).refreshTable();
                    }

                    ShowSaleGUI.this.setVisible(false);
                    parent.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.BuyError"),
                            ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.BuyErrorTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        erosiBotoia.setBounds(90, 353, 265, 81);
        erosiBotoia.setVisible(saltzaile instanceof Registered);
        getContentPane().add(erosiBotoia);

        Registered r = facade.getSellerByEmail(saltzaile.getEmail());
        VIP vip = r.getVip();

        BotoiBorobila btnErreserbatu = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Erreserbatu"));

        if (saltzaile != null && saltzaile instanceof Registered && vip != null
                && facade.erreserbatutaDago(unekoSale.getSale().getSaleNumber(), vip.getId())) {
            erreserbatuta = true;
            btnErreserbatu.setColourRED();
        }

        btnErreserbatu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (saltzaile == null) {
                    return;
                }

                if (vip.daukaErreserba(sale)) {
                    facade.saleDesErreserbatu(sale.getSaleNumber(), vip.getId());
                    btnErreserbatu.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Erreserbatu"));
                    btnErreserbatu.setColourMOREA();
                    erreserbatuta = false;
                } else {
                    facade.saleErreserbatu(sale.getSaleNumber(), vip.getId());
                    btnErreserbatu.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Erreserbatu2"));
                    btnErreserbatu.setColourRED();
                    erreserbatuta = true;
                }
            }
        });

        if (erreserbatuta) {
            btnErreserbatu.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Erreserbatu2"));
            btnErreserbatu.setColourRED();
        } else {
            btnErreserbatu.setColourMOREA();
        }

        btnErreserbatu.setBounds(90, 305, 265, 37);
        getContentPane().add(btnErreserbatu);

        System.out.println(saltzaile);

        Registered r2 = (saltzaile instanceof Registered) ? facade.getSellerByEmail(saltzaile.getEmail()) : null;
        VIP vip2 = (r2 != null) ? r2.getVip() : null;

        btnErreserbatu.setVisible(
                saltzaile != null && vip2 != null && !(unekoSale.getUser().getEmail().equals(saltzaile.getEmail())));

        System.out.println("ERRESERBATUTA? " + unekoSale.getSale().isErreserbatuta());

        boolean unekoVipErreserbatua = false;
        boolean besteVipBatekErreserbatuta = false;

        if (vip2 != null) {
            unekoVipErreserbatua = vip2.getErreserbak().stream()
                    .anyMatch(s -> s.getSaleNumber().equals(unekoSale.getSale().getSaleNumber()));
            besteVipBatekErreserbatuta = unekoSale.getSale().isErreserbatuta() && !unekoVipErreserbatua;
        }

        if (vip2 != null) {
            System.out.println("isErreserbatuta: " + unekoSale.getSale().isErreserbatuta());
            System.out.println("yoLaTengoReservada: " + unekoVipErreserbatua);
            System.out.println("reservadaPorOtro: " + besteVipBatekErreserbatuta);
            System.out.println("erreserbak size: " + vip2.getErreserbak().size());
        }

        btnErreserbatu.setEnabled(!besteVipBatekErreserbatuta);

        if (besteVipBatekErreserbatuta || (vip2 == null && unekoSale.getSale().isErreserbatuta())) {
            btnErreserbatu.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ErreserbatutaDago"));
            erosiBotoia.setEnabled(false);
            erosiBotoia.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ErreserbatutaDago"));
        }

        erosiBotoia.setVisible(saltzaile != null && !(saltzaile instanceof Admin));

        BotoiBorobila btnSaskia = new BotoiBorobila(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.AddSaskian"));

        if (saltzaile instanceof Registered) {
            saskianDago = facade.saskianDago(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
        } else {
            saskianDago = false;
        }

        if (saskianDago) {
            btnSaskia.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.RemoveSaskitik"));
            btnSaskia.setColourRED();
        }

        btnSaskia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (saskianDago) {
                    saskianDago = false;
                    facade.saskitikEzabatu(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
                    btnSaskia.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.AddSaskian"));
                    btnSaskia.setColourBLUE();
                } else {
                    saskianDago = true;
                    facade.saskianGehitu(unekoSale.getSale().getSaleNumber(), saltzaile.getEmail());
                    btnSaskia.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.RemoveSaskitik"));
                    btnSaskia.setColourRED();
                }

                if (parent instanceof SaskiaGUI) {
                    ((SaskiaGUI) parent).refreshTable();
                }
            }
        });
        btnSaskia.setBounds(90, 445, 267, 46);
        getContentPane().add(btnSaskia);

        textField = new JTextField();
        System.out.println("proba"
                + facade.getSellerByEmail(saltzaile.getEmail()).getKokapena()
                        .bidalketaGastuakKalkulatu(
                                facade.getSellerByEmail(unekoSale.getSale().getSeller().getEmail()).getKokapena())
                + "€");

        textField.setText(
                facade.getSellerByEmail(saltzaile.getEmail()).getKokapena()
                        .bidalketaGastuakKalkulatu(
                                facade.getSellerByEmail(unekoSale.getSale().getSeller().getEmail()).getKokapena())
                        + "€");
        textField.setSelectedTextColor(new Color(0, 128, 128));
        textField.setForeground(new Color(0, 128, 128));
        textField.setFont(new Font("Dialog", Font.BOLD, 19));
        textField.setFocusable(false);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setBounds(636, 223, 102, 27);
        getContentPane().add(textField);

        JLabel jLabelProductStatus_1 = new JLabel(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Bidalketa"));
        jLabelProductStatus_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        jLabelProductStatus_1.setBounds(458, 225, 168, 25);
        getContentPane().add(jLabelProductStatus_1);

        JLabel jLabelProductStatus_1_1 = new JLabel(
                ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.ProductoPrice"));
        jLabelProductStatus_1_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        jLabelProductStatus_1_1.setBounds(458, 187, 168, 25);
        getContentPane().add(jLabelProductStatus_1_1);

        btnSaskia.setVisible(
                (saltzaile != null && (saltzaile instanceof Registered) && (parent instanceof ShowRegisteredGUI))
                        || (parent instanceof SaskiaGUI));
    }

    /**
     * Irudi bat tamaina estandarrera berreskalatzen du.
     *
     * @param originalImage Jatorrizko irudia.
     * @return Berreskalatutako irudia.
     */
    public BufferedImage rescale(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * Salatzeko botoia desgaitzen du eta dagokion testua eguneratzen du.
     */
    public void desactivarBotonReclamar() {
        if (btnSalatu != null) {
            btnSalatu.setEnabled(false);
            btnSalatu.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Reported"));
        }
    }
}