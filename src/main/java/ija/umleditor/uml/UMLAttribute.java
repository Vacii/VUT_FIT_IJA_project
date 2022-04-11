package ija.umleditor.uml;

/**
 * Třída UMLAttribute reprezentuje atribut, který má své jméno a typ. Je odvozena (rozšiřuje) od třídy Element.
 * Typ atributu je reprezentován třidou UMLClassifier. Lze použít jako atribut UML třídy nebo argument operace.
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class UMLAttribute extends Element {
    private UMLClassifier type;

// ----- Constructors -----

/**
 *  UMLAttribute(Název diagramu, Classifier)
 *  Vytvoří instanci atributu.
 *  @param name název diagramu
 *  @param type typ
 */
    public UMLAttribute(java.lang.String name, UMLClassifier type) {
        super(name);
        this.type = type;
    }

//  ----- Methods -----

/**
 *  getType()
 *  Poskytuje informaci o typu atributu.
 *  @return typ atributu
 */
    public UMLClassifier getType() {
        return this.type;
    }


/**
 *  toString()
 *  @return řetězec reprezentující stav atributu v podobě "nazev:typ".
 */
    public java.lang.String toString() {
        return String.format("%s:%s", this.getName(), this.type);
    }
}
