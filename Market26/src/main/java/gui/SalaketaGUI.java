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
import configuration.UtilDate;

import exceptions.*;

import extra.BotoiBorobila;


public class SalaketaGUI extends JFrame {
    private BLFacade facade;

    File targetFile;
    BufferedImage targetImg;
    String encodedfile = null;
    private static final int baseSize = 250;
	private static final String basePath="src/main/resources/images/";

	
	private static final long serialVersionUID = 1L;

	private String sellerMail;
	private Calendar calendarAct = null;
	private Calendar calendarAnt = null;

	private JScrollPane scrollPaneEvents = new JScrollPane();
	DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
	List<String> status;


	private BotoiBorobila btnSalaketaGauzatu = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Reportar"));
	//private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JLabel jLabelMsg = new JLabel();
	private JLabel jLabelError = new JLabel();
	private JFrame thisFrame;
	
	private ShowSaleGUI parent;
	private JPanel contentPane;
	private JComboBox comboBox;

	public SalaketaGUI(Sale sale, User saltzaile, ShowSaleGUI parent) {
		this.parent = parent;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
        setSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		
		if (parent != null) {
	        this.setLocation(parent.getLocation());
	    }
		 this.facade = MainGUI.getBusinessLogic();
		
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Reportar"));

		
		scrollPaneEvents.setBounds(new Rectangle(25, 44, 346, 116));
		btnSalaketaGauzatu.setFont(new Font("Lucida Grande", Font.BOLD, 15));

		btnSalaketaGauzatu.setBounds(new Rectangle(234, 334, 258, 65));
		btnSalaketaGauzatu.setColourRED();
		btnSalaketaGauzatu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String mota = comboBox.getSelectedItem().toString();
					Date today = UtilDate.trim(new Date());
					
					facade.salaketaSortu(mota,sale.getSaleNumber(), today, (Registered) saltzaile);
					JOptionPane.showMessageDialog(null, 
							ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.SalaketaBidali"), 
							ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Salaketa"), 
					        JOptionPane.INFORMATION_MESSAGE);
					
					SalaketaGUI.this.setVisible(false);
				} catch (MustBeLaterThanTodayException | ErreklamazioaAlreadyExistException  e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SalaketaGUI.this.parent.desactivarBotonReclamar();
			}
		});
		/*jButtonClose.setBounds(new Rectangle(328, 228, 101, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);			}
		});*/
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		jLabelMsg.setBounds(new Rectangle(16, 379, 377, 20));
		jLabelMsg.setForeground(Color.red);

		jLabelError.setBounds(new Rectangle(9, 364, 384, 20));
		jLabelError.setForeground(Color.red);
		
	    status=Utils.getStatus();
		for(String s:status) statusOptions.addElement(s);

		this.getContentPane().add(jLabelMsg, null);
		this.getContentPane().add(jLabelError, null);

		//this.getContentPane().add(jButtonClose, null);
		this.getContentPane().add(btnSalaketaGauzatu, null);
		
		BotoiBorobila btnAtzera = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); //$NON-NLS-1$ //$NON-NLS-2$
		btnAtzera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SalaketaGUI.this.setVisible(false);
				//parent.setVisible(true);
				
			}
		});
		btnAtzera.setBounds(598, 468, 92, 29);
		contentPane.add(btnAtzera);
		
		JLabel lblNewLabel = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Reportar")); //$NON-NLS-1$ //$NON-NLS-2$
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(164, 66, 389, 29);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel);
		
		comboBox = new JComboBox();
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBox.setBounds(234, 168, 258, 53);
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota1"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota2"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota3"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota4"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota5"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota6"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota7"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota8"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota9"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("SalaketaGUI.Mota10"));
		contentPane.add(comboBox);
		
	}	 
}
