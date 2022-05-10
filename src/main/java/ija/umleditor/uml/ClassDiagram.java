package ija.umleditor.uml;

import java.nio.file.FileSystemLoopException;
import java.util.Collections;
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
    private List<UMLInterface> rozhrani;
    private List<UMLRelation> relace;
    private List<SequenceDiagram> sequenceDiagrams;

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
        this.rozhrani = new ArrayList<>();
        this.relace = new ArrayList<>();
        this.sequenceDiagrams = new ArrayList<>();
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
        if (testPresence(name)){
            return null;
        }
        UMLClass klas = new UMLClass(name);
        this.klasifikatory.add(klas);
        this.tridy.add(klas);
        return klas;
    }

    private boolean testPresence(String name){
        //TODO jestli bude cas, mrknout jestli nebude lepsi predelat
        UMLClass obj;
        UMLInterface objIn;
        for (int i = 0; i < this.tridy.size(); i++) {
            obj = this.tridy.get(i);
            if (obj.getName().compareTo(name) == 0) {
                return true;
            }
        }
        for (int i = 0; i < this.rozhrani.size(); i++) {
            objIn = this.rozhrani.get(i);
            if (objIn.getName().compareTo(name) == 0) {
                return true;
            }
        }
        return false;
    }

    public UMLInterface createInterface(java.lang.String name) {
        if (testPresence(name)){
            return null;
        }
        UMLInterface inter = new UMLInterface(name);
        this.rozhrani.add(inter);
        this.klasifikatory.add(inter);
        return inter;
    }

    public UMLRelation createRelation(String classA, String classB, String name, String type){
        UMLRelation obj;
        for (int i = 0; i < this.relace.size(); i++) {
            obj = this.relace.get(i);
            if (obj.getName().compareTo(name) == 0) {
                return null;
            }
        }
        UMLRelation rel = new UMLRelation(classA, classB, name, type);
        if(findClass(classA) != null){
            findClass(classA).addRelation(rel);
        }
        else{
            findInterface(classA).addRelation(rel);
        }
        if(findClass(classB) != null){
            findClass(classB).addRelation(rel);
        }
        else{
            findInterface(classB).addRelation(rel);
        }

        this.relace.add(rel);
        return rel;
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

    public UMLInterface findInterface(String name){
        for (int i = 0; i < rozhrani.size(); i++){
            if (rozhrani.get(i).getInterfaceName() == name){
                return rozhrani.get(i);
            }
        }
        return null;
    }

    public List<UMLClass> getClasses() {return Collections.unmodifiableList(this.tridy);}

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

    public void deleteInterface(UMLInterface aInterface) {
        this.klasifikatory.remove(aInterface);
        this.rozhrani.remove(aInterface);
    }

    public List<UMLRelation> getClassRelations(String name) {
        return findClass(name).getRaletions();
    }

    public List<UMLRelation> getInterfaceRelations(String name) {
        return findInterface(name).getRelations();
    }

    public UMLRelation findRelation(String classA, String classB){
        for (int i = 0; i < relace.size(); i++){
            if (relace.get(i).getFirstClass() != null){
                if (relace.get(i).getSecondClass() != null){
                    if(relace.get(i).getFirstClass().getName().equals(classA) && relace.get(i).getSecondClass().getName().equals(classB)){
                        return relace.get(i);
                    }
                }
                else {
                    if(relace.get(i).getFirstClass().getName().equals(classA) && relace.get(i).getSecondInterface().getName().equals(classB)){
                        return relace.get(i);
                    }
                }
            }
            else if(relace.get(i).getFirstInterface() != null){
                if (relace.get(i).getSecondInterface() != null){
                    if (relace.get(i).getFirstInterface().getName().equals(classA) && relace.get(i).getSecondInterface().getName().equals(classB)){
                        return relace.get(i);
                    }
                }
                else {
                    if (relace.get(i).getFirstInterface().getName().equals(classA) && relace.get(i).getSecondClass().getName().equals(classB)){
                        return relace.get(i);
                    }
                }
            }
        }
        return null;
    }

    public void deleteRelation(UMLRelation relation){
        this.relace.remove(relation);
        if (relation.getFirstClass() != null){
            relation.getFirstClass().removeReltion(relation);
        } else{
            relation.getFirstInterface().removeReltion(relation);
        }

        if (relation.getSecondClass() != null){
            relation.getSecondClass().removeReltion(relation);
        } else {
            relation.getSecondInterface().removeReltion(relation);
        }
    }

    public SequenceDiagram createSeqDiagram(String name) {
        SequenceDiagram obj;
        for (int i = 0; i < this.sequenceDiagrams.size(); i++) {
            obj = this.sequenceDiagrams.get(i);
            if (obj.getName().compareTo(name) == 0) {
                return null;
            }
        }
        SequenceDiagram seq = new SequenceDiagram(name);
        this.sequenceDiagrams.add(seq);
        return seq;
    }

    public SequenceDiagram findSeqDiagram(String name){
        for (int i = 0; i < this.sequenceDiagrams.size(); i++){
            if (sequenceDiagrams.get(i).getName().equals(name)){
                return sequenceDiagrams.get(i);
            }
        }
        return null;
    }

    public void removeSeqDiagram(String name){
        this.sequenceDiagrams.remove(findSeqDiagram(name));
    }
}