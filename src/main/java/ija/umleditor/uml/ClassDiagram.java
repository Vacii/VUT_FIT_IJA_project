package ija.umleditor.uml;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * ClassDiagram reprezentuje diagram tříd.
 * Obsahuje seznam tříd (instance třídy UMLClass) příp. klasifikátorů pro uživatelsky nedefinované typy (instance třídy UMLClassifier).
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class ClassDiagram extends Element {
    private List<UMLClassifier> klasifikatory;
    private List<UMLClass> tridy;

// ----- Constructors -----

/**
 *  ClassDiagram(Název diagramu)
 *  Konstruktor pro vytvoření instance diagramu. Každý diagram má svůj název name.
 *  @param name nastavuje jméno třídy
 */
    public ClassDiagram(java.lang.String name) {
        super(name);
        this.klasifikatory = new ArrayList<>();
        this.tridy = new ArrayList<>();
    }

//  ----- Methods -----

/**
 *  classifierForName(Název klasifikátoru)
 *  Vyhledá v diagramu klasifikátor podle názvu.
 *  Pokud neexistuje, vytvoří instanci třídy Classifier reprezentující klasifikátor, který není v diagramu zachycen.
 *  @param name hledané jméno klasifikátoru
 *  @return nalezený klasifikátor
 */
    public UMLClassifier classifierForName(java.lang.String name) {
        if (findClassifier(name) == null) {
            UMLClassifier klas = UMLClassifier.forName(name);
            klasifikatory.add(klas);
        }
        return findClassifier(name);
    }

/**
 *  createClass(Název vytvářené třídy)
 *  Vytvoří instanci UML třídy a vloží ji do diagramu. Pokud v diagramu již existuje třída stejného názvu, nedělá nic.
 *  @param name jméno pro novou třídu
 *  @return nová třída
 */
    public UMLClass createClass(java.lang.String name) {

        UMLClass obj;
        for (int i = 0; i < this.tridy.size(); i++) {
            obj = this.tridy.get(i);
            if (obj.getName().compareTo(name) == 0) {
                return null;
            }
        }
        UMLClass klas = new UMLClass(name);
        this.klasifikatory.add(klas);
        this.tridy.add(klas);
        return klas;
    }

    /**
     * findClass(String name)
     * Vyhledá třídu podle jména
     * @param name jméno hledané třídy
     * @return hledanou třídu pokud nalezena, v opačném případě null
     */
    public UMLClass findClass(String name){
        for (int i = 0; i < tridy.size(); i++){
            if (tridy.get(i).getClassName() == name){
                return tridy.get(i);
            }
        }
        return null;
    }

/**
 *  findClassifier(Název klasifikátoru)
 *  Vyhledá v diagramu klasifikátor podle názvu.
 *  @param name jméno hledaného klasifikátoru
 *  @return hledaný klasifikátor
 */
    public UMLClassifier findClassifier(java.lang.String name) {
        for (UMLClassifier umlClassifier : klasifikatory) {
            if (Objects.equals(umlClassifier.getName(), name)) {
                return umlClassifier;
            }
        }
        return null;
    }

    public void deleteClass(UMLClass aClass) {
        this.klasifikatory.remove(aClass);
        this.tridy.remove(aClass);
    }
}