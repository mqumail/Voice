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
import app.com.muhammad.voice.utils.SettingsUtils;
import app.com.muhammad.voice.utils.UserInformation;

public class SignUpActivity extends AppCompatActivity  {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tUserName = findViewById(R.id.tUserName);
        mListView = findViewById(R.id.citiesList);

        aCityList = new ArrayList<>();
        aCityListView = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_list_item_1, aCityListView);
        mListView.setAdapter(arrayAdapter);

        settingsUtils.loadUserName(user, userInformation, tUserName);
        settingsUtils.loadCities(localCities, aCityList, aCityListView, arrayAdapter, this);

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
                settingsUtils.addCitytoList(aCityListView, aCityList, arrayAdapter, place, SignUpActivity.this);
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: (Autocomplete failed)" + status);
            }
        });

    }

    public void continueHome(View view)
    {
        settingsUtils.saveCities(aCityList, localCities, this);
        settingsUtils.saveUserInfo(tUserName, mUID, user, userInformation);
        Intent intent = new Intent(this, HomeScreenActivity.class);
        this.startActivity(intent);
        SignUpActivity.this.finish();
    }

}