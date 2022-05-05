package ija.umleditor.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SequenceDiagram extends Element{
    private List<UMLClass> tridy;
    private List<UMLClass> zpravy;

    public SequenceDiagram(String name) {
        super(name);
        this.tridy = new ArrayList<>();
        this.zpravy= new ArrayList<>();
    }
}
