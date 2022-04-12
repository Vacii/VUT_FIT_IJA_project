package ija.umleditor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class loadJson {

    public static String getJSONFromFile(String filename) {

        String jsonText = "";

        try {

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

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String entity = jsonObject.getString("entity");
            System.out.print(entity + "\n");

            JSONObject jsonAttributeObject = (JSONObject) jsonObject.get("attributes");

            JSONArray keys = jsonAttributeObject.names();

            for (int j = 0; j < keys.length(); j++) {

                String key = keys.getString(j);
                String value = jsonAttributeObject.getString(key);
                System.out.println("\t" + key + "\t" + value);
            }

            JSONObject jsonMethodObject = (JSONObject) jsonObject.get("methods");
            JSONArray keys2 = jsonMethodObject.names();
            System.out.print("\n");

            for (int j = 0; j < keys2.length(); j++) {

                String key = keys2.getString(j);
                String value = jsonMethodObject.getString(key);
                System.out.println("\t" + key + "\t" + value);
            }


        }

    }

}
