/*
 * IJA 2021/22: Úloha č. 1
 * Testovací třída pro JUnit.
 * (C) rk
 */
package ija.homework1;

import ija.homework1.uml.UMLClassifier;
import ija.homework1.uml.ClassDiagram;
import ija.homework1.uml.Element;
import ija.homework1.uml.UMLClass;
import ija.homework1.uml.UMLAttribute;
import ija.homework1.uml.UMLOperation;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testovací třída pro první úkol z předmětu IJA (2021/22).
 * Testovací případy nemusí pokrývat celou funkcionalitu požadovanou API a kontrakty metod v zadání.
 * @author koci
 */
public class IJAHomework1Test {
    
    /**
     * Test vytvoření uživatelsky definované UML třídy (instance třídy UMLClass) 
     * a neuživatelsky definovaného typu (instance třídy UMLClassifier).
     */
    @Test
    public void testUMLClass() {
        UMLClassifier cls1 = new UMLClass("C1");
        UMLClassifier cls2 = UMLClassifier.forName("int");
        
        Assert.assertTrue("Trida C1 je uzivatelsky definovana.", cls1.isUserDefined());
        Assert.assertFalse("Typ int neni uzivatelsky definovany.", cls2.isUserDefined());

        UMLClass cls3 = new UMLClass("C2");
        Assert.assertFalse("Trida C2 neni abstraktni.", cls3.isAbstract());
        cls3.setAbstract(true);
        Assert.assertTrue("Trida C2 je abstraktni.", cls3.isAbstract());
    }
    
    /**
     * Test vytvoření diagramu a klasifikátorů a ověření jejich přítomnosti v diagramu.
     */
    @Test
    public void testUMLClassifiers() {
        ClassDiagram d = new ClassDiagram("My model");
        UMLClass cls = d.createClass("C1");

        Assert.assertEquals("Nazev diagramu trid.", "My model", d.getName());
        Assert.assertNull("Klasifikator s nazvem C0 neni v diagramu", d.findClassifier("C0"));
        Assert.assertEquals("Klasifikator s nazvem C1 je v diagramu", cls, d.findClassifier("C1"));

        Assert.assertNull("Klasifikator s nazvem int neni v diagramu", d.findClassifier("int"));
        UMLClassifier clsInt1 = d.classifierForName("int");
        UMLClassifier clsInt2 = d.findClassifier("int");
        Assert.assertNotNull("Klasifikator s nazvem int je v diagramu", clsInt2);
        Assert.assertSame("Vytvoreny a nalezeny klasifikator je stejny (identicky)", clsInt1, clsInt2);

        d.classifierForName("int");
        UMLClassifier clsInt3 = d.findClassifier("int");
        Assert.assertSame("Novy klasifikator pro int nevznikne, pouzije se jiz vytvoreny.",
                clsInt1, clsInt3);

        UMLAttribute attr = new UMLAttribute("c", d.classifierForName("C1"));
        Assert.assertSame("Novy klasifikator pro C1 nevznikne, pouzije se jiz vytvoreny.",
                cls, attr.getType());
    }

    /**
     * Test práce s atributy v třídě - vložení a změna pozice.
     */
    @Test
    public void testAttributes() {
        ClassDiagram d = new ClassDiagram("My model");
        UMLClass cls = d.createClass("C1");
        UMLAttribute attr1 = new UMLAttribute("count", d.classifierForName("int"));
        UMLAttribute attr2 = new UMLAttribute("id", d.classifierForName("int"));
        UMLAttribute attr3 = new UMLAttribute("c", d.classifierForName("C1"));
        UMLAttribute attr4 = new UMLAttribute("none", d.classifierForName("C1"));

        cls.addAttribute(attr1);
        cls.addAttribute(attr2);
        cls.addAttribute(attr3);

        Assert.assertEquals("Pozice atributu count je 0.", 0, cls.getAttrPosition(attr1));
        Assert.assertEquals("Pozice atributu id je 1.", 1, cls.getAttrPosition(attr2));
        Assert.assertEquals("Pozice atributu c je 2.", 2, cls.getAttrPosition(attr3));
        Assert.assertEquals("Presun pozice atributu none na prvni misto (index 0) - nezdari se (none neni atribut C1).",
                -1, cls.moveAttrAtPosition(attr4, 0));
        Assert.assertEquals("Presun pozice atributu id na prvni misto (index 0).",
                0, cls.moveAttrAtPosition(attr2, 0));

        Assert.assertEquals("Pozice atributu count je 1.", 1, cls.getAttrPosition(attr1));
        Assert.assertEquals("Pozice atributu id je 0.", 0, cls.getAttrPosition(attr2));
        Assert.assertEquals("Pozice atributu c je 2.", 2, cls.getAttrPosition(attr3));
    }

    /**
     * Test práce s atributy - zíkání seznamu atributů pro zobrazení.
     */
    @Test
    public void testAttributesList() {
        ClassDiagram d = new ClassDiagram("My model");
        UMLClass cls = d.createClass("C1");
        UMLAttribute attr1 = new UMLAttribute("count", d.classifierForName("int"));
        UMLAttribute attr2 = new UMLAttribute("id", d.classifierForName("int"));
        UMLAttribute attr3 = new UMLAttribute("c", d.classifierForName("C1"));

        cls.addAttribute(attr1);
        cls.addAttribute(attr2);
        cls.addAttribute(attr3);

        List<UMLAttribute> attributes = cls.getAttributes();
        // Ověříme, zda se při pokusu o změnu generuje výjimka
        try {
            attributes.clear();
            Assert.fail("Vraceny seznam atributu nesmi jit modifikovat.");
        } catch (UnsupportedOperationException ex) {
            // spravne generovana vyjimka
        }

        Assert.assertEquals("Pocet atributu tridy C1 je 3.", 3, attributes.size());

        StringBuilder str = new StringBuilder();
        attributes.forEach(a -> { str.append(a); str.append(";"); } );
        Assert.assertEquals("Test zpracovani atributu UML tridy.",
                "count:int(false);id:int(false);c:C1(true);", str.toString());
    }

    /**
     * Test zpracovani operaci.
     */
    @Test
    public void testOperations() {
        ClassDiagram d = new ClassDiagram("My model");
        UMLClass cls = d.createClass("C1");

        UMLOperation op1 = UMLOperation.create("method1", d.classifierForName("void"),
                new UMLAttribute("arg1", d.classifierForName("C1")),
                new UMLAttribute("arg2", d.classifierForName("String")));

        List<UMLAttribute> attributes = op1.getArguments();

        Assert.assertEquals("Navratovy typ operace je void.", "void(false)", op1.getType().toString());

        StringBuilder str = new StringBuilder();
        attributes.forEach(a -> { str.append(a); str.append(";"); } );
        Assert.assertEquals("Test zpracovani argumentu operace.",
                "arg1:C1(true);arg2:String(false);", str.toString());
    }

    /**
     * Test, zda hierarchie dědičnosti odpovídá zadání.
     */
    @Test
    public void testInheritanceHierarchy() {
        ClassDiagram d = new ClassDiagram("My model");
        UMLClassifier cls = new UMLClass("C1");
        UMLAttribute attr = new UMLAttribute("count", d.classifierForName("int"));
        Assert.assertTrue("Test hierarchie dedicnosti.", d instanceof Element);
        Assert.assertTrue("Test hierarchie dedicnosti.", cls instanceof Element);
        Assert.assertTrue("Test hierarchie dedicnosti.", attr instanceof Element);
    }
    
}
