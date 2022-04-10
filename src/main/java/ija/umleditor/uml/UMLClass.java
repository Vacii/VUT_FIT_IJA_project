package ija.homework1.uml;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

//Třída (její instance) reprezentuje model třídy z jazyka UML. Rozšiřuje třídu UMLClassifier.
//Obsahuje seznam atributů a operací (metod). Třída může být abstraktní.
public class UMLClass extends UMLClassifier {
    private boolean abstraktni;
    private List<UMLAttribute> atributy;

// ----- Constructors -----

    /*
        UMLClass(Název třídy (klasifikátoru))
        Vytvoří instanci reprezentující model třídy z jazyka UML. Třída není abstraktní.
     */
    public UMLClass(java.lang.String name) {
        super(name);
        this.atributy = new ArrayList<>();
        this.abstraktni = false;
    }

//  ----- Methods -----

    /*
        addAttribute(Vkládaný atribut)
        Vloží atribut do modelu UML třídy. Atribut se vloží na konec seznamu (poslední položka).
        Pokud již třída obsahuje atribut stejného jména, nedělá nic.
     */
    public boolean addAttribute(UMLAttribute attr) {
        if (this.atributy.contains(attr)) {
            return false;
        }
        this.atributy.add(attr);
        return true;
    }

    /*
        getAttributes()
        Vrací nemodifikovatelný seznam atributů.
        Lze využít pro zobrazení atributů třídy.
    */
    public List<UMLAttribute> getAttributes() {
        return Collections.unmodifiableList(this.atributy);
    }

    /*
        getAttrPosition(Hledaný atribut)
        Vrací pozici atributu v seznamu atributů. Pozice se indexuje od hodnoty 0.
        Pokud třída daný atribut neobsahuje, vrací -1.
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

    /*
        abstraktni()
        Test, zda objekt reprezentuje model abstraktní třídy.
    */
    public boolean isAbstract() {
        return this.abstraktni;
    }

    /*
        moveAttrAtPosition(Přesunovaný atribut, Nová pozice)
        Přesune pozici atributu na nově zadanou. Pozice se indexuje od hodnoty 0.
        Pokud třída daný atribut neobsahuje, nic neprovádí a vrací -1.
        Při přesunu na pozici pos se všechny stávající položky (atributy) od pozice pos (včetně) posunou o jednu pozici doprava.
    */
    public int moveAttrAtPosition(UMLAttribute attr, int pos) {
        if (!this.atributy.contains(attr)) {
            return -1;
        }
        this.atributy.remove(attr);
        this.atributy.add(pos, attr);
        return pos;
    }

    /*
        setAbstract(Zda se jedná o abstraktní třídu nebo ne)
        Změní informaci objektu, zda reprezentuje abstraktní třídu.
    */
    public void setAbstract(boolean isAbstract) {
        this.abstraktni = isAbstract;
    }
}