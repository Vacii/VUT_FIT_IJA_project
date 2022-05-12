package ija.umleditor.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SequenceDiagram extends Element{
    private List<UMLClass> tridy;
    private List<UMLClassifier> klasifikatory;
    private List<UMLMessage> zpravy;

    private boolean initialized;
    private boolean isOpened;
    private int msgCounter;

    public SequenceDiagram(String name) {
        super(name);
        this.tridy = new ArrayList<>();
        this.zpravy= new ArrayList<>();
        this.klasifikatory = new ArrayList<>();
        this.initialized = false;
        this.isOpened = false;
        this.msgCounter = 0;
    }

    public boolean addClass(UMLClass classA) {
        if (tridy.contains(classA)){
            return false;
        }
        this.tridy.add(classA);
        this.initialized = true;
        return true;
    }

    public UMLMessage createMessage(String name, String class1, String class2, String type, String operation) {
        UMLMessage message = new UMLMessage(name, class1, class2, type, operation);
        zpravy.add(message);
        return message;
    }

    public boolean removeCleass(UMLClass classA){
        if (tridy.contains(classA)){
            tridy.remove(classA);
            return true;
        }
        return false;
    }

    public UMLClass findClass(String name){
        UMLClass obj;
        for (int i = 0; i < this.tridy.size(); i++) {
            obj = this.tridy.get(i);
            if (obj.getName().compareTo(name) == 0) {
                return obj;
            }
        }
        return null;
    }


    public List<UMLMessage> getMessages(){
        return this.zpravy;
    }

    public List<UMLClass> getClasses(){
        return this.tridy;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public void incrementMsgCount(){
        this.msgCounter++;
    }

    public int getMsgCouner(){
        return this.msgCounter;
    }
}
