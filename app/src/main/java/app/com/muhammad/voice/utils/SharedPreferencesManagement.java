package app.com.muhammad.voice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SharedPreferencesManagement {
    private SharedPreferences preferences;
    private String key;
    private Context context;

    public SharedPreferencesManagement(String key, Context context){
        this.context = context;
        this.key = key;
    }

    public String loadSPInfo(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String savedSP = preferences.getString(key, "empty" );
        //Toast.makeText(context, savedSP, Toast.LENGTH_SHORT).show();
        return savedSP;
    }

    public void setSPInfo(String info){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, info);
        editor.commit();
        //Toast.makeText(context, "Information Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    public void clearSP(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }

}
