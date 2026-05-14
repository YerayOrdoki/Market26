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
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


public class QuerySalesGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final JLabel jLabelProducts = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	public BotoiBorobila jButtonSearch = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 
	//public jarrita dago beste klase batetik deitu ahal izateko.
	//private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));

	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts= new JTable();

	private DefaultTableModel tableModelProducts;

	private JFrame thisFrame; 

	private String[] columnNamesProducts = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"),

	};
	private JTextField jTextFieldSearch;
	
	private MainGUI parent;
	private MainGUIerregistratua parent2;
	private JPanel contentPane;

	public void Garbitu() {
		jTextFieldSearch.setText("");
	}

	public QuerySalesGUI(Registered saltzaile, MainGUI parent, MainGUIerregistratua parent2) {
		this.parent=parent;
		this.parent2=parent2;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (parent != null) {
	        this.setLocation(parent.getLocation());
	    } else if(parent2 != null) {
	    	this.setLocation(parent2.getLocation());
	    }
		
		tableProducts.setEnabled(false);
		thisFrame=this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(800, 600));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.FindProducts"));
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
		tableProducts.setRowHeight(35);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 15));
		
		tableModelProducts.setDataVector(null, columnNamesProducts);
		tableModelProducts.setColumnCount(4); // another column added to allocate ride objects

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);


		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3)); // not shown in JTable

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
					tableModelProducts.setColumnCount(4); // another column added to allocate product object

					BLFacade facade = MainGUI.getBusinessLogic();
					System.out.println("guian null dago :"+facade);
					Date today = UtilDate.trim(new Date());
					

					List<Sale> sales=facade.getPublishedSales(jTextFieldSearch.getText(),today);

					if (sales.isEmpty() ) jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					else jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					for (Sale sale:sales){
						Registered uneko=(Registered) sale.getSeller();;
						
							if (uneko != null &&!uneko.isBanned()) {
								Vector<Object> row = new Vector<Object>();
								row.add(sale.getTitle());
								row.add(sale.getPrice());
								row.add(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPubDate().getTime()));
								row.add(sale); // product object added in order to obtain it with
												// tableModelProducts.getValueAt(i,2)
								tableModelProducts.addRow(row);
							}
					}
				} catch (Exception e1) {

					e1.printStackTrace();
				}
				tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
				tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
				tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);

				tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3)); // not shown in JTable
		 		
		 	}
		 });
		jButtonSearch.setBounds(591, 45, 126, 46);
		getContentPane().add(jButtonSearch);
		
	    
		tableProducts.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mousePressed(MouseEvent mouseEvent) {
		            
		            if(mouseEvent.getClickCount() == 2)
		            {
				        JTable table =(JTable) mouseEvent.getSource();
		            	Point point = mouseEvent.getPoint();
				        int row = table.rowAtPoint(point);
		            	Sale s=(Sale) tableModelProducts.getValueAt(row, 3);
		            	QuerySalesGUI.this.setVisible(false);
		            	jTextFieldSearch.setText("");
		            	
			            new ShowSaleGUI(s, saltzaile, QuerySalesGUI.this, null);
		            }
		        }
		 });
		
		BotoiBorobila btnAtzera = new BotoiBorobila(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.btnAtzera")); //$NON-NLS-1$ //$NON-NLS-2$
		btnAtzera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QuerySalesGUI.this.setVisible(false);
            	jTextFieldSearch.setText("");

				if(parent == null) {
				parent2.setVisible(true);
				}else {
					parent.setVisible(true);
				}
			}
		});
		btnAtzera.setBounds(642, 486, 101, 37);
		contentPane.add(btnAtzera);
		
		
	}
}
