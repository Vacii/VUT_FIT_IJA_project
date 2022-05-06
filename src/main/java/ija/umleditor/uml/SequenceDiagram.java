package ija.umleditor.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SequenceDiagram extends Element{
    private List<UMLClass> tridy;
    private List<UMLClassifier> klasifikatory;
    private List<UMLClass> zpravy;

    public SequenceDiagram(String name) {
        super(name);
        this.tridy = new ArrayList<>();
        this.zpravy= new ArrayList<>();
    }

    public boolean addClass(UMLClass classA) {
        if (tridy.contains(classA)){
            return false;
        }
        tridy.add(classA);
        return true;
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
}
