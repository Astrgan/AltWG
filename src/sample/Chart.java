package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

public class Chart {

    private int length = 479;
    public TreeMap<Double, Double> data;

    public LineChart createChart(ResultSet resultSet, String name) throws SQLException {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setData(getChartData(resultSet));
        lineChart.setTitle(name);
//        yAxis.setAutoRanging(false);
//        yAxis.setLowerBound(0);
//        yAxis.setUpperBound(1000);
        lineChart.setLegendVisible(false);
//        lineChart.setCreateSymbols(false);
//        lineChart.getXAxis().setTickLabelsVisible(false);
//        lineChart.getXAxis().setOpacity(0);


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

    private ObservableList<XYChart.Series<Number, Number>> getChartData(ResultSet resultSet) throws SQLException {

        ObservableList<XYChart.Series<Number, Number>> answer = FXCollections.observableArrayList();



        XYChart.Series<Number, Number> aSeries = new XYChart.Series<>();

        int count = 0;

        data = new TreeMap();
        int i = 0;
        while (resultSet.next())
        {
            data.put((double)(i*3)/60, resultSet.getDouble("VAL"));
            i++;
        }

        Set set = data.entrySet();
        Iterator itr = set.iterator();


        for (int j=0; j<length; j++){
            count++;
            if (itr.hasNext()){
                Map.Entry me = (Map.Entry)itr.next();

//                System.out.println("x: " + (double)(j*3)/60 + "y: " + resultSet.getDouble("VAL"));
                aSeries.getData().add(new XYChart.Data(me.getKey(), me.getValue()));

            }else {
                aSeries.getData().add(new XYChart.Data((double)(j*3)/60, 0.0)); //(j*3)/60
            }

        }


        System.out.println("Число измерений: " + count);
        answer.addAll(aSeries);
        return answer;
    }
}
