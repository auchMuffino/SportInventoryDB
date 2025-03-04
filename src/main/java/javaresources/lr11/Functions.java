package javaresources.lr11;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class Functions implements Initializable {
    @FXML
    ComboBox<String> chooser;
    @FXML
    TableView tab;
    @FXML
    TextField argument;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chooser.getItems().addAll("Стоимость всех заказов", "Фамилии клиентов и название инвентаря, скидка которых больше введенной",
                "Информацию о клиенте, взявшем инвентарь на минимальный срок","Общее количество инвентаря и сумму, сданного в прокат сегодня","Название и описание инвентаря, который сдавался в прокат в текущем месяце со скидкой большей, чем вводит пользователь",
                "Общее количество часов проката по каждому наименованию инвентаря",
                "Количестве заказов на каждую дату. Выводить только те даты, когда количество заказов было более 5","О спортивном инвентаре, который не сдавался в прокат",
                "Для введенного клиента показать наименования и общее количество часов проката инвентаря, который брался им в прокат");
        javafx.event.EventHandler<ActionEvent> eventChoose=new javafx.event.EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (chooser.getSelectionModel().getSelectedIndex()){
                    case 0:{
                        rentalCost();
                        break;
                    }
                    case 1:{
                        argument.setPromptText("Введите скидку");
                        argument.setVisible(true);
                        argument.setOnKeyPressed(keyEvent->{
                            if(keyEvent.getCode()== KeyCode.ENTER && argument.getText()!=null) {
                                try {
                                    surnameAndEnventoryItemWithDiscount(argument.getText());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                argument.setVisible(false);
                                argument.setText(null);
                            }
                        });
                        break;
                    }
                    case 2:{
                        try {
                            minimalTermRent();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case 3:{
                        try {
                            inventoryCountToday();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case 4:{
                        argument.setPromptText("Введите скидку");
                        argument.setVisible(true);
                        argument.setOnKeyPressed(keyEvent->{
                            if(keyEvent.getCode()== KeyCode.ENTER && argument.getText()!=null) {
                                try {
                                    getInventoryWithParameters(argument.getText());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                argument.setVisible(false);
                                argument.setText(null);
                            }
                        });
                        break;
                    }
                    case 5:{
                        try {
                            getTotalByInventoryItem();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case 6:{
                        try {
                            getOrdersByDate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case 7:{
                        try {
                            getNonPopularInventory();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case 8:{
                        argument.setPromptText("Введите номер паспорта");
                        argument.setVisible(true);
                        argument.setOnKeyPressed(keyEvent->{
                            if(keyEvent.getCode()== KeyCode.ENTER && argument.getText()!=null) {
                                try {
                                    getCustomerOrders(argument.getText());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                argument.setVisible(false);
                                argument.setText(null);
                            }
                        });
                        break;
                    }
                    default:{
                        Alert alert=new Alert(Alert.AlertType.ERROR,"Нет такого пункта меню",ButtonType.OK);
                        alert.showAndWait();
                        break;
                    }
                }
            }
        };
        chooser.setOnAction(eventChoose);
    }
    //Стоимость заказа, согласно выбранному инвентарю и количеству часов
    public void rentalCost(){
        tab.getColumns().clear();
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        for(AboutOrder order:HelloController.orders){
            fr.add(new FunctionsNeeds(order.getInventoryItem().getTitle(),null,null,null,null,null,order.getHoursCount()*order.getInventoryItem().getCostPerHour()-(order.getHoursCount()*order.getInventoryItem().getCostPerHour()*order.getCustomer().getDiscount()),0,order.getHoursCount(),0));
        }

        TableColumn<FunctionsNeeds, String> title = new TableColumn<FunctionsNeeds, String>("Название инвентаря");
        title.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, Integer> hours = new TableColumn<FunctionsNeeds, Integer>("Количество часов");
        hours.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Integer>("Integer1"));
        TableColumn<FunctionsNeeds, Double> totalCost = new TableColumn<FunctionsNeeds, Double>("Сумма заказов инвентаря");
        totalCost.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Double>("Double1"));

        tab.getColumns().addAll(title, hours,totalCost);
        tab.setItems(fr);
    }
    //Фамилии клиентов и название инвентаря, сумма скидки которых составила больше введенной
    public void surnameAndEnventoryItemWithDiscount(String argumentQ) throws SQLException {
        tab.getColumns().clear();
        ResultSet invetoriesAndInventories = DBConnection.statementExecuteQuery("select Customer.fullName, group_concat(distinct Inventory.title separator ',') as inv from AboutOrder inner join Customer on Customer.passportNumber=AboutOrder.passportNumber inner join Inventory on Inventory.ID=AboutOrder.inventoryNumber where Customer.discount>"+argumentQ+" group by Customer.fullName;");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(invetoriesAndInventories.next()) {
            fr.add(new FunctionsNeeds(invetoriesAndInventories.getString(1),invetoriesAndInventories.getString(2),null,null,null,null,0,0,0,0));
        }

        TableColumn<FunctionsNeeds, String> fullname = new TableColumn<FunctionsNeeds, String>("ФИО");
        fullname.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, String> inventories = new TableColumn<FunctionsNeeds, String>("Название инвентаря");
        inventories.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String2"));

        tab.getColumns().addAll(fullname, inventories);
        tab.setItems(fr);
    }
    //Информацию о клиенте, взявшем инвентарь на минимальный срок
    public void minimalTermRent() throws SQLException {
        tab.getColumns().clear();
        ResultSet customersQ = DBConnection.statementExecuteQuery("select distinct Customer.* from AboutOrder inner join Customer on Customer.passportNumber=AboutOrder.passportNumber where AboutOrder.hoursCount=(select min(hoursCount) from AboutOrder);");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(customersQ.next()) {
            fr.add(new FunctionsNeeds(customersQ.getString(1),customersQ.getString(2),customersQ.getString(3),customersQ.getString(4),null,null,customersQ.getDouble(5),0,0,0));
        }

        TableColumn<FunctionsNeeds, String> passport = new TableColumn<FunctionsNeeds, String>("Номер паспорта");
        passport.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, String> fullname = new TableColumn<FunctionsNeeds, String>("ФИО");
        fullname.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String2"));
        TableColumn<FunctionsNeeds, String> phone = new TableColumn<FunctionsNeeds, String>("Номер телефона");
        phone.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String3"));
        TableColumn<FunctionsNeeds, String> address = new TableColumn<FunctionsNeeds, String>("Адрес");
        address.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String4"));
        TableColumn<FunctionsNeeds, Double> discount = new TableColumn<FunctionsNeeds, Double>("Скидка");
        discount.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Double>("Double1"));

        tab.getColumns().addAll(passport, fullname,phone,address,discount);
        tab.setItems(fr);
    }
    //Общее количество инвентаря и сумму, сданного в прокат сегодня
    public void inventoryCountToday() throws SQLException {
        tab.getColumns().clear();
        ResultSet count = DBConnection.statementExecuteQuery("select count(AboutOrder.inventoryNumber),sum(Inventory.costForHour*AboutOrder.hoursCount) from AboutOrder inner join Inventory on Inventory.ID=AboutOrder.inventoryNumber where AboutOrder.orderDate=CURDATE() group by AboutOrder.orderDate;");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(count.next()) {
            fr.add(new FunctionsNeeds(null,null,null,null,null,null,count.getDouble(2),0,count.getInt(1),0));
        }

        TableColumn<FunctionsNeeds, Integer> cnt = new TableColumn<FunctionsNeeds, Integer>("Количество сданного сегодня инвентаря");
        cnt.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Integer>("Integer1"));
        TableColumn<FunctionsNeeds, Double> totalCost = new TableColumn<FunctionsNeeds, Double>("Сумма сданного сегодня инвентаря");
        totalCost.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Double>("Double1"));

        tab.getColumns().addAll(cnt, totalCost);
        tab.setItems(fr);
    }
    //Название и описание инвентаря, который сдавался в прокат в текущем месяце со скидкой большей, чем вводит пользователь.
    public void getInventoryWithParameters(String argumentQ) throws SQLException {
        tab.getColumns().clear();
        ResultSet inventoriesQ=DBConnection.statementExecuteQuery("select Inventory.title, Inventory.description from AboutOrder inner join Inventory on Inventory.ID=AboutOrder.inventoryNumber inner join Customer on  Customer.passportNumber=AboutOrder.passportNumber where month(AboutOrder.orderDate)=month(CURDATE()) and Customer.discount>"+argumentQ);
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(inventoriesQ.next()) {
            fr.add(new FunctionsNeeds(inventoriesQ.getString(1),inventoriesQ.getString(2),null,null,null,null,0,0,0,0));
        }
        TableColumn<FunctionsNeeds, String> title = new TableColumn<FunctionsNeeds, String>("Название инвентаря");
        title.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, String> description = new TableColumn<FunctionsNeeds, String>("Описание");
        description.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String2"));

        tab.getColumns().addAll(title, description);
        tab.setItems(fr);
    }
    //Общее количество часов проката по каждому наименованию инвентаря.
    public void getTotalByInventoryItem() throws SQLException {
        tab.getColumns().clear();
        ResultSet inventoriesQ=DBConnection.statementExecuteQuery("select Inventory.title, sum(AboutOrder.hoursCount) from Inventory inner join AboutOrder on Inventory.ID=AboutOrder.inventoryNumber group by Inventory.title;");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(inventoriesQ.next()) {
            fr.add(new FunctionsNeeds(inventoriesQ.getString(1),null,null,null,null,null,0,0, inventoriesQ.getInt(2), 0));
        }

        TableColumn<FunctionsNeeds, String> title = new TableColumn<FunctionsNeeds, String>("Название инвентаря");
        title.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, Integer> hours = new TableColumn<FunctionsNeeds, Integer>("Общее количество часов");
        hours.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Integer>("Integer1"));

        tab.getColumns().addAll(title, hours);
        tab.setItems(fr);
    }
    //Количестве заказов на каждую дату. Выводить только те даты, когда количество заказов было более 5.
    public void getOrdersByDate() throws SQLException {
        tab.getColumns().clear();
        ResultSet inventoriesQ=DBConnection.statementExecuteQuery("select orderDate from AboutOrder group by orderDate having count(inventoryNumber)>5;");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(inventoriesQ.next()) {
            fr.add(new FunctionsNeeds(inventoriesQ.getDate(1)));
        }

        TableColumn<FunctionsNeeds, Date> date = new TableColumn<FunctionsNeeds, Date>("Дата");
        date.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Date>("date"));

        tab.getColumns().addAll(date);
        tab.setItems(fr);
    }
    //О спортивном инвентаре, который не сдавался в прокат.
    public void getNonPopularInventory() throws SQLException {
        tab.getColumns().clear();
        ResultSet inventoriesQ=DBConnection.statementExecuteQuery("select Inventory.* from Inventory left join AboutOrder on Inventory.ID=AboutOrder.inventoryNumber where AboutOrder.orderNumber is null;");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(inventoriesQ.next()) {
            fr.add(new FunctionsNeeds(inventoriesQ.getString(2),inventoriesQ.getString(3),null,null,null,null,inventoriesQ.getDouble(5),0, inventoriesQ.getInt(4), 0));
        }

        TableColumn<FunctionsNeeds, String> title = new TableColumn<FunctionsNeeds, String>("Название инвентаря");
        title.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, String> description = new TableColumn<FunctionsNeeds, String>("Описание");
        description.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String2"));
        TableColumn<FunctionsNeeds, Integer> cnt = new TableColumn<FunctionsNeeds, Integer>("Количетсво на складе");
        cnt.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Integer>("Integer1"));
        TableColumn<FunctionsNeeds, Double> cost = new TableColumn<FunctionsNeeds, Double>("Стоимость часа проката");
        cost.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Double>("Double1"));

        tab.getColumns().addAll(title, description, cnt,cost);
        tab.setItems(fr);
    }
    //Для введенного клиента показать наименования и общее количество часов проката инвентаря, который брался им в прокат.
    public void getCustomerOrders(String argumentQ) throws SQLException {
        tab.getColumns().clear();
        ResultSet invetoriesAndHoursCount=DBConnection.statementExecuteQuery("select group_concat(distinct Inventory.title separator ','), sum(AboutOrder.hoursCount) from AboutOrder inner join Customer on Customer.passportNumber=AboutOrder.passportNumber inner join Inventory on Inventory.ID=AboutOrder.inventoryNumber where AboutOrder.passportNumber="+argumentQ+" group by AboutOrder.passportNumber;");
        ObservableList<FunctionsNeeds> fr = FXCollections.observableArrayList();
        while(invetoriesAndHoursCount.next()) {
            fr.add(new FunctionsNeeds(invetoriesAndHoursCount.getString(1),null,null,null,null,null,0,0, invetoriesAndHoursCount.getInt(2), 0));
        }

        TableColumn<FunctionsNeeds, String> title = new TableColumn<FunctionsNeeds, String>("Название инвентаря");
        title.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, String>("String1"));
        TableColumn<FunctionsNeeds, Integer> hours = new TableColumn<FunctionsNeeds, Integer>("Общее число часов проката");
        hours.setCellValueFactory(new PropertyValueFactory<FunctionsNeeds, Integer>("Integer1"));

        tab.getColumns().addAll(title, hours);
        tab.setItems(fr);
    }
}
