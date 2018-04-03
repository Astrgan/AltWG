package sample;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HeatingMainParameters {


    public String name;
    public Map<String, String> parameters = new LinkedHashMap<>();

    public void print(){
        System.out.println("-----------------------------------");
        System.out.println("Наименование: " + name);
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            System.out.println("key: " + entry.getKey() + " Value: " + entry.getValue());
        }

        System.out.println();
    }

}
