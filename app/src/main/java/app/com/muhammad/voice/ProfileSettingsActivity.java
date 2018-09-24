package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.UserInformation;

public class ProfileSettingsActivity extends AppCompatActivity  {

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
        setContentView(R.layout.activity_profile_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tUserName = findViewById(R.id.tUserName_settings);
        mListView = findViewById(R.id.citiesList_settings);

        aCityList = new ArrayList<>();
        aCityListView = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(ProfileSettingsActivity.this, android.R.layout.simple_list_item_1, aCityListView);
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
                getFragmentManager().findFragmentById(R.id.search_city_settings);
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
                Toast.makeText(ProfileSettingsActivity.this,  place.getName() + " added to you settings", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: (Autocomplete failed)" + status);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                this.startActivity(upIntent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

            if(userInformation.getUserName() == "NA" || userInformation.getUserName() == "") {
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
        if (aCityList.size() > 0){
            if (aCityList.get(0) != "" || aCityList.get(0) != "empty"){
                for (String aCitiesArray : aCityList) {
                    citiesInfo = aCitiesArray + "/" + citiesInfo;
                }
                localCities.setCities(citiesInfo);
            }
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCities(){
        try{
            String[] citiesAddressArray = localCities.getCitiesAddress();
            String[] citiesArray = localCities.getCities();
            if (citiesArray[0] != "empty") {
                int i = 0;
                for (String aCitiesArray : citiesArray) {
                    aCityList.add(0, aCitiesArray);
                    aCityListView.add(0, citiesAddressArray[i]);
                    arrayAdapter.notifyDataSetChanged();
                    i++;
                }
                Toast.makeText(this, "Local Cities Loaded", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e) {
            System.err.println("Error while retrieving cities from Shared Preferences");
            e.printStackTrace();
            localCities.clearSharedPreferences();
            Toast.makeText(this, "Error Loading Cities", Toast.LENGTH_SHORT).show();
        }

    }
}