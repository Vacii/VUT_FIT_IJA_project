package ija.umleditor.uml;

import ija.umleditor.uml.UMLClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ija.umleditor.Main.classDiagram;

public class UMLRelation {
    private double XpositionOfRelationStart;
    private double XpositionOfRelationEnd;
    private double YpositionOfRelationStart;
    private double YpositionOfRelationEnd;
    private List<UMLClass> classes;
    private UMLClass classA;
    private UMLClass classB;

    private List<UMLInterface> interfaces;
    private UMLInterface interfaceA;
    private UMLInterface interfaceB;

    private String name;

    public UMLRelation(String classA, String classB, String name){
        this.name = name;
        this.classes = new ArrayList<>();
        this.interfaces = new ArrayList<>();

        if (classDiagram.findClass(classA) != null){
            this.XpositionOfRelationStart = classDiagram.findClass(classA).getXposition();
            this.YpositionOfRelationStart = classDiagram.findClass(classA).getYposition();
            this.classA = classDiagram.findClass(classA);
            this.classes.add(classDiagram.findClass(classA));
        }
        else{
            this.XpositionOfRelationStart = classDiagram.findInterface(classA).getXposition();
            this.YpositionOfRelationStart = classDiagram.findInterface(classA).getYposition();
            this.interfaceA = classDiagram.findInterface(classA);
            this.interfaces.add(classDiagram.findInterface(classA));
        }
        if (classDiagram.findClass(classB) != null){
            this.XpositionOfRelationEnd = classDiagram.findClass(classB).getXposition();
            this.YpositionOfRelationEnd = classDiagram.findClass(classB).getYposition();
            this.classB = classDiagram.findClass(classB);
            this.classes.add(classDiagram.findClass(classB));
        }
        else{
            this.XpositionOfRelationStart = classDiagram.findInterface(classB).getXposition();
            this.YpositionOfRelationStart = classDiagram.findInterface(classB).getYposition();
            this.interfaceB = classDiagram.findInterface(classB);
            this.interfaces.add(classDiagram.findInterface(classB));
        }
    }

    public UMLClass getFirstClass(){
        return classA;
    }

    public UMLInterface getFirstInterface(){
        return interfaceA;
    }

    public UMLClass getSecondClass(){
        return classB;
    }

    public UMLInterface getSecondInterface(){
        return interfaceB;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setStartPositopn(double xpositionOfRelationStart, double ypositionOfRelationStart){
        XpositionOfRelationStart = xpositionOfRelationStart;
        YpositionOfRelationStart = ypositionOfRelationStart;
    }

    public void setEndPositopn(double xpositionOfRelationEnd, double ypositionOfRelationEnd){
        XpositionOfRelationEnd = xpositionOfRelationEnd;
        YpositionOfRelationEnd = ypositionOfRelationEnd;
    }

    public List<UMLClass> getClasses(){
        return classes;
    }

    public List<UMLInterface> getInterfaces(){
        return interfaces;
    }

    public double getXpositionOfRelationStart() {
        return XpositionOfRelationStart;
    }

    public void setXpositionOfRelationStart(double xpositionOfRelationStart) {
        XpositionOfRelationStart = xpositionOfRelationStart;
    }

    public double getXpositionOfRelationEnd() {
        return XpositionOfRelationEnd;
    }

    public void setXpositionOfRelationEnd(double xpositionOfRelationEnd) {
        XpositionOfRelationEnd = xpositionOfRelationEnd;
    }

    public double getYpositionOfRelationStart() {
        return YpositionOfRelationStart;
    }

    public void setYpositionOfRelationStart(double ypositionOfRelationStart) {
        YpositionOfRelationStart = ypositionOfRelationStart;
    }

    public double getYpositionOfRelationEnd() {
        return YpositionOfRelationEnd;
    }

    public void setYpositionOfRelationEnd(double ypositionOfRelationEnd) {
        YpositionOfRelationEnd = ypositionOfRelationEnd;
    }
}
