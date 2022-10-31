package javaresources.lr11;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private TableView<SportInventory> inventory;
    @FXML
    private TableView<AboutOrder> ordersTable;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private Label title;
    @FXML
    private Label enabled;
    @FXML
    private TextArea description;
    @FXML
    private ImageView imgInventory;
    @FXML
    private MenuItem menuInsert, menuUpdate;

    public static ObservableList<SportInventory> inventories = FXCollections.observableArrayList();
    public static ObservableList<Customer> customers = FXCollections.observableArrayList();
    public static ObservableList<AboutOrder> orders = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createTable();

        TableView.TableViewSelectionModel<SportInventory> selectionModel = inventory.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<SportInventory>() {
            public void changed(ObservableValue<? extends SportInventory> val, SportInventory oldVal, SportInventory newVal) {
                if (newVal != null) {
                    try {
                        imgInventory.setImage(new Image(new FileInputStream(newVal.getPhotoPath()),225.0,139.0,false,true));
                        enabled.setText((newVal.getEnabled()) ? "Доступно" : "Недоступно");
                        title.setText(newVal.getTitle());
                        description.setText(newVal.getDescription());
                    } catch (Exception e){
                        System.err.println(e);
                    }
                }
            }
        });

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                FXMLLoader fxmlLoader;
                if(((MenuItem)e.getSource())==menuInsert) {
                    fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("insert.fxml"));
                    newStage(fxmlLoader);
                }
                else {
                    fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("update.fxml"));
                    newStage(fxmlLoader);
                }
            }
        };
        menuInsert.setOnAction(event);
        menuUpdate.setOnAction(event);
    }
    //Удаление из таблицы СпортИнвентарь по щелчку правой кнопкой мыши по записи
    public void contextDeleteActionInventories(){
        TableView.TableViewSelectionModel<SportInventory> selectionModel = inventory.getSelectionModel();
        if(selectionModel.getSelectedItem()!=null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы уверены, что хотите удалить запись?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert = new Alert( Alert.AlertType.CONFIRMATION,"Вы точно в этом уверены?", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    if(selectionModel.getSelectedItem().getEnabled()==false){
                        //Полное удаление
                        DBConnection.statementExecute("delete from Inventory where id=" + selectionModel.getSelectedItem().getID());
                        System.out.println(selectionModel.getSelectedItem().getID());
                        inventories.remove(selectionModel.getSelectedItem());
                    }
                    else{
                        //Замена поля доступность
                        DBConnection.statementExecute("update Inventory set enabled=false where id=" + selectionModel.getSelectedItem().getID());
                        selectionModel.getSelectedItem().setEnabled(false);
                        inventory.refresh();
                        enabled.setText("Недоступно");
                    }
                }
            }
        }
    }
    //Удаление из таблицы Клиента по щелчку правой кнопкой мыши по записи
    public void contextDeleteActionCustomers(){
        TableView.TableViewSelectionModel<Customer> selectionModel = customersTable.getSelectionModel();
        if(selectionModel.getSelectedItem()!=null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы уверены, что хотите удалить запись?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert = new Alert( Alert.AlertType.CONFIRMATION,"Вы точно в этом уверены?", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    if(selectionModel.getSelectedItem().getIsActive()==false){
                        //Полное удаление
                        DBConnection.statementExecute("delete from Customer where passportNumber='" + selectionModel.getSelectedItem().getPassportNumber()+"'");
                        System.out.println(selectionModel.getSelectedItem().getPassportNumber());
                        customers.remove(selectionModel.getSelectedItem());
                    }
                    else{
                        //Замена поля доступность
                        DBConnection.statementExecute("update Customer set isActive=false where passportNumber='" + selectionModel.getSelectedItem().getPassportNumber()+"'");
                        selectionModel.getSelectedItem().setIsActive(false);
                        customersTable.refresh();
                    }
                }
            }
        }
    }
    //Создание и добавление записей в таблицу из бд
    public void createTable(){
        ResultSet setInventory = DBConnection.statementExecuteQuery("select * from Inventory");
        ResultSet setCustomer = DBConnection.statementExecuteQuery("select * from Customer");
        ResultSet setOrder = DBConnection.statementExecuteQuery("select * from AboutOrder");
        Customer customer;
        SportInventory item;

        try {
            while(setInventory.next()) {
                inventories.add(new SportInventory(setInventory.getInt(1), setInventory.getString(2), setInventory.getString(3),setInventory.getInt(4),setInventory.getDouble(5),setInventory.getString(6),setInventory.getBoolean(7)));
            }
            while(setCustomer.next()) {
                customers.add(new Customer(setCustomer.getString(1), setCustomer.getString(2), setCustomer.getString(3),setCustomer.getString(4),setCustomer.getDouble(5),setCustomer.getBoolean(6)));
            }
            while(setOrder.next()) {
                customer=customers.get(setOrder.getInt(4)-1);
                item=inventories.get(setOrder.getInt(3)-1);
                orders.add(new AboutOrder(setOrder.getInt(1),setOrder.getDate(2), item, customer, setOrder.getInt(5)));
            }
        }
        catch (Exception e){
            System.err.println(e.fillInStackTrace());
            new RuntimeException(e);
        }

        TableColumn<SportInventory, String> inventoryTitle = new TableColumn<SportInventory, String>("Название");
        inventoryTitle.setCellValueFactory(new PropertyValueFactory<SportInventory, String>("title"));
        TableColumn<SportInventory, Integer> inventoryCount = new TableColumn<SportInventory, Integer>("Количество");
        inventoryCount.setCellValueFactory(new PropertyValueFactory<SportInventory, Integer>("count"));
        TableColumn<SportInventory, Double> inventoryCost= new TableColumn<SportInventory, Double>("Цена за час");
        inventoryCost.setCellValueFactory(new PropertyValueFactory<SportInventory, Double>("costPerHour"));

        inventory.getColumns().addAll(inventoryTitle, inventoryCount, inventoryCost);
        inventory.setItems(inventories);

        TableColumn<AboutOrder, String> customerName = new TableColumn<AboutOrder, String>("Имя клиента");
        customerName.setCellValueFactory(customer1 -> new ReadOnlyStringWrapper(customer1.getValue().getCustomer().getFullName()));
        TableColumn<AboutOrder, Integer> hourCount = new TableColumn<AboutOrder, Integer>("Количество часов");
        hourCount.setCellValueFactory(new PropertyValueFactory<AboutOrder, Integer>("hoursCount"));
        TableColumn<AboutOrder, String> inventoryTitleOrder= new TableColumn<AboutOrder, String>("Название");
        inventoryTitleOrder.setCellValueFactory(inventoryItem -> new ReadOnlyStringWrapper(inventoryItem.getValue().getInventoryItem().getTitle()));
        TableColumn<AboutOrder, Date> orderDate= new TableColumn<AboutOrder, Date>("Дата заказа");
        orderDate.setCellValueFactory(new PropertyValueFactory<AboutOrder, Date>("date"));

        ordersTable.getColumns().addAll(customerName, hourCount, inventoryTitleOrder,orderDate);
        ordersTable.setItems(orders);

        TableColumn<Customer, String> customerFullName = new TableColumn<Customer, String>("Имя клиента");
        customerFullName.setCellValueFactory(new PropertyValueFactory<Customer, String>("fullName"));
        TableColumn<Customer, String> phoneNumber = new TableColumn<Customer, String>("Номер телефона");
        phoneNumber.setCellValueFactory(new PropertyValueFactory<Customer, String>("phoneNumber"));
        TableColumn<Customer, String> address = new TableColumn<Customer, String>("Адрес");
        address.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        TableColumn<Customer, Boolean> isActive= new TableColumn<Customer, Boolean>("Активен");
        isActive.setCellValueFactory(new PropertyValueFactory<Customer, Boolean>("isActive"));

        customersTable.getColumns().addAll(customerFullName, phoneNumber, address,isActive);
        customersTable.setItems(customers);
    }
    //Форма добавления записей в таблицы
    private void newStage(FXMLLoader fxmlLoader){
        Stage stage=new Stage();
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        stage.setTitle("Добавление новой записи");
        stage.setScene(scene);
        stage.show();
    }
    //Удаление из таблицы Заказы по щелчку правой кнопкой мыши по записи
    public void contextDeleteActionOrders() {
        TableView.TableViewSelectionModel<AboutOrder> selectionModel = ordersTable.getSelectionModel();
        if(selectionModel.getSelectedItem()!=null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы уверены, что хотите удалить запись?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert = new Alert( Alert.AlertType.CONFIRMATION,"Вы точно в этом уверены?", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    //Полное удаление
                    DBConnection.statementExecute("delete from AboutOrder where orderNumber='" + selectionModel.getSelectedItem().getOrderNumber()+"'");
                    System.out.println(selectionModel.getSelectedItem().getOrderNumber());
                    orders.remove(selectionModel.getSelectedItem());
                }
            }
        }
    }
    @FXML
    private void hourRentCost(){
        List<Double> finalOrderCost=new ArrayList<>();
        for(AboutOrder order:orders){
            finalOrderCost.add(order.getHoursCount()*order.getInventoryItem().getCostPerHour());
            System.out.println(order.getHoursCount()*order.getInventoryItem().getCostPerHour());
        }
    }
}