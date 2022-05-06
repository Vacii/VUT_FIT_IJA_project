package ija.umleditor.uml;

public class UMLSequence extends Element{
    private UMLClass aClass;
    private double position;

    public UMLSequence(String name, UMLClass aClass, double position) {
        super(name);
        this.aClass = aClass;
        this.position = position;
    }
}
