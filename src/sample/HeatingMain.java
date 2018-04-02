package sample;

public class HeatingMain {

    String name;

    public void action(){
        System.out.println("action from: " + name);
    }

    public HeatingMain(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
