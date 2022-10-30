package javaresources.lr11;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Date;

public class AboutOrder {

    private SimpleIntegerProperty orderNumber;
    private SimpleObjectProperty<Date> date;
    private SimpleObjectProperty<SportInventory> inventoryItem;
    private SimpleObjectProperty<Customer> customer;
    private SimpleIntegerProperty hoursCount;

    public AboutOrder(int orderNumber, Date date, SportInventory inventoryItem, Customer customer, int hoursCount) {
        this.orderNumber=new SimpleIntegerProperty(orderNumber);
        this.customer=new SimpleObjectProperty<Customer>(customer);
        this.hoursCount=new SimpleIntegerProperty(hoursCount);
        this.inventoryItem=new SimpleObjectProperty<SportInventory>(inventoryItem);
        this.date=new SimpleObjectProperty<Date>(date);
    }

    public int getOrderNumber(){
        return orderNumber.get();
    }
    public Customer getCustomer(){
        return customer.get();
    }
    public int getHoursCount(){
        return hoursCount.get();
    }
    public SportInventory getInventoryItem(){
        return inventoryItem.get();
    }
    public Date getDate(){
        return date.get();
    }
    public String toString(){
        return inventoryItem.getName();
    }
}
