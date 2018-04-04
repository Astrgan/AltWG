package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.*;
import java.util.*;

public class MyChart {

    private int length = 479;
    public TreeMap<Double, Double> data;
    public LineChart<Number,Number> lineChart;
    String name;
    String id;
    ResultSet resultSet;
    private String date;

    public LineChart createChart(String name, String id, String date)  {

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(1000 * 10), //1000 мс * 60 сек = 1 мин
                        ae -> this.createChart1()
                )
        );

        timeline.setCycleCount(0); //Ограничим число повторений
//        timeline.play();

        this.name = name;
        this.id = id;
        this.date = date;

        return createChart1();


    }

    public LineChart createChart1()  {
        this.connectToDB(date, id);

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setData(getChartData(resultSet));
        lineChart.setTitle(name);
        lineChart.setLegendVisible(false);



        for (XYChart.Series<Number, Number> series1 : lineChart.getData()) {
            //for all series1, take date, each data has Node (symbol) for representing point
            for (XYChart.Data<Number, Number> data1 : series1.getData()) {
                // this node is StackPane
                StackPane stackPane =  (StackPane) data1.getNode();
                stackPane.setPrefWidth(5);
                stackPane.setPrefHeight(5);
            }
        }

        Set<Node> node = lineChart.lookupAll(".default-color0.chart-line-symbol.series0.");
        node.forEach((element) -> {
            element.setOnMouseEntered((MouseEvent event1) -> {
                System.out.println("over value!");
                double x = event1.getScreenX();
                double y = event1.getScreenY();
                List keys = new ArrayList(data.keySet());


                for(int j=0; j<length; j++){
                    if (event1.getSource().toString().contains("data"+j)){
                        Tooltip t = new Tooltip(data.get(Double.parseDouble(keys.get(j).toString())).toString());
                        Tooltip.install(element, t);
                    }
                }
            });
        });
        return lineChart;
    }

    private ObservableList<XYChart.Series<Number, Number>> getChartData(ResultSet resultSet)  {

        ObservableList<XYChart.Series<Number, Number>> answer = FXCollections.observableArrayList();
        XYChart.Series<Number, Number> aSeries = new XYChart.Series<>();

        int count = 0;
        Set set = data.entrySet();
        Iterator itr = set.iterator();

        for (int j=0; j<length; j++){
            count++;
            if (itr.hasNext()){
                Map.Entry me = (Map.Entry)itr.next();

                aSeries.getData().add(new XYChart.Data(me.getKey(), me.getValue()));

            }else {
                aSeries.getData().add(new XYChart.Data((double)(j*3)/60, 0.0)); //(j*3)/60
            }

        }


        System.out.println("Число измерений: " + count);
        answer.addAll(aSeries);
        return answer;
    }



    private void connectToDB(String date, String id) {
        System.out.println("Connect...");


        String sql = "SELECT FROM_DT1970(TIME1970), TIME1970, VAL FROM RSDU2ELARH.EL010_" + id + " \n" +
                "WHERE time1970 > TO_DT1970(TO_DATE (?, 'YYYY-MM-DD HH24:MI:SS')) AND time1970 < TO_DT1970(TO_DATE (?, 'YYYY-MM-DD HH24:MI:SS'))\n";


        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@10.100.35.102:1521:rsdu", "rsdu2elarh", "passme");
             PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setString(1, date + " 0:00:00");
            statement.setString(2, date + " 23:59:59");
            resultSet = statement.executeQuery();

            data = new TreeMap();
            int i = 0;
            while (resultSet.next())
            {
                data.put((double)(i*3)/60, resultSet.getDouble("VAL"));
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
