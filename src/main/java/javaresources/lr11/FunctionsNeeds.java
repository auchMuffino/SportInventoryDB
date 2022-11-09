package javaresources.lr11;

import javafx.beans.property.*;

import java.util.Date;

public class FunctionsNeeds {
    private SimpleStringProperty String1;
    private SimpleStringProperty String2;
    private SimpleStringProperty String3;
    private SimpleStringProperty String4;
    private SimpleStringProperty String5;
    private SimpleStringProperty String6;
    private SimpleDoubleProperty Double1;
    private SimpleDoubleProperty Double2;
    private SimpleIntegerProperty Integer1;
    private SimpleIntegerProperty Integer2;
    private SimpleObjectProperty<Date> date;

    public FunctionsNeeds(String String1,String String2,String String3,String String4,String String5,String String6,double Double1,double Double2,int Integer1,int Integer2){
        this.String1=new SimpleStringProperty(String1);
        this.String2=new SimpleStringProperty(String2);
        this.String3=new SimpleStringProperty(String3);
        this.String4=new SimpleStringProperty(String4);
        this.String5=new SimpleStringProperty(String5);
        this.String6=new SimpleStringProperty(String6);
        this.Double1=new SimpleDoubleProperty(Double1);
        this.Double2=new SimpleDoubleProperty(Double2);
        this.Integer1=new SimpleIntegerProperty(Integer1);
        this.Integer2=new SimpleIntegerProperty(Integer2);
    }
    public FunctionsNeeds(Date date){
        this.date=new SimpleObjectProperty<Date>(date);
    }

    public Date getDate(){return date.get();}
    public String getString1(){return String1.get();}
    public String getString2(){return String2.get();}
    public String getString3(){return String3.get();}
    public String getString4(){return String4.get();}
    public String getString5(){return String5.get();}
    public String getString6(){return String6.get();}
    public double getDouble1(){return Double1.get();}
    public double getDouble2(){return Double2.get();}
    public int getInteger1(){return Integer1.get();}
    public int getInteger2(){return Integer2.get();}
}
