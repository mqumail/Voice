package app.com.muhammad.voice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class UserInformation extends SharedPreferencesManagement {

    public UserInformation (String uid, Context context){
        super.uid = uid;
        super.context = context;
        super.key = uid + "-UserInfo";
    }

    //Gets all user's information in a String format
    public String getUserInformation(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "empty" );
    }

    //Gets user's ID
    public String getUserID(){
        String preferences = getUserInformation();
        String userID;
        String[] userArray;

        try{
            userArray = preferences.split("/");
            userID = userArray[0];
        } catch(Exception e) {
            e.printStackTrace();
            userID = "NA";
        }

        return userID;
    }

    //Gets user's Name
    public String getUserName(){
        String preferences = getUserInformation();
        String userName;
        String[] userArray;

        try{
            userArray = preferences.split("/");
            userName = userArray[1];
        } catch(Exception e) {
            e.printStackTrace();
            userName = "NA";
        }

        return userName;
    }

    //Gets user's Email
    public String getUserEmail(){
        String preferences = getUserInformation();
        String userEmail;
        String[] userArray;

        try{
            userArray = preferences.split("/");
            userEmail = userArray[2];
        } catch(Exception e) {
            e.printStackTrace();
            userEmail = "NA";
        }

        return userEmail;
    }

    public void setUserInformation(String userString){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, userString);
        editor.commit();
        //Toast.makeText(context, "Name Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    //Erase all Shared Preferences
    public void clearSharedPreferences(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }
}
