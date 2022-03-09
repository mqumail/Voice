package app.com.muhammad.voice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;

public class LocalCity extends SharedPreferencesManagement{

    public LocalCity(String uid, Context context){
        super.uid = uid;
        super.context = context;
        super.key = uid + "-LocalCities";
    }

    //Gets all local cities information in a String format
    public String getCitiesString(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String citiesString;
        try{
            citiesString = preferences.getString(key, "empty" );
        }catch(Exception e){
            e.printStackTrace();
            citiesString = "Error";
        }
        return citiesString;
    }

    //Gets all local cities information in an Array format
    public ArrayList<String> getCities(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String citiesString;
        ArrayList<String> citiesArray = new ArrayList<>();
        try{
            //citiesArray = preferences.getString(key, "empty" ).split("/");
            citiesString = preferences.getString(key, "empty" );
            citiesArray = new ArrayList<>(Arrays.asList(citiesString.split("/")));

        }catch(Exception e){
            e.printStackTrace();
            citiesArray.add("Error");
        }
        return citiesArray;
    }

    //Gets all local cities IDs in a Array format
    public ArrayList<String> getCitiesID(){
        ArrayList<String> citiesArray = getCities();
        ArrayList<String> filterArray;
        ArrayList<String> idArray = new ArrayList<>(citiesArray.size());

        try{
            for (String aCitiesArray : citiesArray) {
                filterArray = new ArrayList<>(Arrays.asList(aCitiesArray.split("-")));
                idArray.add(filterArray.get(0));
            }
        } catch(Exception e) {
            e.printStackTrace();
            idArray.add("Error");
        }

        return idArray;
    }

    //Gets all local cities Names in a Array format
    public ArrayList<String> getCitiesName(){
        ArrayList<String> citiesArray = getCities();
        ArrayList<String> filterArray;
        ArrayList<String> namesArray = new ArrayList<>(citiesArray.size());

        try{
            for (String aCitiesArray : citiesArray) {
                filterArray = new ArrayList<>(Arrays.asList(aCitiesArray.split("-")));
                namesArray.add(filterArray.get(1));
            }
        } catch(Exception e) {
            e.printStackTrace();
            namesArray.add("Error");
        }

        return namesArray;
    }

    //Gets all local cities Address in a Array format
    public ArrayList<String> getCitiesAddress(){
        ArrayList<String> citiesArray = getCities();
        ArrayList<String> filterArray;
        ArrayList<String> addressArray = new ArrayList<>(citiesArray.size());
        int i = 0;

        try{
            for (String aCitiesArray : citiesArray) {
                filterArray = new ArrayList<>(Arrays.asList(aCitiesArray.split("-")));
                addressArray.add(filterArray.get(2));
                i++;
            }
        } catch(Exception e) {
            e.printStackTrace();
            addressArray.add("Error");
        }

        return addressArray;
    }

    public void setCities(String citiesString){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, citiesString);
        editor.commit();
    }

    public void clearSharedPreferences(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }


}
