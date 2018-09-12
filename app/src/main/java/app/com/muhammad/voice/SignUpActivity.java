package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.com.muhammad.voice.utils.SharedPreferencesManagement;

public class SignUpActivity extends AppCompatActivity {

    private CheckBox city1;
    private CheckBox city2;
    private CheckBox city3;
    private CheckBox city4;
    private CheckBox city5;
    private CheckBox city6;
    private CheckBox city7;
    private EditText tUserName;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String mUID = user.getUid();
    private SharedPreferencesManagement spCities = new SharedPreferencesManagement(mUID + "-LocalCities", this);
    private SharedPreferencesManagement spUserInfo = new SharedPreferencesManagement(mUID + "-UserInfo", this);

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
        tUserName = findViewById(R.id.tUserName);
        getUserName();
        loadCities();
    }

    public void continueHome(View view)
    {
        setCities();
        setUserInfo();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
        finish();
    }

    public void skipHome(View view)
    {
        spCities.clearSP();
        spUserInfo.clearSP();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void getUserName(){
        if(user != null)
        {
            String mName = user.getDisplayName();
            String savedUserInfo = spUserInfo.loadSPInfo();

            if(savedUserInfo == "empty") {
                if (mName == ""){
                    tUserName.setText("Anonymous");
                }else{
                    tUserName.setText(mName);
                }
            } else {
                String[] userArray = savedUserInfo.split("/");
                if(userArray.length > 2){
                    tUserName.setText(userArray[1]);
                }
            }
        }
    }

    private void setUserInfo(){
        String userInfo = mUID + "/" + tUserName.getText() + "/" + user.getEmail();
        spUserInfo.setSPInfo(userInfo);
    }

    private void setCities(){
        String citiesInfo = "";
        citiesInfo = (city1.isChecked()) ? citiesInfo + "Weimar" + "/" : citiesInfo + "" ;
        citiesInfo = (city2.isChecked()) ? citiesInfo + "Mexico City" + "/" : citiesInfo + "" ;
        citiesInfo = (city3.isChecked()) ? citiesInfo + "Dubai" + "/" : citiesInfo + "" ;
        citiesInfo = (city4.isChecked()) ? citiesInfo + "Muscat" + "/" : citiesInfo + "" ;
        citiesInfo = (city5.isChecked()) ? citiesInfo + "New York City" + "/" : citiesInfo + "" ;
        citiesInfo = (city6.isChecked()) ? citiesInfo + "Karachi" + "/" : citiesInfo + "" ;
        citiesInfo = (city7.isChecked()) ? citiesInfo + "Spartanburg" + "/" : citiesInfo + "" ;
        spCities.setSPInfo(citiesInfo);
    }

    private void loadCities(){
        String savedCities = spCities.loadSPInfo();

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
}