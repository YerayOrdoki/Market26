package gui;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
import configuration.UtilDate;

import extra.BotoiBorobila;
import extra.EstrellitaButton;

public class ShowRegisteredAdminGUI extends JFrame {

    File targetFile;
    BufferedImage targetImg;
    private static final int baseSize = 320;
    private static final String basePath="src/main/resources/images/";

    private static final long serialVersionUID = 1L;

    private JScrollPane scrollPaneEvents = new JScrollPane();
    DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
    private JLabel jLabelError = new JLabel();
    private JLabel statusField=new JLabel();
    private JFrame thisFrame;
    private JFrame parent;
    private BLFacade facade;
    
    private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts= new JTable();
	private DefaultTableModel tableModelProducts;
	private String[] columnNamesProducts = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"),

	};
	private Registered user;



    // Izarrak behar dituen bariableak


    public ShowRegisteredAdminGUI(Registered user, User administratzailea, JFrame parent) { 
        thisFrame=this; 
        this.parent=parent;
        
        if (parent != null) {
            this.setLocation(parent.getLocation());
        }
      
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(new Dimension(800, 600));
        this.facade = MainGUI.getBusinessLogic();
        this.user = facade.getSellerByEmail(user.getEmail());

        getContentPane().setLayout(null);
     // faboritoaDa hasieraketa
    

        JSeparator separator = new JSeparator();
        separator.setBounds(16, 56, 721, 16);
        getContentPane().add(separator);
        
        BotoiBorobila btnAtzera = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); //$NON-NLS-1$ //$NON-NLS-2$
        btnAtzera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowRegisteredAdminGUI.this.setVisible(false);
				parent.setVisible(true);
            	
			}
		});
		btnAtzera.setBounds(677, 507, 101, 37);
		getContentPane().add(btnAtzera);
        
        
        BotoiBorobila btnUserDelete = new BotoiBorobila(""); //$NON-NLS-1$ //$NON-NLS-2$
        if(user.isBanned()) {
        	btnUserDelete.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.Unban"));
        }else {
        	btnUserDelete.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.BanAccount"));

        }
        btnUserDelete.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnUserDelete.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        	       if(user.isBanned()) {
        	    	   facade.kontuaDesbaneatu(user.getEmail());
                       JOptionPane.showMessageDialog(null,
                           ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.AccountUnbannedMsg"),
                           ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.UnbanAccountTitle"),
                           JOptionPane.INFORMATION_MESSAGE);
                       System.out.println("KONTUA DESBANEATU DA!!!");
        	        }else {
        	        	facade.kontuaBaneatu(user.getEmail());
                        JOptionPane.showMessageDialog(null,
                            ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.AccountBannedMsg"),
                            ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.BanAccountTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("KONTUA BANEATU DA!!!");

        	        }
        	       ShowRegisteredAdminGUI.this.setVisible(false);
        	       ((QueryUsersAdminGUI) parent).refreshTable(); // <- freskatu
        	       parent.setVisible(true);
        	}
        });
        btnUserDelete.setBounds(164, 490, 277, 64);
        btnUserDelete.setColourRED();
        btnUserDelete.setVisible(administratzailea instanceof Admin);
        getContentPane().add(btnUserDelete);
        
        scrollPanelProducts.setBounds(new Rectangle(16, 110, 721, 355));

		scrollPanelProducts.setViewportView(tableProducts);
		tableModelProducts = new DefaultTableModel(null, columnNamesProducts);

		tableProducts.setModel(tableModelProducts);
		tableProducts.setRowHeight(35);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 15));
		
		tableModelProducts.setDataVector(null, columnNamesProducts);
		tableModelProducts.setColumnCount(4); // another column added to allocate ride objects

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);


		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3)); // not shown in JTable

		this.getContentPane().add(scrollPanelProducts, null);
		
		JLabel lblNewLabel = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.Name"));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(16, 22, 60, 23);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(user.getName());
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(73, 22, 209, 23);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.Email"));
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_2.setBounds(292, 22, 60, 23);
		getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_1_1 = new JLabel(user.getEmail());
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(349, 22, 429, 23);
		getContentPane().add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_3 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowRegisteredGUI.ProductList"));
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_3.setBounds(16, 79, 219, 23);
		getContentPane().add(lblNewLabel_3);
		
		try {
			tableModelProducts.setDataVector(null, columnNamesProducts);
			tableModelProducts.setColumnCount(4); // another column added to allocate product object

			BLFacade facade = MainGUI.getBusinessLogic();
			Date today = UtilDate.trim(new Date());

			List<Sale> sales=user.getSales();

			for (Sale sale:sales){
				Vector<Object> row = new Vector<Object>();
				row.add(sale.getTitle());
				row.add(sale.getPrice());
				row.add(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPubDate().getTime()));
				row.add(sale); // product object added in order to obtain it with tableModelProducts.getValueAt(i,2)
				tableModelProducts.addRow(row);		
			}
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);

		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3)); // not shown in JTable
		
		tableProducts.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mousePressed(MouseEvent mouseEvent) {
	            
	            if(mouseEvent.getClickCount() == 2)
	            {
			        JTable table =(JTable) mouseEvent.getSource();
	            	Point point = mouseEvent.getPoint();
			        int row = table.rowAtPoint(point);
	            	Sale s=(Sale) tableModelProducts.getValueAt(row, 3);
	            	ShowRegisteredAdminGUI.this.setVisible(false);
		            new ShowSaleGUI(s, administratzailea, ShowRegisteredAdminGUI.this, null);
	            }
	        }
	 });
        
    } 

    public BufferedImage rescale(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
    
    public void refreshTable() {
        try {
            tableModelProducts.setDataVector(null, columnNamesProducts);
            tableModelProducts.setColumnCount(4);

            Registered updatedUser = facade.getSellerByEmail(user.getEmail());
            List<Sale> sales = updatedUser.getSales();

            for (Sale sale : sales) {
                Vector<Object> row = new Vector<Object>();
                row.add(sale.getTitle());
                row.add(sale.getPrice());
                row.add(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPubDate().getTime()));
                row.add(sale);
                tableModelProducts.addRow(row);
            }

            tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
            tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
            tableProducts.getColumnModel().getColumn(2).setPreferredWidth(70);

            if (tableProducts.getColumnModel().getColumnCount() > 3) {
                tableProducts.getColumnModel().removeColumn(
                    tableProducts.getColumnModel().getColumn(3)
                );
            }

            tableModelProducts.fireTableDataChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}