package javaresources.lr11;

import javafx.beans.property.*;

public class Customer {
    private SimpleStringProperty passportNumber;
    private SimpleStringProperty fullName;
    private SimpleStringProperty phoneNumber;
    private SimpleStringProperty address;
    private SimpleDoubleProperty discount;
    private SimpleBooleanProperty isActive;

    public Customer(String passportNumber, String fullName, String phoneNumber, String address, double discount,  boolean isActive){
        this.passportNumber=new SimpleStringProperty(passportNumber);
        this.address=new SimpleStringProperty(address);
        this.discount=new SimpleDoubleProperty(discount);
        this.fullName=new SimpleStringProperty(fullName);
        this.isActive=new SimpleBooleanProperty(isActive);
        this.phoneNumber=new SimpleStringProperty(phoneNumber);
    }

    public String getPassportNumber() {
        return passportNumber.get();
    }
    public String getAddress() {
        return address.get();
    }
    public double getDiscount() {
        return discount.get();
    }
    public String  getFullName() {
        return fullName.get();
    }
    public boolean getIsActive() {
        return isActive.get();
    }
    public void setIsActive(boolean active){
        this.isActive=new SimpleBooleanProperty(active);
    }
    public String getPhoneNumber(){
        return phoneNumber.get();
    }
    public String toString(){
        return this.getPassportNumber();
    }
}