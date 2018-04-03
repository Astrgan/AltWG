package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import java.io.*;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.VBox;

public class Controller implements Initializable{

    @FXML
    private DatePicker calendar;
    @FXML
    private TreeView<String> treeHM;
    @FXML
    private VBox vBox;

    ArrayList<HeatingMainParameters> listHMP;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, HeatingMainParameters> mapHeating = new LinkedHashMap<>();

    Connection connection = null;
    private ResultSet resultSet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        calendar.setValue(LocalDate.now());
        System.out.println("Controller");
        loadSettings();

        TreeItem<String> treeItemRoot = new TreeItem<>("root");
        treeItemRoot.setExpanded(false);

        for(Map.Entry<String, HeatingMainParameters> entry : mapHeating.entrySet()) {

            TreeItem<String> itemMH = new TreeItem<>(entry.getKey());

            for(Map.Entry<String, String> entryParams : entry.getValue().parameters.entrySet()) {
                itemMH.getChildren().add(new TreeItem<>(entryParams.getKey()));
            }

            treeItemRoot.getChildren().add(itemMH);
        }

        treeHM.setRoot(treeItemRoot);
        treeHM.setShowRoot(false);


    }

    private void connectToDB(String date, String id, String name) {
        System.out.println("Connect...");
        String sql = "SELECT FROM_DT1970(TIME1970), TIME1970, VAL FROM RSDU2ELARH.EL006_6303821 \n" +
                "WHERE time1970 > TO_DT1970(TO_DATE ('02.04.2018 23:59:59', 'DD.MM.YYYY HH24:MI:SS')) AND time1970 < TO_DT1970(TO_DATE ('04.04.2018 0:00:00', 'DD.MM.YYYY HH24:MI:SS'))\n";

        String sql2 = "SELECT FROM_DT1970(TIME1970), TIME1970, VAL FROM RSDU2ELARH.EL010_" + id + " \n" +
                "WHERE time1970 > TO_DT1970(TO_DATE (?, 'YYYY-MM-DD HH24:MI:SS')) AND time1970 < TO_DT1970(TO_DATE (?, 'YYYY-MM-DD HH24:MI:SS'))\n";


        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@10.100.35.102:1521:rsdu", "rsdu2elarh", "passme");
             PreparedStatement statement = connection.prepareStatement(sql2)) {


            statement.setString(1, date + " 0:00:00");
            statement.setString(2, date + " 23:59:59");
            resultSet = statement.executeQuery();

//            while (resultSet.next()) {
////                System.out.println("Date: " + resultSet.getDate(1) + " " + resultSet.getTime(1) + " VOL: " + resultSet.getDouble("VAL"));
////            }

            addChart(resultSet, name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void treeClicked(MouseEvent event) {
        if (event.getClickCount()==2){

            if (!treeHM.getSelectionModel().getSelectedItem().getParent().getValue().equals("root")) {
                System.out.println("Value item: " + treeHM.getSelectionModel().getSelectedItem().getValue());
                System.out.println("Value parent: " + treeHM.getSelectionModel().getSelectedItem().getParent().getValue());
                System.out.println("Date:" + calendar.getValue().toString());

                String id = mapHeating.get(treeHM.getSelectionModel().getSelectedItem().getParent().getValue()).parameters.get(treeHM.getSelectionModel().getSelectedItem().getValue());

                connectToDB(calendar.getValue().toString(), id, treeHM.getSelectionModel().getSelectedItem().getParent().getValue() + ": " + treeHM.getSelectionModel().getSelectedItem().getValue());

            }

        }

    }

    void loadSettings(){


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("Settings2.json"))){

            listHMP = gson.fromJson(bufferedReader,  new TypeToken<List<HeatingMainParameters>>(){}.getType());

            for (HeatingMainParameters hmp: listHMP) {
                mapHeating.putIfAbsent(hmp.name, hmp);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void addChart(ResultSet resultSet, String name) throws SQLException {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart lineChart = new LineChart(xAxis, yAxis);
        lineChart.setData(getChartData(resultSet));
        lineChart.setTitle(name);

        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(false);
        lineChart.getXAxis().setTickLabelsVisible(false);
        lineChart.getXAxis().setOpacity(0);


        vBox.getChildren().add(lineChart);
    }


    private ObservableList<XYChart.Series<Number, Double>> getChartData(ResultSet resultSet) throws SQLException {

        ObservableList<XYChart.Series<Number, Double>> answer = FXCollections.observableArrayList();



        XYChart.Series<Number, Double> aSeries = new XYChart.Series<>();

        int count = 0;

        for (int i=0; i<479; i++){
            count++;
            if (resultSet.next()){
                aSeries.getData().add(new XYChart.Data(i*3, resultSet.getDouble("VAL")));
            }else {
                aSeries.getData().add(new XYChart.Data(i*3, 0.0));
            }
        }


/*        while (resultSet.next()) {
            System.out.println("Date: " + resultSet.getDate(1) + " " + resultSet.getTime(1) + " VOL: " + resultSet.getDouble("VAL"));
            count++;
            aSeries.getData().add(new XYChart.Data(count+3, resultSet.getDouble("VAL")));
        }*/

        System.out.println("Число измерений: " + count);
        answer.addAll(aSeries);
        return answer;
    }




    void iniListHM(){

        HeatingMainParameters heatingMainParameters = new HeatingMainParameters();
        listHMP = new ArrayList<>();

        heatingMainParameters.parameters.putIfAbsent("6303821", "Тепло (расчет)");
        heatingMainParameters.parameters.putIfAbsent("6303789", "Подпитка");
        heatingMainParameters.parameters.putIfAbsent("6303717", "Расход прямой сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303716", "Расход обратной сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303715", "Давление обратной сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303714", "Давление прямой сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303713", "Температура сырой воды");
        heatingMainParameters.parameters.putIfAbsent("6303712", "Температура прямой сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303711", "Температура обратной сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303710", "Расход тепла прямой");
        heatingMainParameters.parameters.putIfAbsent("6303709", "Расход тепла обратный");
        heatingMainParameters.name  = "Теплотрасса 1-2";

        listHMP.add(heatingMainParameters);

        heatingMainParameters = new HeatingMainParameters();

        heatingMainParameters.parameters.putIfAbsent("6303822", "Тепло (расчет)");
        heatingMainParameters.parameters.putIfAbsent("6303790", "Подпитка");
        heatingMainParameters.parameters.putIfAbsent("6303761", "Расход прямой сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303762", "Расход обратной сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303759", "Давление обратной сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303760", "Давление прямой сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303758", "Температура сырой воды");
        heatingMainParameters.parameters.putIfAbsent("6303757", "Температура прямой сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303756", "Температура обратной сетевой воды");
        heatingMainParameters.parameters.putIfAbsent("6303755", "Расход тепла прямой");
        heatingMainParameters.parameters.putIfAbsent("6303754", "Расход тепла обратный");
        heatingMainParameters.name  = "Теплотрасса 10";
        listHMP.add(heatingMainParameters);

        for (HeatingMainParameters heatingMain1: listHMP) {
            heatingMain1.print();
        }



        try (Writer writer = new FileWriter("Settings2.json")) {

            gson.toJson(listHMP, writer);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
