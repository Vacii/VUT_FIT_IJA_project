package ija.umleditor.uml;

public class UMLMessage extends Element {
    String type;
    String class1;
    String class2;
    String operation;
    double height;
    int order;
//TODO why order and why string

    public UMLMessage(String name, String class1, String class2, String type, String operation) {
        super(name);
        this.type = type;
        this.class1 = class1;
        this.class2 = class2;
        this.operation = operation;
        this.height = 50;
        this.order = 1;
    }

    public String getClass1(){
        return class1;
    }

    public String getClass2(){
        return class2;
    }

    public String getType(){
        return type;
    }

    public String getOperation(){
        return operation;
    }

    public double getHeight(){
        return height;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public int getOrder(){
        return order;
    }

    public void setOrder(Integer order){
        this.order = order;
    }
}
