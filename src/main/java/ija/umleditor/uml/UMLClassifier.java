package ija.umleditor.uml;

//Třída reprezentuje klasifikátor v diagramu.
//Odvozené třídy reprezentují konkrétní podoby klasifikátoru (třída, rozhraní, atribut, apod.)
public class UMLClassifier extends Element {
    private boolean definovane;

// ----- Constructors -----

    /*
        UMLClassifier(Název klasifikátoru)
        Vytvoří instanci třídy Classifier. Instance je uživatelsky definována (je součástí diagramu).
     */
    public UMLClassifier(java.lang.String name) {
        super(name);
        this.definovane = true;
    }

    /*
        UMLClassifier(Název klasifikátoru, Uživatelsky definován (součástí diagramu))
        Vytvoří instanci třídy Classifier.
     */
    public UMLClassifier(java.lang.String name, boolean isUserDefined) {
        super(name);
        this.definovane = isUserDefined;
    }


    //  ----- Methods -----
/*
    forName(Název klasifikátoru)
    Tovární metoda pro vytvoření instance třídy Classifier pro zadané jméno.
    Instance reprezentuje klasifikátor, který není v diagramu modelován.
*/
    public static UMLClassifier forName(java.lang.String name) {
        return new UMLClassifier(name, false);
    }


    /*
        isUserDefined()
        Zjišťuje, zda objekt reprezentuje klasifikátor, který je modelován uživatelem v diagramu nebo ne.
    */
    public boolean isUserDefined() {
        return this.definovane;
    }

    /*
        toString()
        Vrací řetězec reprezentující klasifikátor v podobě "nazev(userDefined)", kde userDefined je true nebo false.
    */
    @Override
    public java.lang.String toString() {
        return String.format("%s(%b)", this.getName(), this.definovane);
    }
}
