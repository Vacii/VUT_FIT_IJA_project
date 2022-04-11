package ija.umleditor.uml;
/**
 * Třída Element reprezentuje pojmenovaný element (thing),
 * který může být součástí jakékoliv části v diagramu.
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class Element extends java.lang.Object {
    private String name;

// ----- Constructors -----

/**
 *  Element(Název elementu)
 *  Vytvoří instanci se zadaným názvem.
 *  @param name nastavuje jméno elementu
 */
    public Element(java.lang.String name) {
        this.name = name;
    }

//  ----- Methods -----

/**
 *  getName()
 *  Získá jméno elementu.
 *  @return jméno elementy
 */
    public java.lang.String getName() {
        return this.name;
    }

    /**
     *  rename(Nový název elementu)
     *  Přejmenuje element.
     *  @param newName přejmenuje element
     */
    public void rename(java.lang.String newName) {
        this.name = newName;
    }
}