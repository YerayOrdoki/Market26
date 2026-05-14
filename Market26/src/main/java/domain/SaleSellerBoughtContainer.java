package domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class SaleSellerBoughtContainer implements Serializable{
	private Sale sale;
	private Registered user;
	private BoughtSale bought;
	
	public SaleSellerBoughtContainer(Sale s) {
		sale=s;
		user=s.getSeller();
		bought=null;
	}
	public SaleSellerBoughtContainer(BoughtSale s) {
		System.out.println(s);
		sale=s.getSale();
		user=s.getSale().getSeller();
		bought=s;
	}
	public SaleSellerBoughtContainer() {super();}

	public BoughtSale getBought() {
		return bought;
	}
	public void setBought(BoughtSale bought) {
		this.bought = bought;
	}
	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public Registered getUser() {
		return user;
	}

	public void setUser(Registered user) {
		this.user = user;
	}
	
	
	
	
	

}
