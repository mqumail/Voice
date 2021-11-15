package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.SettingsUtils;
import app.com.muhammad.voice.utils.UserInformation;

public class SignUpActivity extends AppCompatActivity  {

    private SettingsUtils settingsUtils = new SettingsUtils();
    private EditText tUserName;
    private ListView mListView;
    private ArrayList<String> aCityList;
    private ArrayList<String> aCityListView;
    private ArrayAdapter<String> arrayAdapter;

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


        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                .build();
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.search_city);
//        autocompleteFragment.setFilter(typeFilter);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            private static final String TAG = "AutoComplete";
//            @Override
//            public void onPlaceSelected(Place place) {
//                Log.i(TAG, "Place: " + place.getName());
//                settingsUtils.addCitytoList(aCityListView, aCityList, arrayAdapter, SignUpActivity.this);
//            }
//            @Override
//            public void onError(Status status) {
//                Log.i(TAG, "An error occurred: (Autocomplete failed)" + status);
//            }
//        });

    }

    public void continueHome(View view)
    {
//        settingsUtils.saveCities(aCityList, localCities, this);
//        settingsUtils.saveUserInfo(tUserName, mUID, user, userInformation);
//        Intent intent = new Intent(this, HomeScreenActivity.class);
//        this.startActivity(intent);
//        SignUpActivity.this.finish();
    }

}