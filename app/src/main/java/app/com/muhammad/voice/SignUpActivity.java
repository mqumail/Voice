package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;

import java.util.ArrayList;

import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.UserInformation;

public class SignUpActivity extends AppCompatActivity  {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private EditText tUserName;
    private ListView mListView;
    private ArrayList<String> aCityList;
    private ArrayList<String> aCityListView;
    private ArrayAdapter<String> arrayAdapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String mUID = user.getUid();
    private LocalCity localCities = new LocalCity(mUID, this);
    private UserInformation userInformation = new UserInformation(mUID, this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tUserName = findViewById(R.id.tUserName);
        mListView = findViewById(R.id.citiesList);

        aCityList = new ArrayList<>();
        aCityListView = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_list_item_1, aCityListView);
        mListView.setAdapter(arrayAdapter);

        loadUserName();
        loadCities();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.search_city);
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            private static final String TAG = "AutoComplete";
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                String allCityInfo = place.getId() + "-" + place.getName() + "-" + place.getAddress();
                aCityList.add(allCityInfo);
                aCityListView.add((String)place.getAddress());
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(SignUpActivity.this,  place.getName() + " added to you settings", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: (Autocomplete failed)" + status);
            }
        });

    }

    public void continueHome(View view)
    {
        saveCities();
        saveUserInfo();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
        finish();
    }

    public void skipHome(View view)
    {
        localCities.clearSharedPreferences();
        userInformation.clearSharedPreferences();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void loadUserName(){
        if(user != null)
        {
            String mName = user.getDisplayName();

            if(userInformation.getUserName() == "NA") {
                if (mName == ""){
                    tUserName.setText("Anonymous");
                }else{
                    tUserName.setText(mName);
                }
            } else {
                tUserName.setText(userInformation.getUserName());
            }
        }
    }

    private void saveUserInfo(){
        String userInfo;
        if(tUserName.getText().length() > 2){
            userInfo = mUID + "/" + tUserName.getText() + "/" + user.getEmail();
        }else userInfo = mUID + "/" + "Anonymous" + "/" + user.getEmail();
        userInformation.setUserInformation(userInfo);
    }

    private void saveCities(){
        String citiesInfo = "";
        for (String aCitiesArray : aCityList) {
            citiesInfo = aCitiesArray + "/" + citiesInfo;
        }
        localCities.setCities(citiesInfo);
    }

    private void loadCities(){
        String[] citiesAddressArray = localCities.getCitiesAddress();
        String[] citiesArray = localCities.getCities();
        if (citiesArray[0] != "NA"){
            int i = 0;
            try{
                for (String aCitiesArray : citiesArray) {
                    aCityList.add(0, aCitiesArray);
                    aCityListView.add(0, citiesAddressArray[i]);
                    arrayAdapter.notifyDataSetChanged();
                    i++;
                }
            }catch(Exception e) {
                System.err.println("Error while retrieving cities from Shared Preferences");
                e.printStackTrace();
                localCities.clearSharedPreferences();
                Toast.makeText(this, "Error Loading Cities", Toast.LENGTH_SHORT).show();
            }

        }
    }
}