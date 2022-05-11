package ija.umleditor.uml;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída UMLClass (její instance) reprezentuje model třídy z jazyka UML. Rozšiřuje třídu UMLClassifier.
 * Obsahuje seznam atributů a operací (metod). Třída může být abstraktní.
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class UMLClass extends UMLClassifier {
    private boolean abstraktni;
    private List<UMLAttribute> atributy;
    private List<String> jmenaAtributu;
    private List<UMLOperation> operace;
    private List<String> jmenaOperaci;
    private List<UMLRelation> relace;
    private List<Double> heightsOfCom;
    private List<Double> posOfCom;
    private double posX;
    private double posY;
    private double seqPos;

// ----- Constructors -----

    /**
     * UMLClass(Název třídy (klasifikátoru))
     * Vytvoří instanci reprezentující model třídy z jazyka UML. Třída není abstraktní.
     *
     * @param name název třídy
     */
    public UMLClass(java.lang.String name) {
        super(name);
        this.atributy = new ArrayList<>();
        this.jmenaAtributu = new ArrayList<>();
        this.operace = new ArrayList<>();
        this.jmenaOperaci = new ArrayList<>();
        this.relace = new ArrayList<>();
        this.heightsOfCom = new ArrayList<>();
        this.posOfCom = new ArrayList<>();
        this.abstraktni = false;
        this.posX = 70.0;
        this.posY = 70.0;
        this.seqPos = 0.0;
    }

//  ----- Methods -----

    /**
     * addAttribute(Vkládaný atribut)
     * Vloží atribut do modelu UML třídy. Atribut se vloží na konec seznamu (poslední položka).
     * Pokud již třída obsahuje atribut stejného jména, nedělá nic.
     *
     * @param attr přidávaný atribut
     * @return true/false jestli atribut byl vložen, nebo ne
     */
    public boolean addAttribute(UMLAttribute attr) {
        if (this.atributy.contains(attr)) {
            return false;
        }
        this.atributy.add(attr);
        this.jmenaAtributu.add(attr.getName());
        return true;
    }


    public boolean addMethod(UMLOperation operation) {

        if (this.operace.contains(operation)) {
            return false;
        }

        this.operace.add(operation);
        this.jmenaOperaci.add(operation.getName());
        return true;
    }

    public boolean addRelation(UMLRelation relace){
        if (this.relace.contains(relace)){
            return false;
        }
        this.relace.add(relace);
        return true;
    }

    public List<UMLOperation> getMethods() {

        return Collections.unmodifiableList(this.operace);
    }

    public List<String> getNamesOfMethods(){
        return this.jmenaOperaci;
    }

    /**
     * getAttributes()
     *
     * @return nemodifikovatelný seznam atributů.
     */
    public List<UMLAttribute> getAttributes() {
        return Collections.unmodifiableList(this.atributy);
    }

    public List<String> getNamesOfAttributes(){
        return this.jmenaAtributu;
    }

    /**
     * getAttrPosition(Hledaný atribut)
     * Vrací pozici atributu v seznamu atributů. Pozice se indexuje od hodnoty 0.
     * Pokud třída daný atribut neobsahuje, vrací -1.
     *
     * @param attr hledaný atribut
     * @return pozice atributu v seznamu atributů
     */
    public int getAttrPosition(UMLAttribute attr) {
        int n = 0;
        for (UMLAttribute att : this.atributy) {
            if (att.equals(attr)) {
                return n;
            }
            n++;
        }
        return -1;
    }

    /**
     * isAbstract()
     * Test, zda objekt reprezentuje model abstraktní třídy.
     *
     * @return true/false toho, zda je objekt abstraktní
     */
    public boolean isAbstract() {
        return this.abstraktni;
    }

    /**
     * moveAttrAtPosition(Přesunovaný atribut, Nová pozice)
     * Přesune pozici atributu na nově zadanou. Pozice se indexuje od hodnoty 0.
     * Pokud třída daný atribut neobsahuje, nic neprovádí a vrací -1.
     * Při přesunu na pozici pos se všechny stávající položky (atributy) od pozice pos (včetně) posunou o jednu pozici doprava.
     *
     * @param attr atribut, který se má přesunout
     * @param pos  pozice, na kterou se má atribut přesunout
     * @return nová pozice
     */
    public int moveAttrAtPosition(UMLAttribute attr, int pos) {
        if (!this.atributy.contains(attr)) {
            return -1;
        }
        this.atributy.remove(attr);
        this.atributy.add(pos, attr);
        return pos;
    }

    /**
     * setAbstract(Zda se jedná o abstraktní třídu nebo ne)
     * Změní informaci objektu, zda reprezentuje abstraktní třídu.
     *
     * @param isAbstract informace zda se má nastavit na abstraktni, nebo neabstraktni
     */
    public void setAbstract(boolean isAbstract) {
        this.abstraktni = isAbstract;
    }

    /**
     * getXposition(String name)
     *
     * @return X pozice třídy
     */
    public double getXposition() {
        return this.posX;
    }

    /**
     * getYposition(String name)
     *
     * @return Y pozice třídy
     */
    public double getYposition() {
        return this.posY;
    }

    public double getSeqPos(){
        return this.seqPos;
    }

    /**
     * setXposition(double position)
     * Setter pro X pozici tridy
     *
     * @param position nova X pozice
     */
    public void setXposition(double position) {
        this.posX = position;
    }

    /**
     * setXposition(double position)
     * Setter pro Y pozici tridy
     *
     * @param position nova Y pozice
     */
    public void setYposition(double position) {
        this.posY = position;
    }

    public void setSeqPos(double position){
        this.seqPos = position;
    }

    /**
     * getClassName()
     *
     * @return getter pro jmémo třídy
     */
    public String getClassName() {
        return this.getName();
    }


    public Boolean removeMethod(UMLOperation operation) {
        for (int i = 0; i < this.operace.size(); i++) {
            if (this.operace.get(i) == operation) {
                this.operace.remove(operation);
                return true;
            }
        }
        return false;
    }

    public Boolean removeAttribute(UMLAttribute attribute) {
        for (int i = 0; i < this.atributy.size(); i++) {
            if (this.atributy.get(i) == attribute) {
                this.atributy.remove(attribute);
                return true;
            }
        }
        return false;
    }

    public List<UMLRelation> getRelations(){
        return this.relace;
    }

    public void removeReltion(UMLRelation relation){
        this.relace.remove(relation);
    }

    public List<Double> getHeightsOfCom() {
        return heightsOfCom;
    }

    public void addHeight(double height) {
        this.heightsOfCom.add(height);
    }

    public void removeHeightsOfCom(){
        this.heightsOfCom.removeAll(getHeightsOfCom());
    }

    public List<Double> getPosOfCom() {
        return posOfCom;
    }

    public void addPosOfCom(double pos) {
        this.posOfCom.add(pos);
    }

    public void setPositionOfCom(int index,double pos){
        this.posOfCom.add(index,pos);
        this.posOfCom.remove(index + 1);
    }
}