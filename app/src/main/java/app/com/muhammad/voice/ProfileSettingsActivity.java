package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.SettingsUtils;
import app.com.muhammad.voice.utils.UserInformation;

public class ProfileSettingsActivity extends AppCompatActivity  {

    //protected GeoDataClient mGeoDataClient;
    //protected PlaceDetectionClient mPlaceDetectionClient;
    private SettingsUtils settingsUtils = new SettingsUtils();
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
        //TODO: There is a bug here. After searching for a place and selecting it, the app crashes. Find out why and fix it
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tUserName = findViewById(R.id.tUserName_settings);
        mListView = findViewById(R.id.citiesList_settings);

        aCityList = new ArrayList<>();
        aCityListView = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(ProfileSettingsActivity.this, android.R.layout.simple_list_item_1, aCityListView);
        //mListView.setAdapter(arrayAdapter);

        settingsUtils.loadUserName(user, userInformation, tUserName);
        //settingsUtils.loadCities(localCities, aCityList, aCityListView, arrayAdapter, this);

        /*// Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();*/

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), "AIzaSyAD2mONY8_s4OffYrAsIKW5b_dbRmDSH0Y");
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.search_city_settings);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            private static final String TAG = "AutoComplete";
            @Override
            public void onPlaceSelected(@NonNull Place place)
            {
                Log.i(TAG, "Place: " + place.getName());
                settingsUtils.addCitytoList(aCityListView, aCityList, arrayAdapter, place, ProfileSettingsActivity.this);
            }

            @Override
            public void onError(@NonNull Status status)
            {
                Log.i(TAG, "An error occurred: (Autocomplete failed)" + status);
            }
        });

//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.search_city_settings);
//        autocompleteFragment.setFilter(typeFilter);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            private static final String TAG = "AutoComplete";
//            @Override
//            public void onPlaceSelected(Place place) {
//                Log.i(TAG, "Place: " + place.getName());
//                settingsUtils.addCitytoList(aCityListView, aCityList, arrayAdapter, place, ProfileSettingsActivity.this);
//            }
//            @Override
//            public void onError(Status status) {
//                Log.i(TAG, "An error occurred: (Autocomplete failed)" + status);
//            }
//        });

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
        settingsUtils.saveCities(aCityList, localCities, this);
        settingsUtils.saveUserInfo(tUserName, mUID, user, userInformation);
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
        finish();
    }
}