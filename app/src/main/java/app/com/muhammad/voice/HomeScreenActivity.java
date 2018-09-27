package app.com.muhammad.voice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.UserInformation;

public class HomeScreenActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "HomeScreenActivity";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int finePermissionLocation = 101;

    private GoogleMap mMap;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected GoogleApiClient mGoogleApiClient;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String mUID = user.getUid();
    public LocalCity localCities = new LocalCity(mUID, this);
    public UserInformation userInformation = new UserInformation(mUID, this);
    private CollectionReference placesCollectionReference;

    public DrawerLayout mDrawerLayout;

    private TextView email;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        init();

        NavigationDrawerSetup();

        GoogleAPIClientSetup();
    }

    private void init()
    {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        placesCollectionReference = db.collection("placesCollection");

        ImageButton checkInButton = findViewById(R.id.checkInButton);
        checkInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CheckIn();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void CheckIn()
    {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            this.startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "GooglePlayServicesNotAvailableException: " + e.getMessage());
        }

        Log.i(TAG, "startActivityForResult finished");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                UserCheckIns userCheckIns = new UserCheckIns(HomeScreenActivity.this, mMap, placesCollectionReference);

                userCheckIns.PlacePicker(data);
            }
        }
    }

    private void GoogleAPIClientSetup()
    {
        // Construct a GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    private void NavigationDrawerSetup()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        int id = menuItem.getItemId();

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (id){
                            case R.id.local_cities:
                                //Toast.makeText(getApplicationContext(), "Local Cities clicked", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(HomeScreenActivity.this, ProfileSettingsActivity.class);
                                intent.putExtra("caller", TAG);
                                startActivity(intent);
                                HomeScreenActivity.this.finish();
                            return true;

                            case R.id.log_out:
                                AuthUI.getInstance().signOut(getApplicationContext());
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                HomeScreenActivity.this.finish();
                            return true;

                            default:
                                return HomeScreenActivity.super.onOptionsItemSelected(menuItem);
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);

            // code to load raw map without default markers
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

            DisplayUserCheckIns displayUserCheckIns = new DisplayUserCheckIns(HomeScreenActivity.this, mMap, placesCollectionReference);
            displayUserCheckIns.LoadUserCheckIns();
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, finePermissionLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case finePermissionLocation:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Location permissions are required", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    public void profile(View view)
    {
        // TODO: Find a better way (or location) to place the get user code since this is expensive. Everytime this method executes, the db is called
        // Get current user info before drawer is opened

        email = findViewById(R.id.navigationHeaderTextView);
        userName = findViewById(R.id.navigationHeaderTextUSerName);
        assignProfileView();

        mDrawerLayout.openDrawer(Gravity.START);
    }

    private void assignProfileView(){
        // TODO: Refactor this code to a utility class as its needed multiple places.
        String mUserInfo = userInformation.loadSPInfo();
        if(mUserInfo == "empty"){
            email.setText("Mail not available");
            userName.setText("User Name not available");
        } else{
            String[] userArray = mUserInfo.split("/");
            if(userArray.length > 2){
                email.setText(userArray[2]);
                userName.setText(userArray[1]);
            } else {
                email.setText("Mail not available");
                userName.setText("User Name not available");
            }
        }
    }

    private class GetCoordinates extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog = new ProgressDialog(HomeScreenActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                GeocodingDataHandler http = new GeocodingDataHandler();
                String url = String.format(
                                "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                                address,
                                R.string.geocode_api_key);
                response = http.getGeocodeData(url);
                return response;
            }
            catch (Exception ex)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();

                String textCoordinates = String.format("Coordinates : %s / %s ", lat, lng);

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
