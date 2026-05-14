package gui;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.*;
import gui.*;
import domain.*;
import configuration.UtilDate;

import extra.BotoiBorobila;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;


public class QueryUsersGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final JLabel jLabelProducts = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	public BotoiBorobila jButtonSearch = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 
	//public jarrita dago beste klase batetik deitu ahal izateko.
	//private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));

	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts= new JTable();

	private DefaultTableModel tableModelProducts;

	private JFrame thisFrame; 

	private String[] columnNamesProducts = new String[] {ResourceBundle.getBundle("Etiquetas").getString("QueryUsersAdminGUI.Izena"),
			ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Email")};
	private JTextField jTextFieldSearch;
	
	private MainGUIerregistratua parent;
	private JPanel contentPane;
	Registered erabiltzailea;


	public void Garbitu() {
		jTextFieldSearch.setText("");
	}

	public QueryUsersGUI(Registered erabiltzailea, MainGUIerregistratua parent) {
		this.parent=parent;
		this.erabiltzailea=erabiltzailea;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (parent != null) {
	        this.setLocation(parent.getLocation());
	    }
		
		tableProducts.setEnabled(false);
		thisFrame=this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(800, 600));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.ErabiltzaileakKontsultatu"));
		jLabelProducts.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelProducts);

		/*jButtonClose.setBounds(new Rectangle(220, 379, 130, 30));

		jButtonClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				thisFrame.setVisible(false);

			}
		});		*/
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//this.getContentPane().add(jButtonClose, null);

		scrollPanelProducts.setBounds(new Rectangle(52, 110, 691, 355));

		scrollPanelProducts.setViewportView(tableProducts);
		tableModelProducts = new DefaultTableModel(null, columnNamesProducts);

		tableProducts.setModel(tableModelProducts);
		tableProducts.setDefaultEditor(Object.class, null);
		tableProducts.editCellAt(-1, -1);
		tableProducts.setRowHeight(35);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 15));
		
		tableModelProducts.setDataVector(null, columnNamesProducts);
		tableModelProducts.setColumnCount(3); // another column added to allocate ride objects

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);


		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(2)); // not shown in JTable

		this.getContentPane().add(scrollPanelProducts, null);
		
		jTextFieldSearch = new JTextField();
		jTextFieldSearch.setBounds(78, 53, 478, 31);
		getContentPane().add(jTextFieldSearch);
		jTextFieldSearch.setColumns(10);
		 jButtonSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		 jButtonSearch.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent e) {
		 		try {
					tableModelProducts.setDataVector(null, columnNamesProducts);
					tableModelProducts.setColumnCount(3); // another column added to allocate product object

					BLFacade facade = MainGUI.getBusinessLogic();

					List<Registered> users=facade.getRegisteredUsersWithSales(jTextFieldSearch.getText(), erabiltzailea.getName());

					if (users.isEmpty() ) jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("QueryUsersAdminGUI.EzErabiltzaile"));
					else jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIAdmin.ErabiltzaileakKontsultatu"));
					for (Registered user:users){
						Vector<Object> row = new Vector<Object>();
						row.add(user.getName());
						row.add(user.getEmail());
						row.add(user); // product object added in order to obtain it with tableModelProducts.getValueAt(i,2)
						tableModelProducts.addRow(row);		
					}
				} catch (Exception e1) {

					e1.printStackTrace();
				}
				tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
				tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
				tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);

				tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(2)); // not shown in JTable
		 		
		 	}
		 });
		jButtonSearch.setBounds(591, 45, 126, 46);
		getContentPane().add(jButtonSearch);
		
		tableProducts.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		        
		        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        
		        Registered user = (Registered) tableModelProducts.getValueAt(row, 2);
		        
		        if (isSelected) {
		            c.setBackground(table.getSelectionBackground());
		        } else if (user != null && user.isBanned()) {
		            c.setBackground(new Color(255, 150, 150)); // Gorria
		        } else {
		            c.setBackground(Color.WHITE);
		        }
		        
		        return c;
		    }
		});
	    
		tableProducts.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            
		            if(mouseEvent.getClickCount() == 2)
		            {
				        JTable table =(JTable) mouseEvent.getSource();
		            	Point point = mouseEvent.getPoint();
				        int row = table.rowAtPoint(point);
		            	Registered s=(Registered) tableModelProducts.getValueAt(row, 2);
		            	QueryUsersGUI.this.setVisible(false);
		            	jTextFieldSearch.setText("");
			            new ShowRegisteredGUI(s, erabiltzailea, QueryUsersGUI.this);
		            }
		        }
		 });
		
		BotoiBorobila btnAtzera = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); //$NON-NLS-1$ //$NON-NLS-2$
		btnAtzera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QueryUsersGUI.this.setVisible(false);
					parent.setVisible(true);
				
			}
		});
		btnAtzera.setBounds(642, 486, 101, 37);
		contentPane.add(btnAtzera);
		
		
	}
	public void refreshTable() {
	    tableModelProducts.setDataVector(null, columnNamesProducts);
	    tableModelProducts.setColumnCount(3);

	    BLFacade facade = MainGUI.getBusinessLogic();
	    List<Registered> users=facade.getRegisteredUsersWithSales(jTextFieldSearch.getText(), erabiltzailea.getName());

	    for (Registered user : users) {
	        Vector<Object> row = new Vector<Object>();
	        row.add(user.getName());
	        row.add(user.getEmail());
	        row.add(user);
	        tableModelProducts.addRow(row);
	    }

	    tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
	    tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);
	    tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(2));
	}
}
