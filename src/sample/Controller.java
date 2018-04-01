package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    ListView<String> myList;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Controller");

        myList.getItems().add("Item 1");
        myList.getItems().add("Item 2");
        myList.getItems().add("Item 3");
    }
}
