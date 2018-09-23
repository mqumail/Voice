package app.com.muhammad.voice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public abstract class SharedPreferencesManagement {
    protected SharedPreferences preferences;
    protected String uid;
    protected Context context;
    protected String key;

    public SharedPreferencesManagement(){
    }

    public String loadSPInfo(){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String savedSP = preferences.getString(key, "empty" );
        //Toast.makeText(context, savedSP, Toast.LENGTH_SHORT).show();
        return savedSP;
    }

    public abstract void clearSharedPreferences();

}
