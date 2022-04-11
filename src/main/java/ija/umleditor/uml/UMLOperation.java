package ija.umleditor.uml;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Třída UMLOperation reprezentuje operaci, která má své jméno, návratový typ a seznam argumentů.
 * Je odvozena (rozšiřuje) od třídy UMLAttribute, od které přejímá název a návratový typ.
 * Argument je reprezentován třídou UMLAttribute. Lze použít jako součást UML klasifikátoru třída nebo rozhraní.
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class UMLOperation extends UMLAttribute {
    private List<UMLAttribute> argList;

// ----- Constructors -----


/**
 * UMLOperation(Název operace, Návratový typ operace)
 * Konstruktor pro vytvoření operace s daným názvem a návratovým typem.
 * @param name jméno nové operace
 * @param type typ operace
 */
    public UMLOperation(java.lang.String name, UMLClassifier type) {
        super(name, type);
        argList = new ArrayList<>();
    }

//  ----- Methods -----


/**
 * addArgument(Vkládaný argument)
 * Přidá nový argument do seznamu argumentů. Argument se vloží na konec seznamu.
 * Pokud v seznamu již existuje argument stejného názvu, operaci neprovede.
 * @param arg nový argument
 * @return true/false jestli se přidal, nebo ne
 */
    public boolean addArgument(UMLAttribute arg) {
        if (this.argList.contains(arg))
            return false;
        return this.argList.add(arg);
    }

/**
 * create(Název operace, Návratový typ operace, Seznam argumentů operace)
 * Tovární metoda pro vytvoření instance operace.
 * @param name název operace
 * @param type typ operace
 * @param args seznam argumentů operace
 * @return nová operace
 */
    public static UMLOperation create(java.lang.String name, UMLClassifier type, UMLAttribute... args) {
        UMLOperation operace = new UMLOperation(name, type);
        int n = 0;
        while (args.length > n) {
            operace.addArgument(args[n]);
            n++;
        }
        n = -1;
        return operace;
    }

/**
 * getArguments()
 * Lze využít pro zobrazení.
 * @return nemodifikovatelný seznam argumentů
 */
    public List<UMLAttribute> getArguments() {
        return Collections.unmodifiableList(this.argList);
    }
}
