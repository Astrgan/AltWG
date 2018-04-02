package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private TreeView<HeatingMain> treeHM;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Controller");

        HeatingMain heatingMainRoot = new HeatingMain("root");


        HeatingMain heatingMain1 = new HeatingMain("Теплотрасса 1");



        HeatingMain heatingMain2 = new HeatingMain("Теплотрасса 2");

        TreeItem<HeatingMain> treeItemRoot = new TreeItem<>(heatingMainRoot);
        treeItemRoot.setExpanded(false);
        TreeItem<HeatingMain> treeItemHeatingMain1 = new TreeItem<>(heatingMain1);
        TreeItem<HeatingMain> treeItemHeatingMain2 = new TreeItem<>(heatingMain2);

        treeItemRoot.getChildren().setAll(treeItemHeatingMain1, treeItemHeatingMain2);

        treeHM.setRoot(treeItemRoot);

    }

    @FXML
    void treeClicked(MouseEvent event) {
        if (event.getClickCount()==2){

            if (! treeHM.getSelectionModel().getSelectedItem().getValue().toString().equals("root")) {
                System.out.println("Value item: " + treeHM.getSelectionModel().getSelectedItem().getValue());
                System.out.println("Value parent: " + treeHM.getSelectionModel().getSelectedItem().getParent().getValue());
            }

        }
    }
}
