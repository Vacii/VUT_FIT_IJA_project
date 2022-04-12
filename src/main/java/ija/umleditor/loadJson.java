package ija.umleditor;

import ija.umleditor.uml.Element;
import ija.umleditor.uml.UMLAttribute;
import ija.umleditor.uml.UMLClassifier;
import ija.umleditor.uml.UMLOperation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class loadJson {

    public static String getJSONFromFile(String filename) {

        String jsonText = "";

        try {

            //reading through file and appending each line to our empty string jsonText
            //when we reach the EOF we close the buffer and return JSON file as a string
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                jsonText += line + "\n";
            }

            bufferedReader.close();
        }

        catch (Exception e) {

            e.printStackTrace();
        }

        return jsonText;
    }

    public static void parseJsonToObject(String JSONStr) {

        JSONArray jsonArray = new JSONArray(JSONStr);

        for (int i = 0; i < jsonArray.length(); i++) {

            //iterating over JSON array -> this way we can select all elements and its attributes
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //in "i" object select the entity key word in JSON -> corresponds to elements name
            String entity = jsonObject.getString("entity");

            //element in class diagram, takes the entity name as a constructor parameter
            Element elementObject = new Element(entity);
            System.out.println(elementObject.getName()); //debug

            //same as entity, only now we are choosing attributes -> it's an array
            //need to iterate through that one more time
            JSONObject jsonAttributeObject = (JSONObject) jsonObject.get("attributes");

            //we don't know the keyword for attributes -> every element has different names
            //for its attributes -> object.names() solves the issue
            JSONArray keys = jsonAttributeObject.names();

            //iterating through attribute array, on "j" key we store the key (name of our attribute)
            //and value, which is type in our case -> creating new Attribute object that requires UMLClassifier
            //object for its type
            //TO DO - Jsem confused z toho druhýho konstruktoru u UMLClassifier, kde je userdefinied - usage?

            for (int j = 0; j < keys.length(); j++) {

                String key = keys.getString(j);
                String value = jsonAttributeObject.getString(key);

                UMLClassifier classifierObject = new UMLClassifier(value);
                UMLAttribute attributeObject = new UMLAttribute(key, classifierObject);

                String name = attributeObject.toString(); //debug
                System.out.println(name);

            }

            //methods are the same as attributes mentioned above
            //constructor takes name of the method and value that the method returns
            //TO DO - what about the arguments?

            JSONObject jsonMethodObject = (JSONObject) jsonObject.get("methods");
            JSONArray keys2 = jsonMethodObject.names();

            for (int j = 0; j < keys2.length(); j++) {

                String key = keys2.getString(j);
                String value = jsonMethodObject.getString(key);

                UMLClassifier classifierObject = new UMLClassifier(value);
                UMLOperation operationObject = new UMLOperation(key, classifierObject);

                String string = operationObject.toString(); //debug
                System.out.println(string);

            }


        }

    }

}
