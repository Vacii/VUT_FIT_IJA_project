package ija.umleditor.uml;

import ija.umleditor.uml.UMLClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UMLRelation {
    private double XpositionOfRelationStart;
    private double XpositionOfRelationEnd;
    private double YpositionOfRelationStart;
    private double YpositionOfRelationEnd;
    private List<UMLClass> classes;
    private UMLClass classA;
    private UMLClass classB;
    private String name;

    public UMLRelation(UMLClass classA, UMLClass classB, String name){
        this.name = name;
        this.classes = new ArrayList<>();
        this.XpositionOfRelationStart = classA.getXposition();
        this.YpositionOfRelationStart = classA.getYposition();
        this.XpositionOfRelationEnd = classB.getXposition();
        this.YpositionOfRelationEnd = classB.getYposition();
        this.classA = classA;
        this.classB = classB;
        classes.add(classA);
        classes.add(classB);
        classA.setRelation(this);
        classB.setRelation(this);
    }

    public UMLClass getFirstClass(){
        return classA;
    }

    public UMLClass getSecondClass(){
        return classB;
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
