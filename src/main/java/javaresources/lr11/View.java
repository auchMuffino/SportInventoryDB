package javaresources.lr11;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class View {
    @FXML private TableView<SportInventory> inventory;

    void setTable(TableColumn... tableColumns) {
        inventory.getColumns().addAll(tableColumns);
    }
}
