package app.com.muhammad.voice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity
{
    private SharedPreferences preferences;
    private CheckBox city1;
    private CheckBox city2;
    private CheckBox city3;
    private CheckBox city4;
    private CheckBox city5;
    private CheckBox city6;
    private CheckBox city7;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        city1 = findViewById(R.id.city1);
        city2 = findViewById(R.id.city2);
        city3 = findViewById(R.id.city3);
        city4 = findViewById(R.id.city4);
        city5 = findViewById(R.id.city5);
        city6 = findViewById(R.id.city6);
        city7 = findViewById(R.id.city7);
        loadPreferences("localCities", getApplicationContext());
    }


    public void continueHome(View view)
    {
        setPreferences("localCities", getApplicationContext());
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);

    }

    public void skipHome(View view)
    {
        clearPreferences();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
    }

    private void skipPreferences(){
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        this.startActivity(intent);
    }

    private void setPreferences(String key, Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String citiesInfo = "";
        citiesInfo = (city1.isChecked()) ? citiesInfo + "Weimar" + "/" : citiesInfo + "" ;
        citiesInfo = (city2.isChecked()) ? citiesInfo + "Mexico City" + "/" : citiesInfo + "" ;
        citiesInfo = (city3.isChecked()) ? citiesInfo + "Dubai" + "/" : citiesInfo + "" ;
        citiesInfo = (city4.isChecked()) ? citiesInfo + "Muscat" + "/" : citiesInfo + "" ;
        citiesInfo = (city5.isChecked()) ? citiesInfo + "New York City" + "/" : citiesInfo + "" ;
        citiesInfo = (city6.isChecked()) ? citiesInfo + "Karachi" + "/" : citiesInfo + "" ;
        citiesInfo = (city7.isChecked()) ? citiesInfo + "Spartanburg" + "/" : citiesInfo + "" ;
        editor.putString(key, citiesInfo);
        editor.commit();
        Toast.makeText(getApplicationContext(), "Cities Saved Successfully", Toast.LENGTH_LONG).show();
    }

    private void loadPreferences(String key, Context context){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String savedCities = preferences.getString(key, "empty" );
        Toast.makeText(this, savedCities, Toast.LENGTH_SHORT).show();

        //if(savedCities.equals("empty")) {
            String[] citiesArray = savedCities.split("/");
            for (String aCitiesArray : citiesArray) {
                switch (aCitiesArray) {
                    case "Weimar":
                        city1.setChecked(true);
                        break;
                    case "Mexico City":
                        city2.setChecked(true);
                        break;
                    case "Dubai":
                        city3.setChecked(true);
                        break;
                    case "Muscat":
                        city4.setChecked(true);
                        break;
                    case "New York City":
                        city5.setChecked(true);
                        break;
                    case "Karachi":
                        city6.setChecked(true);
                        break;
                    case "Spartanburg":
                        city7.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
    }

    private void clearPreferences() {
        preferences =  SignUpActivity.this.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

}