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
    private List<UMLOperation> operace;
    private double posX;
    private double posY;

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
        this.operace = new ArrayList<>();
        this.abstraktni = false;
        this.posX = 20.0;
        this.posY = 20.0;
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
        return true;
    }


    public boolean addMethod(UMLOperation operation) {

        if (this.operace.contains(operation)) return false;

        this.operace.add(operation);
        return true;
    }

    public List<UMLOperation> getMethods() {

        return Collections.unmodifiableList(this.operace);
    }

    /**
     * getAttributes()
     *
     * @return nemodifikovatelný seznam atributů.
     */
    public List<UMLAttribute> getAttributes() {
        return Collections.unmodifiableList(this.atributy);
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

}