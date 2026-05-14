
package gui;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.ResourceBundle;

import businessLogic.BLFacade;
import domain.Eskaera;
import domain.Registered;
import extra.BotoiBorobila;

/**
 * Produktu eskaera berriak sortzeko leihoa.
 */
public class CreateEskaeraGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private Registered erabiltzailea;
    private JFrame gurasoa;
    private BLFacade facade;

    private JTextField txtTitle;
    private JTextArea txtDesc;

    public CreateEskaeraGUI(Registered erabiltzailea, JFrame gurasoa) {
        this.erabiltzailea = erabiltzailea;
        this.gurasoa = gurasoa;
        this.facade = MainGUI.getBusinessLogic();

        // Tamaina estandarra ezarri (800x600)
        this.setSize(800, 600);
        this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.EskaeraSortu"));
        this.setLayout(null);
        if (gurasoa != null) this.setLocation(gurasoa.getLocation());

        // Titulua
        JLabel lblTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title") + ":");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTitle.setBounds(100, 60, 150, 25);
        this.add(lblTitle);

        txtTitle = new JTextField();
        txtTitle.setBounds(100, 90, 600, 35);
        this.add(txtTitle);

        // Deskribapena
        JLabel lblDesc = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Deskribapena") + ":");
        lblDesc.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblDesc.setBounds(100, 150, 150, 25);
        this.add(lblDesc);

        txtDesc = new JTextArea();
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBounds(100, 180, 600, 200);
        this.add(scrollDesc);

        // Sortu botoia
        BotoiBorobila btnSortu = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("EskaeraGUI.Sortu"));
        btnSortu.setBounds(250, 420, 300, 50);
        btnSortu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = txtTitle.getText();
                String desc = txtDesc.getText();

                if (title.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ErrorQuery"));
                    return;
                }

                Eskaera esk = facade.eskaeraSortu(title, desc, new Date(), erabiltzailea.getEmail());
                
                if (esk != null) {
                	JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("CreateEskaeraGUI.EskaeraOndoSortu"));
                    CreateEskaeraGUI.this.setVisible(false);
                    gurasoa.setVisible(true);
                } else {
                	JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("EskaeraSortuErrorea"));
                }
            }
        });
        this.add(btnSortu);

        // Atzera botoia
        BotoiBorobila btnAtzera = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.btnAtzera"));
        btnAtzera.setBounds(50, 500, 120, 40);
        btnAtzera.addActionListener(e -> {
            this.setVisible(false);
            gurasoa.setVisible(true);
        });
        this.add(btnAtzera);
    }
}  