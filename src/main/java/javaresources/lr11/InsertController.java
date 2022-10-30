package javaresources.lr11;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.SearchableComboBox;

import javax.swing.event.DocumentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.ResourceBundle;

public class InsertController implements Initializable {
    @FXML
    private TabPane TabPane;
    @FXML
    private Spinner<Integer> count,hoursCount;
    @FXML
    private Spinner<Double> cost,discount;
    @FXML
    private TextField title,passportNumber,fullName,phoneNumber;
    @FXML
    private TextArea description,address;
    @FXML
    private CheckBox access,active;
    @FXML
    private SearchableComboBox<SportInventory> inventoryItemPicker;
    @FXML
    private SearchableComboBox<Customer> customerPicker;
    @FXML
    private DatePicker datePicker;

    private String filePath="";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        count.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 3));
        cost.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,10000,10,0.01));
        discount.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,1,0,0.01));
        hoursCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 3));
        inventoryItemPicker.setItems(HelloController.inventories);
        customerPicker.setItems(HelloController.customers);
        inventoryItemPicker.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode()== KeyCode.ENTER){
                if(inventoryItemPicker.getSelectionModel().getSelectedIndex()==-1){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Нет подходящих условию поиска элементов! Хотите добавить?", ButtonType.NO,ButtonType.YES);
                    alert.showAndWait();
                    if(alert.getResult()==ButtonType.YES)
                        TabPane.getSelectionModel().select(0);
                }
            }
        });
        customerPicker.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode()== KeyCode.ENTER){
                if(customerPicker.getSelectionModel().getSelectedIndex()==-1){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Нет подходящих условию поиска элементов! Хотите добавить?", ButtonType.NO,ButtonType.YES);
                    alert.showAndWait();
                    if(alert.getResult()==ButtonType.YES)
                        TabPane.getSelectionModel().select(1);
                }
            }
        });
    }
    @FXML
    public void access(ActionEvent event){
        ((CheckBox)event.getSource()).setText(((CheckBox)event.getSource()).isSelected() ? "Доступно" : "Недоступно");
    }
    @FXML
    public void selectPhoto(){
        Stage stage = new Stage();
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Выберите фото");
        fileChooser.setInitialDirectory(new File("/Users/timofejspostgmail.com/Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select...","*.jpg","*.jpeg","*.png"));
        File file=fileChooser.showOpenDialog(stage);
        filePath=file.getPath();
    }
    @FXML
    public void insertIntoInventory() throws SQLException {
        if(title.getText()!="" && description.getText()!="" && filePath!=""){
            int lastIndex=0;
            for(SportInventory i: HelloController.inventories){
                if(i.getTitle().equals(title.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Инвентарь с таким названием уже существует!", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
            }
            DBConnection.statementExecute("insert into Inventory values (default, '"+title.getText()+"', '"+description.getText()+"', "+count.getValue()+", "+cost.getValue()+", '"+filePath+"', "+access.isSelected()+"); ");
            ResultSet set=DBConnection.statementExecuteQuery("select max(ID) from Inventory");
            while(set.next())
                lastIndex=set.getInt(1);
            HelloController.inventories.add(new SportInventory(lastIndex,title.getText(),description.getText(),count.getValue(),cost.getValue(),filePath,access.isSelected()));

        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Заполните все поля!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    @FXML
    public void insertIntoCustomer() throws SQLException {
        Alert alert;
        if(passportNumber.getText()!="" && fullName.getText()!="" && phoneNumber.getText()!="" && address.getText()!=""){
            if(passportNumber.getText().matches("\\A[A-Z]{2}\\d{7}")) {
                if(phoneNumber.getText().matches("\\A\\d{7}")) {
                    for (Customer i : HelloController.customers) {
                        if (passportNumber.getText().equals(i.getPassportNumber())) {
                            alert = new Alert(Alert.AlertType.ERROR, "Клиент с таким номером паспорта уже существует", ButtonType.OK);
                            alert.showAndWait();
                            return;
                        }
                    }
                    DBConnection.statementExecute("insert into Customer values ('" + passportNumber.getText() + "', '" + fullName.getText() + "', '" + phoneNumber.getText() + "', '" + address.getText() + "'," + discount.getValue() + "," + active.isSelected() + "); ");
                    HelloController.customers.add(new Customer(passportNumber.getText(), fullName.getText(), phoneNumber.getText(), address.getText(), discount.getValue(), active.isSelected()));
                }
                else{
                    phoneNumber.setText("");
                    alert = new Alert(Alert.AlertType.ERROR, "Неправильно набран номер", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            else{
                passportNumber.setText("");
                alert = new Alert(Alert.AlertType.ERROR, "Несуществующий номер паспорта", ButtonType.OK);
                alert.showAndWait();
            }
        }
        else {
            alert = new Alert(Alert.AlertType.ERROR, "Заполните все поля!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    @FXML
    public void insertIntoOrders() throws SQLException {
        if(!(inventoryItemPicker.getValue() ==null) && !(customerPicker.getValue()==null) && !(datePicker.getValue()==null)) {
            if (!inventoryItemPicker.getValue().getEnabled() || !customerPicker.getValue().getIsActive()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Пользователь или инвентарь недоступен!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            else {
                DBConnection.statementExecute("insert into AboutOrder values ("+(HelloController.orders.size()+1)+", '"+datePicker.getValue()+"', "+inventoryItemPicker.getValue().getID()+", '"+customerPicker.getValue().getPassportNumber()+"', "+hoursCount.getValue()+"); ");
                HelloController.orders.add(new AboutOrder(HelloController.orders.size()+1,new Date((datePicker.getValue().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())),inventoryItemPicker.getValue(),customerPicker.getValue(),hoursCount.getValue()));
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Заполните все поля!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    @FXML
    public void active(ActionEvent event){
        ((CheckBox)event.getSource()).setText(((CheckBox)event.getSource()).isSelected() ? "Активен" : "Неактивен");
    }
}
