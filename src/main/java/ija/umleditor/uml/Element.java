package ija.homework1.uml;

// Třída reprezentuje pojmenovaný element (thing), který může být součástí jakékoliv části v diagramu.
public class Element extends java.lang.Object {
    private String name;

// ----- Constructors -----

    /*
        Element(Název elementu)
        Vytvoří instanci se zadaným názvem.
     */
    public Element(java.lang.String name) {
        this.name = name;
    }

//  ----- Methods -----

    /*
        getName()
        Vrátí název elementu.
     */
    public java.lang.String getName() {
        return this.name;
    }

    /*
        rename(Nový název elementu)
        Přejmenuje element.
     */
    public void rename(java.lang.String newName) {
        this.name = newName;
    }
}