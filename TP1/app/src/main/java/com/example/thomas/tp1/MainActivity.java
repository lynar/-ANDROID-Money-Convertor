package com.example.thomas.tp1;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.app.ProgressDialog.show;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private HashMap<String, String> map = new HashMap<>(); //Création de la Hashmap
    private List<CharSequence> spinnerList = new ArrayList<CharSequence>(); // Création de la liste des monnaies
    String item, item2;
    final EditText myEdit = findViewById(R.id.editText);
    final TextView myText = findViewById(R.id.textView);
    Spinner mySpin1;
    Spinner mySpin2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySpin1 = findViewById(R.id.spinner);
        mySpin2 = findViewById(R.id.spinner2);

        item = mySpin1.getSelectedItem().toString();
        item2 = mySpin2.getSelectedItem().toString();

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this,
                android.R.layout.simple_spinner_item,
                spinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mySpin1.setAdapter(adapter);
        mySpin2.setAdapter(adapter);

        mySpin1.setOnItemClickListener(this);
        mySpin2.setOnItemClickListener(this);

        URL myURL;

        {
            try {
                myURL = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
                HttpURLConnection urlConnection = (HttpURLConnection) myURL.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();

                DocumentBuilderFactory myDBF = DocumentBuilderFactory.newInstance();
                DocumentBuilder myDB = myDBF.newDocumentBuilder();
                Document myDoc = myDB.parse(inputStream);
                NodeList myNode = myDoc.getElementsByTagName("Cube");

                for(int i=0; i < myNode.getLength(); i++){
                    Element e = (Element)myNode.item(i);
                    String currency = e.getAttribute("currency");
                    String rate = e.getAttribute("rate");
                    map.put(currency, rate);
                    Log.v("Convert",""+currency+" "+rate);
                }

                for(Map.Entry<String, String> entry : map.entrySet()){
                    String myKey = entry.getKey();
                    Log.v("Convert",myKey);

                    if(!myKey.isEmpty()){
                        spinnerList.add(myKey);
                    }
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



    public void onStart () {
        super.onStart();
        System.out.println("test");
        Button myButton = findViewById(R.id.button);

        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try{
                    double inputValue = Double.valueOf(myEdit.getText().toString());  //On récupère la valeur rentrée par l'utilisateur
                    for(Map.Entry<String, String> entry : map.entrySet()){
                        String key = entry.getKey();
                        if(item.equals(key)){
                            double value1 = inputValue/Double.parseDouble(map.get(key));

                            for(Map.Entry<String, String> entry2 : map.entrySet()){
                                String key2 = entry2.getKey();
                                if(item2.equals(key2)){
                                    double result = Double.parseDouble(map.get(key2))*value1;
                                    myText.setText(String.valueOf(result));
                                }
                            }
                        }

                    }
                }
                catch (NumberFormatException excep){
                    Toast.makeText(getApplicationContext(),"Pas un nombre valide",Toast.LENGTH_SHORT).show();
                    Log.w("inputValue","Convertion impossible : " + myEdit.getText().toString());
                }

                /*String mySpinText1 = mySpin1.getSelectedItem().toString();
                String mySpinText2 = mySpin2.getSelectedItem().toString();

                //System.out.println("hello world");
                String price = myEdit.getText().toString();
                double prix= Double.parseDouble(price);
                double dollar, yen, peso;

                Log.v("price :", price);

                if(mySpinText1.equals("Yen")){
                    if(mySpinText2.equals("Dollar")){
                        dollar=convYD(prix);
                        myText.setText(String.valueOf(dollar+" $"));
                    }
                    if(mySpinText2.equals("Peso")){
                        peso=convYP(prix);
                        myText.setText(String.valueOf(peso+" $mxn"));
                    }
                    if(mySpinText2.equals("Yen")){
                        myText.setText(String.valueOf(prix+" ¥"));
                    }
                }

                if(mySpinText1.equals("Dollar")){
                    if(mySpinText2.equals("Yen")){
                        yen=convDY(prix);
                        myText.setText(String.valueOf(yen+" ¥"));
                    }
                    if(mySpinText2.equals("Peso")){
                        peso=convDP(prix);
                        myText.setText(String.valueOf(peso+" $mxn"));
                    }
                    if(mySpinText2.equals("Dollar")){
                        myText.setText(String.valueOf(prix+" $"));
                    }
                }

                if(mySpinText1.equals("Peso")){
                    if(mySpinText2.equals("Yen")){
                        yen=convPY(prix);
                        myText.setText(String.valueOf(yen+" ¥"));
                    }
                    if(mySpinText2.equals("Dollar")){
                        dollar=convPD(prix);
                        myText.setText(String.valueOf(dollar+" $"));
                    }
                    if(mySpinText2.equals("Peso")){
                        myText.setText(String.valueOf(prix+" $mxn"));
                    }
                }
*/

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    /*//Conversion Dollar -> Yen
    public double convDY(double dollar){
        double result = dollar * 112.31;
        return result;
    }

    //Conversion Dollar -> Peso
    public double convDP(double dollar){
        double result = dollar * 19.07;
        return result;
    }

    //Conversion Yen -> Peso
    public double convYP(double yen){
        double result = yen * 0.17;
        return result;
    }

    //Conversion Yen -> Dollar
    public double convYD(double yen){
        double result = yen * 0.0088;
        return result;
    }

    //Conversion Peso -> Dollar
    public double convPD(double peso){
        double result = peso * 0.052;
        return result;
    }

    //Conversion Peso -> Yen
    public double convPY(double peso){
        double result = peso * 5.93;
        return result;
    }*/



}