package com.example.lynar.tp1;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Converter extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Converter";
    private String item;
    private String item2;
    private HashMap <String, String> map= new HashMap<>();
    private Spinner spinner;
    private Spinner spinner2;
    private List<String> spinner_array = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        Log.i(TAG,"Entering the OnCreate function");


        new DocumentParserConnection().execute();


        //TP3 - Question 2
        ///pointeur de recherche du spinner
        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_array);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);
    }

    ///TP4  Question 6  NetworkOnMainThreadException
    /// Exception qui indique que une opération de Netwworking a lui dans le main thread
    /// il faut donc créer des Thread en parallele pour effectuer une tache de Networking
    /// C'est pourquoi il faut créer une classe qui hérite de la classe AsyncTask afin d'effectuer des
    /// taches en parallele du main thread.


    ///TP4  Question 7
    private class DocumentParserConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            DocParser();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
    }
    private void DocParser() {
        Log.d(TAG, "Doc Parser Function");
        {
            try {
                /// TP4 Question 2
                ///création de l'objet URL
                URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
                ///Instance de ocumentBuilderFactory pour créer un objet documentBuilder
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(url.openStream());
                /// TP4 Question 3
                ///extraction des Nodes "Cube" et création d'une NodeList contenant les Nodes
                NodeList nodes = doc.getElementsByTagName("Cube");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Element e = (Element) nodes.item(i);
                    String currency = e.getAttribute("currency");
                    String rate = e.getAttribute("rate");
                    //TP4 Question 4
                    ///stockage des currency and rate dans la hashMap
                    map.put(currency, rate);
                    Log.d(TAG, "" + currency + " " + rate);
                }

                ///TP4 Question 8 Mise a jour du array du spinner afin de mettre à jour
                /// Les currency dans les spinner
                for ( Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Log.d(TAG, key);
                    if(!key.isEmpty())
                    {spinner_array.add(key); }
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }


    /// TP1- Question 5
    @Override
    public void onStart() {

        //Declaration of elements of the user interface
        super.onStart();
        Button buttonConverte = findViewById(R.id.button);
        final EditText written = findViewById(R.id.editText2);
        final EditText calculated = findViewById(R.id.editText);

        /// TP2 - Question 1
        // OnClickListener
        buttonConverte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException {
                try { /// TP2 - question 7  Try du control de saisie

                    /// TP1 - Question 9
                    // la fonction .getText() permet de récupérer le contenu d'un champ de texte
                    float valuewritten = Float.parseFloat(written.getText().toString());

                    /// TP2 - Question 3
                    // Log pour etre afficher dans la console
                    Log.d(TAG, "Value written: " + valuewritten);

                    ///TP4 - Question 8
                    /// Mise a jour des valeur de Currency en fonction de la Valeur dans La HashMap
                    for ( Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey();
                        if(item==key)
                        {
                            Log.d(TAG, "Value rate conversion: " + map.get(key));
                            float valueeuro = valuewritten / Float.parseFloat(map.get(key));
                            Log.d(TAG, "Value conversion in euro: " + valueeuro);

                            ///TP4 - Question 8
                            // / Mise a jour des valeur de Currency en fonction de la Valeur dans La HashMap
                            for ( Map.Entry<String, String> entry2 : map.entrySet()) {
                                String key2 = entry2.getKey();
                                if(item2==key2)
                                {
                                    Log.d(TAG, "Value rate conversion 2: " + map.get(key2));
                                    float valueresult = Float.parseFloat(map.get(key2)) * valueeuro;
                                    Log.d(TAG, "Value conversion final: " + valueresult);
                                    calculated.setText(String.valueOf(valueresult));
                                    break;
                                }
                            }
                            break;
                        }
                    }


                    /// TP2 - question 7  catch du control de saisie
                    // verifier que la saisie est une valeur numerique
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, e.getMessage());
                    Context context = getApplicationContext();
                    CharSequence text = "Illegal argument!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }


    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "testing on itemSelecteds");
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
