package app.com.muhammad.voice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener
{
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String TAG = "HomeScreenActivity";
    private PlaceInfo mPlace;
    private static final float DEFAULT_ZOOM = 15f;
    private com.google.android.gms.maps.model.Marker mMarker;

    private GoogleMap mMap;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private GoogleApiClient mGoogleApiClient;

    private static final int finePermissionLocation = 101;
    FirebaseFirestore db;

    DrawerLayout mDrawerLayout;

    String textCoordinates = "";

    private FirebaseAuth mAuth;
    private TextView email;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

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
                        if (id == R.id.local_cities)
                        {
                            //Toast.makeText(getApplicationContext(), "Local Cities clicked", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(HomeScreenActivity.this, SignUpActivity.class);
                            intent.putExtra("caller", TAG);
                            startActivity(intent);
                        }

                        return true;
                    }
                });

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);

            // code to load raw map without default markers
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

            // load the user check ins
            db.collection("placesCollection")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }


                            List<PlaceInfo> userCheckIns = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value)
                            {
                                // Create a new placeInfo object for each check in user has made
                                PlaceInfo userCheckIn = new PlaceInfo();

                                if (doc.get("name") != null)
                                {
                                    userCheckIn.setName(doc.getString("name"));
                                }
                                if (doc.get("address") != null)
                                {
                                    userCheckIn.setAddress(doc.getString("address"));
                                }
                                if (doc.get("id") != null)
                                {
                                    userCheckIn.setId(doc.getString("id"));
                                }
                                if (doc.get("phone") != null)
                                {
                                    userCheckIn.setPhoneNumber(doc.getString("phone"));
                                }
                                if (doc.get("website") != null)
                                {
                                    userCheckIn.setWebsiteUri(Uri.parse(doc.getString("website")));
                                }

                                userCheckIn.setLatlng(new LatLng(doc.getGeoPoint("LatLng").getLatitude(),
                                        doc.getGeoPoint("LatLng").getLongitude()));

                                userCheckIn.setRating(doc.getDouble("rating"));

                                // Add the individual check in to the collection
                                userCheckIns.add(userCheckIn);
                            }

                            // Add the markers on the map
                            for (PlaceInfo checkIn: userCheckIns)
                            {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(checkIn.getLatlng())
                                        .title(checkIn.getName())
                                        //.snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
                                        // use Voice Icon here. TODO: the current icons are too large. Ask Luis to make it smaller
                                        .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

                                PlaceInfo info = new PlaceInfo();
                                info.setName(checkIn.getName());
                                info.setAddress(checkIn.getAddress());
                                info.setPhoneNumber(checkIn.getPhoneNumber());
                                info.setWebsiteUri(checkIn.getWebsiteUri());
                                info.setRating(checkIn.getRating());

                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(HomeScreenActivity.this);
                                mMap.setInfoWindowAdapter(customInfoWindow);

                                com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(info);
                            }

                            Log.d(TAG, "Current check ins: " + userCheckIns);
                        }
                    });
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, finePermissionLocation);
            }
        }

        /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_voice_marker)));
            }
        });*/
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

    public void checkIn(View view)
    {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(HomeScreenActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "GooglePlayServicesNotAvailableException: " + e.getMessage());
        }

        //new GetCoordinates().execute(address.replace(" ","+"));
        Log.i(TAG, "startActivityForResult finished");


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>()
    {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            SavePlaceInfo();

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();
        }
    };

    private void SavePlaceInfo()
    {
        // Store the place info in the DB
        Map<String, Object> placeCollection = new HashMap<>();
        if (mPlace.getName() != null)
        {
            placeCollection.put("name", mPlace.getName());
        }
        if (mPlace.getAddress() != null)
        {
            placeCollection.put("address", mPlace.getAddress());
        }
        if (mPlace.getId() != null)
        {
            placeCollection.put("id", mPlace.getId());
        }
        if (mPlace.getPhoneNumber() != null)
        {
            placeCollection.put("phone", mPlace.getPhoneNumber());
        }
        if (mPlace.getWebsiteUri() != null)
        {
            placeCollection.put("website", mPlace.getWebsiteUri().toString());
        }

        placeCollection.put("LatLng", new GeoPoint(mPlace.getLatlng().latitude, mPlace.getLatlng().longitude));
        placeCollection.put("rating", mPlace.getRating());

        CollectionReference placesCollection = db.collection("placesCollection");

        // Add a new document with a generated ID
        placesCollection
            .add(placeCollection)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("MainActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("MainActivity", "Error adding document", e);
                }
            });
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(HomeScreenActivity.this));

        if(placeInfo != null){
            try{
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_voice_marker))
                    .position(latLng));
        }

        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_voice_marker));
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        if (email.getText() != null)
        {
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null)
            {
                email.setText(mAuth.getCurrentUser().getEmail());
            }
        }

        mDrawerLayout.openDrawer(Gravity.START);
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

                textCoordinates = String.format("Coordinates : %s / %s ",lat,lng);

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
