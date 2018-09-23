package app.com.muhammad.voice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
            citiesString = "NA";
        }
        return citiesString;
    }

    //Gets all local cities information in an Array format
    public String[] getCities(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String[] citiesArray;
        try{
            citiesArray = preferences.getString(key, "empty" ).split("/");
        }catch(Exception e){
            e.printStackTrace();
            citiesArray = new String[1];
            citiesArray[0] = "NA";
        }
        return citiesArray;
    }

    //Gets all local cities IDs in a Array format
    public String[] getCitiesID(){
        String[] citiesArray = getCities();
        String[] filterArray;
        String[] idArray = new String[citiesArray.length];
        int i = 0;

        try{
            for (String aCitiesArray : citiesArray) {
                filterArray = aCitiesArray.split("-");
                idArray[i] = filterArray[0];
                i++;
            }
        } catch(Exception e) {
            e.printStackTrace();
            idArray = new String[1];
            idArray[0] = "NA";
        }

        return idArray;
    }

    //Gets all local cities Names in a Array format
    public String[] getCitiesName(){
        String[] citiesArray = getCities();
        String[] filterArray;
        String[] namesArray = new String[citiesArray.length];
        int i = 0;

        try{
            for (String aCitiesArray : citiesArray) {
                filterArray = aCitiesArray.split("-");
                namesArray[i] = filterArray[1];
                i++;
            }
        } catch(Exception e) {
            e.printStackTrace();
            namesArray = new String[1];
            namesArray[0] = "NA";
        }

        return namesArray;
    }

    //Gets all local cities Address in a Array format
    public String[] getCitiesAddress(){
        String[] citiesArray = getCities();
        String[] filterArray;
        String[] addressArray = new String[citiesArray.length];
        int i = 0;

        try{
            for (String aCitiesArray : citiesArray) {
                filterArray = aCitiesArray.split("-");
                addressArray[i] = filterArray[2];
                i++;
            }
        } catch(Exception e) {
            e.printStackTrace();
            addressArray = new String[1];
            addressArray[0] = "NA";
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
