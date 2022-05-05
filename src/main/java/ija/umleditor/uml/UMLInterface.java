package ija.umleditor.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UMLInterface extends UMLClassifier{
    private List<UMLOperation> operace;
    private List<UMLRelation> relace;
    private double posX;
    private double posY;

// ----- Constructors -----

    /**
     * UMLInterface(Název třídy (klasifikátoru))
     * Vytvoří instanci reprezentující model rozhraní z jazyka UML
     *
     * @param name název třídy
     */
    public UMLInterface(java.lang.String name) {
        super(name);
        this.operace = new ArrayList<>();
        this.relace = new ArrayList<>();
        this.posX = 20.0;
        this.posY = 20.0;
    }

//  ----- Methods -----

    public boolean addMethod(UMLOperation operation) {

        if (this.operace.contains(operation))
            return false;

        this.operace.add(operation);
        return true;
    }

    public boolean addRelation(UMLRelation relace){
        if (this.relace.contains(relace))
            return false;

        this.relace.add(relace);
        return true;
    }

    public List<UMLOperation> getMethods() {

        return Collections.unmodifiableList(this.operace);
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
    public String getInterfaceName() {
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
}
