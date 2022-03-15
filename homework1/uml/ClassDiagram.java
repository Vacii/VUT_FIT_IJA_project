package ija.homework1.uml;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

//Třída reprezentuje diagram tříd. Je odvozen od třídy Element (má název).
//Obsahuje seznam tříd (instance třídy UMLClass) příp. klasifikátorů pro uživatelsky nedefinované typy (instance třídy UMLClassifier).
public class ClassDiagram extends Element{
    private List<UMLClassifier> klasifikatory;
    private List<UMLClass> tridy;

// ----- Constructors -----

/*
    ClassDiagram(Název diagramu)
    Konstruktor pro vytvoření instance diagramu. Každý diagram má svůj název name.
 */
    public ClassDiagram(java.lang.String name){
        super(name);
        this.klasifikatory = new ArrayList<>();
        this.tridy = new ArrayList<>();
    }

//  ----- Methods -----

/*
    classifierForName(Název klasifikátoru)
    Vyhledá v diagramu klasifikátor podle názvu.
    Pokud neexistuje, vytvoří instanci třídy Classifier reprezentující klasifikátor, který není v diagramu zachycen.
 */
    public UMLClassifier classifierForName(java.lang.String name){
        if (findClassifier(name) == null){
            UMLClassifier klas = UMLClassifier.forName(name);
            klasifikatory.add(klas);
        }
        return findClassifier(name);
    }

/*
    createClass(Název vytvářené třídy)
    Vytvoří instanci UML třídy a vloží ji do diagramu. Pokud v diagramu již existuje třída stejného názvu, nedělá nic.
 */
    public UMLClass createClass(java.lang.String name){
        UMLClass klas = new UMLClass(name);

        if (!this.tridy.contains(klas)){
            this.klasifikatory.add(klas);
            this.tridy.add(klas);
            return klas;
        }
        return null;
    }

/*
    findClassifier(Název klasifikátoru)
    Vyhledá v diagramu klasifikátor podle názvu.
*/
    public UMLClassifier findClassifier(java.lang.String name){
        for (UMLClassifier umlClassifier : klasifikatory) {
            if (Objects.equals(umlClassifier.getName(), name)){
                return umlClassifier;
            }
        }
        return null;
    }
}