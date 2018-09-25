package app.com.muhammad.voice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.auth.AuthUI;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.com.muhammad.voice.DTO.CheckinInfo;
import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.MyCallBack;
import app.com.muhammad.voice.utils.SharedPreferencesManagement;
import app.com.muhammad.voice.utils.UserInformation;

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
    private TextView userName;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String mUID = user.getUid();

    private LocalCity localCities = new LocalCity(mUID, this);
    private UserInformation userInformation = new UserInformation(mUID, this);

    private PopupWindow commentAndVotesPopup;
    private PopupWindow placeDetailPopupWindow, revealedUserPopupWindow, reviewsPopupWindow;

    PendingResult<PlaceBuffer> placeResult;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerViewReviews;
    private RecyclerView.Adapter mAdapterReviews;
    private RecyclerView.LayoutManager mLayoutManagerReviews;
    private List<String> revealedUserNameDataSet;
    private List<CheckinInfo> reviewsDataSet;
    private List<String> emptyUserNameDataSetMessage, emptyReviewsDataSetMessage;

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

        db = FirebaseFirestore.getInstance();
        //placesCollectionReference = db.collection("placesCollection");

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
                    .addSnapshotListener(new EventListener<QuerySnapshot>()
                    {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e)
                        {
                            if (e != null)
                            {
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

                                try
                                {
                                    userCheckIn.setRating(doc.getDouble("rating"));
                                }
                                catch (NullPointerException ex)
                                {
                                    Log.e(TAG, "Null exception");
                                }

                                // Add the individual check in to the collection
                                userCheckIns.add(userCheckIn);
                            }

                            mMap.clear();

                            // Add the markers, including the new ones on the map
                            for (PlaceInfo checkIn: userCheckIns)
                            {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(checkIn.getLatlng())
                                        .title(checkIn.getName())
                                        .snippet(checkIn.getAddress())
                                        // use Voice Icon here. TODO: the current icons are too large. Ask Luis to make it smaller
                                        .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

                                mMap.addMarker(markerOptions);
                            }

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                            {
                                private TextView name, address,
                                        localNumber, localUpvotes,
                                        touristNumber, touristUpvotes,
                                        commentsNumber, revealedUserName,
                                        revealedUserAddress, placeReviewTitle;

                                private Button placeDetailPopupWindowCloseButton,
                                        placeDetailPopupWindowRevealedUserButton,
                                        revealedUserPopupWindowCloseButton,
                                        reviewsButton, reviewsCloseButton;


                                private int numberOfLocalVisitors, numberOfTouristVisitors,
                                        numberOfLocalHearts, numberOfTouristHearts,
                                        numberOfComments;

                                @Override
                                public boolean onMarkerClick(final Marker marker)
                                {
                                    if(placeDetailPopupWindow != null)
                                    {
                                        if (placeDetailPopupWindow.isShowing())
                                        {
                                            placeDetailPopupWindow.dismiss();
                                        }
                                    }

                                    LayoutInflater inflater = (LayoutInflater) HomeScreenActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                                    final View customViewPlaceDetail = inflater.inflate(R.layout.place_details_popup_window, null);
                                    final View customViewRevealedUser = inflater.inflate(R.layout.place_reveal_list_popup_window, null);
                                    final View customViewCommentList = inflater.inflate(R.layout.comments_list_popup_window, null);

                                    //*************************************************** placeDetailPopupWindow ***************************************************
                                    // Reset the variables on each click so accurate number is shown
                                    numberOfLocalVisitors = 0;
                                    numberOfTouristVisitors = 0;
                                    numberOfLocalHearts = 0;
                                    numberOfTouristHearts = 0;
                                    numberOfComments = 0;

                                    // Hide the default Info Window
                                    marker.hideInfoWindow();

                                    placeDetailPopupWindow = new PopupWindow(
                                            customViewPlaceDetail,
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    View parent = findViewById(android.R.id.content);
                                    placeDetailPopupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        placeDetailPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                                        placeDetailPopupWindow.setElevation(20);
                                    }

                                    reviewsPopupWindow = new PopupWindow(
                                            customViewCommentList,
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT);

                                    placeDetailPopupWindowCloseButton = customViewPlaceDetail.findViewById(R.id.closePopupButton);
                                    placeDetailPopupWindowCloseButton.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            // Dismiss the popup window
                                            placeDetailPopupWindow.dismiss();
                                        }
                                    });

                                    reviewsButton = customViewPlaceDetail.findViewById(R.id.reviews_button);
                                    reviewsButton.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            // Set an elevation value for popup window
                                            // Call requires API level 21
                                            if(Build.VERSION.SDK_INT>=21){
                                                reviewsPopupWindow.setElevation(5.0f);
                                            }

                                            // set the title
                                            placeReviewTitle = customViewCommentList.findViewById(R.id.name_comments_list);
                                            placeReviewTitle.setText(marker.getTitle());

                                            mRecyclerViewReviews = customViewCommentList.findViewById(R.id.users_comments_list);

                                            // use this setting to improve performance if you know that changes
                                            // in content do not change the layout size of the RecyclerView
                                            mRecyclerViewReviews.setHasFixedSize(true);

                                            // use a linear layout manager
                                            mLayoutManagerReviews = new LinearLayoutManager(HomeScreenActivity.this);
                                            mRecyclerViewReviews.setLayoutManager(mLayoutManagerReviews);

                                            if (reviewsDataSet != null)
                                            {
                                                if(reviewsDataSet.size() > 0)
                                                {
                                                    // specify an adapter (see also next example)
                                                    mAdapterReviews = new RecyclerViewReviewsAdapter(reviewsDataSet);
                                                    mRecyclerViewReviews.setAdapter(mAdapterReviews);
                                                }
                                                else
                                                {
                                                    emptyReviewsDataSetMessage = new ArrayList<>();
                                                    emptyReviewsDataSetMessage.add("No reviews yet!");
                                                    mAdapterReviews = new RecyclerViewAdapter(emptyReviewsDataSetMessage);
                                                    mRecyclerViewReviews.setAdapter(mAdapterReviews);
                                                }
                                            }

                                            reviewsPopupWindow.showAtLocation(mDrawerLayout, Gravity.CENTER,0,0);
                                        }
                                    });

                                    placeDetailPopupWindowRevealedUserButton = customViewPlaceDetail.findViewById(R.id.revealedUsersButton);
                                    placeDetailPopupWindowRevealedUserButton.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            // Set an elevation value for popup window
                                            // Call requires API level 21
                                            if(Build.VERSION.SDK_INT>=21){
                                                revealedUserPopupWindow.setElevation(5.0f);
                                            }

                                            mRecyclerView = customViewRevealedUser.findViewById(R.id.users_revealed_list_recycler_view);

                                            // use this setting to improve performance if you know that changes
                                            // in content do not change the layout size of the RecyclerView
                                            mRecyclerView.setHasFixedSize(true);

                                            // use a linear layout manager
                                            mLayoutManager = new LinearLayoutManager(HomeScreenActivity.this);
                                            mRecyclerView.setLayoutManager(mLayoutManager);

                                            if (revealedUserNameDataSet != null)
                                            {
                                                if(revealedUserNameDataSet.size() > 0)
                                                {
                                                    // specify an adapter (see also next example)
                                                    mAdapter = new RecyclerViewAdapter(revealedUserNameDataSet);
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }
                                                else
                                                {
                                                    emptyUserNameDataSetMessage = new ArrayList<>();
                                                    emptyUserNameDataSetMessage.add("No revealed users have checked in yet!");
                                                    mAdapter = new RecyclerViewAdapter(emptyUserNameDataSetMessage);
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }
                                            }

                                            revealedUserPopupWindow.showAtLocation(mDrawerLayout, Gravity.CENTER,0,0);
                                        }
                                    });

                                    // Set an elevation value for popup window
                                    // Call requires API level 21
                                    if(Build.VERSION.SDK_INT>=21){
                                        placeDetailPopupWindow.setElevation(5.0f);
                                    }

                                    // Connect to DB and get the information needed to display on the popup
                                    // Name and address of the place
                                    // # of local visitors + # of hearts, # of tourist visitors + hearts, # of comments
                                    name = customViewPlaceDetail.findViewById(R.id.namePlaceDetail);
                                    address = customViewPlaceDetail.findViewById(R.id.addressPlaceDetail);
                                    localNumber = customViewPlaceDetail.findViewById(R.id.localNumber);
                                    localUpvotes = customViewPlaceDetail.findViewById(R.id.localUpvotes);
                                    touristNumber = customViewPlaceDetail.findViewById(R.id.touristNumber);
                                    touristUpvotes = customViewPlaceDetail.findViewById(R.id.touristUpvotes);
                                    commentsNumber = customViewPlaceDetail.findViewById(R.id.commentNumber);

                                    name.setText(marker.getTitle());
                                    address.setText(marker.getSnippet());

                                    db.collection("placesCollection")
                                            .whereEqualTo("name", marker.getTitle())
                                            .whereEqualTo("address", marker.getSnippet())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                                {
                                                    revealedUserNameDataSet = new ArrayList<>();
                                                    reviewsDataSet = new ArrayList<>();

                                                    for (DocumentSnapshot document : task.getResult())
                                                    {
                                                        document.getReference()
                                                                .collection("CheckInsCollection")
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                                                {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                                                                    {
                                                                        CheckinInfo reviewData = new CheckinInfo();

                                                                        for (DocumentSnapshot document : task.getResult().getDocuments())
                                                                        {
                                                                            if ((boolean)document.get("IsIdentifiedCheckin"))
                                                                            {
                                                                                revealedUserNameDataSet.add((String)document.get("UserName"));
                                                                            }

                                                                            if ((boolean)document.get("IsIdentifiedCheckin"))
                                                                            {
                                                                                reviewData.setReview((String)document.get("Review"));
                                                                                reviewData.setUserName((String)document.get("UserName"));
                                                                                reviewData.setCheckInTime(new Timestamp((Date) document.get("CheckInTime")));
                                                                            }
                                                                            else
                                                                            {
                                                                                reviewData.setReview((String)document.get("Review"));
                                                                                reviewData.setUserName("Anonymous");
                                                                                reviewData.setCheckInTime(new Timestamp((Date) document.get("CheckInTime")));
                                                                            }



                                                                            reviewsDataSet.add(reviewData);

                                                                            if ((boolean)document.get("IsLocal"))
                                                                            {
                                                                                numberOfLocalVisitors++;
                                                                                if ((boolean)document.get("IsHearted"))
                                                                                {
                                                                                    numberOfLocalHearts++;
                                                                                }
                                                                            }
                                                                            else
                                                                            {
                                                                                numberOfTouristVisitors++;
                                                                                if ((boolean)document.get("IsHearted"))
                                                                                {
                                                                                    numberOfTouristHearts++;
                                                                                }
                                                                            }
                                                                            if (document.get("Review") != null)
                                                                            {
                                                                                numberOfComments++;
                                                                            }
                                                                        }

                                                                        localNumber.setText(String.valueOf(numberOfLocalVisitors));
                                                                        localUpvotes.setText(String.valueOf(numberOfLocalHearts));
                                                                        touristNumber.setText(String.valueOf(numberOfTouristVisitors));
                                                                        touristUpvotes.setText(String.valueOf(numberOfTouristHearts));
                                                                        commentsNumber.setText(String.valueOf(numberOfComments));

                                                                        placeDetailPopupWindow.showAtLocation(mDrawerLayout, Gravity.CENTER,0,0);
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                    //*************************************************** revealedUserPopupWindow ***************************************************

                                    revealedUserPopupWindow = new PopupWindow(
                                            customViewRevealedUser,
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT);

                                    revealedUserPopupWindowCloseButton = customViewRevealedUser.findViewById(R.id.close_button);
                                    revealedUserPopupWindowCloseButton.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            // Dismiss the popup window
                                            revealedUserPopupWindow.dismiss();
                                        }
                                    });

                                    revealedUserName = customViewRevealedUser.findViewById(R.id.revealedUserNameTextView);
                                    revealedUserAddress = customViewRevealedUser.findViewById(R.id.revealedUserAddressTextView);

                                    revealedUserName.setText(marker.getTitle());
                                    revealedUserAddress.setText(R.string.guest_book_title);

                                    //*************************************************** reviewsPopupWindow ***************************************************
                                    reviewsCloseButton = customViewCommentList.findViewById(R.id.close_button);
                                    reviewsCloseButton.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            // Dismiss the popup window
                                            reviewsPopupWindow.dismiss();
                                        }
                                    });

                                    // Position camera near the marker
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                                    // Return true so the default infoWindow is not shown
                                    return true;
                                }
                            });

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

                placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());

                ///////////////////////////////////////////////////////
                mPlace = new PlaceInfo();
                mPlace.setCheckinInfo(new CheckinInfo());

                // show a popup which will allow the user to pass a comment and upvotes
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View customView = inflater.inflate(R.layout.comments_and_votes_popup_window, null);

                commentAndVotesPopup = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    commentAndVotesPopup.setElevation(5.0f);
                }

                Button sendButton = customView.findViewById(R.id.commentSendButton);
                sendButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // Dismiss the popup window
                        commentAndVotesPopup.dismiss();

                        // Store the user comment, checkin as anonymous and upvote to PlaceInfo
                        EditText commentEditText = customView.findViewById(R.id.commentEditText);
                        Switch switchButton = customView.findViewById(R.id.userVisibilitySwitchButton);
                        ToggleButton upvoteToggleButton = customView.findViewById(R.id.upVoteToggleButton);

                        mPlace.getCheckinInfo().setReview(commentEditText.getText().toString());
                        mPlace.getCheckinInfo().setIdentifiedCheckIn(switchButton.isChecked());
                        mPlace.getCheckinInfo().setHearted(upvoteToggleButton.isChecked());
                        mPlace.getCheckinInfo().setCheckInTime(new Timestamp(new Date()));

                        // Place this call in the onCLick of popup:
                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                    }
                });

                // Allow user to enter comments
                commentAndVotesPopup.setFocusable(true);

                // Finally, show the popup window at the center location of root relative layout
                commentAndVotesPopup.showAtLocation(mDrawerLayout, Gravity.CENTER,0,0);

                ///////////////////////////////////////////////////////

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
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());

                MyCallBack myCallBackIfLocalCheckIn = new MyCallBack()
                {
                    @Override
                    public void onCallback(boolean localCheckIn)
                    {
                        mPlace.getCheckinInfo().setLocal(localCheckIn);
                    }
                };

                CheckIfLocalCheckIn(place, myCallBackIfLocalCheckIn);

            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            // check if the place is already exist in the firebase, if it does,
            // just increment the check in counter, otherwise create a new entry
            final String id = place.getId();
            final CharSequence name = place.getName();

            MyCallBack myCallBack = new MyCallBack()
            {
                @Override
                public void onCallback(boolean alreadyExists)
                {
                    if (alreadyExists)
                    {
                        UpdatePlaceInfo(id, name);
                    }
                    else
                    {
                        SavePlaceInfo();
                    }
                }
            };

            PlaceExistsInDB(myCallBack, place.getId(), place.getName());

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();
        }
    };

    private void CheckIfLocalCheckIn(Place place, final MyCallBack myCallBackIfLocalCheckIn)
    {
        // TODO: Instead of just checking the names of the cities, also check the cities ID
        // for example, there is Weimar in Germany and Also in Texas USA

        String savedCities = localCities.getCitiesString();
        final LatLng placeLatLng = place.getLatLng();

        final List<String> cityNames = new ArrayList<>();

        if (!savedCities.equals("empty"))
        {
            try {
                String[] citiesArray = savedCities.split("/");
                String[] cityInfo;

                for (String aCitiesArray : citiesArray) {
                    cityInfo = aCitiesArray.split("-");
                    cityNames.add(cityInfo[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getId())
                .setResultCallback(new ResultCallback<PlaceBuffer>()
                {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places)
                    {
                        if (!places.getStatus().isSuccess())
                        {
                            // Request did not complete successfully
                            return;
                        }

                        // Setup Geocoder
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses;

                        try
                        {
                            addresses = geocoder.getFromLocation(
                                    placeLatLng.latitude,
                                    placeLatLng.longitude,
                                    1);

                            if (addresses.size() > 0)
                            {
                                // Here are some results you can geocode
                                String ZIP;
                                String city;
                                String state;
                                String country;

                                if (addresses.get(0).getPostalCode() != null) {
                                    ZIP = addresses.get(0).getPostalCode();
                                    Log.d("ZIP", ZIP);
                                }

                                if (addresses.get(0).getLocality() != null) {
                                    city = addresses.get(0).getLocality();
                                    Log.d("city", city);

                                    if (cityNames.contains(city))
                                    {
                                        myCallBackIfLocalCheckIn.onCallback(true);
                                    }
                                    else
                                    {
                                        myCallBackIfLocalCheckIn.onCallback(false);
                                    }
                                }

                                if (addresses.get(0).getAdminArea() != null) {
                                    state = addresses.get(0).getAdminArea();
                                    Log.d("state", state);
                                }

                                if (addresses.get(0).getCountryName() != null) {
                                    country = addresses.get(0).getCountryName();
                                    Log.d("country", country);
                                }
                            }
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void PlaceExistsInDB(final MyCallBack myCallBack, String id, CharSequence name)
    {
        db.collection("placesCollection")
                .whereEqualTo("id", id)
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            if (task.getResult().isEmpty())
                            {
                                // return false
                                myCallBack.onCallback(false);
                            }
                            else
                            {
                                // return true
                                myCallBack.onCallback(true);
                            }
                        }
                        else
                        {
                            // Log that the call to DB failed.
                        }
                    }
                });
    }

    private void UpdatePlaceInfo(String id, CharSequence name)
    {
        db.collection("placesCollection")
                .whereEqualTo("id", id)
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task)
                     {
                         for (DocumentSnapshot document : task.getResult().getDocuments())
                         {
                             DocumentReference docReference = document.getReference();
                             final Map<String, Object> CheckInsCollection = new HashMap<>();

                             CheckInsCollection.put("IsLocal", mPlace.getCheckinInfo().isLocal());
                             CheckInsCollection.put("IsHearted", mPlace.getCheckinInfo().isHearted());
                             CheckInsCollection.put("IsIdentifiedCheckin", mPlace.getCheckinInfo().isIdentifiedCheckIn());
                             CheckInsCollection.put("Review", mPlace.getCheckinInfo().getReview());
                             CheckInsCollection.put("CheckInTime", mPlace.getCheckinInfo().getCheckInTime());

                             if (mPlace.getCheckinInfo().isIdentifiedCheckIn())
                             {
                                 // Get the username from the SP
                                 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                 String mUID = user.getUid();
                                 SharedPreferencesManagement spUserInfo = userInformation;

                                 String mUserInfo = spUserInfo.loadSPInfo();

                                 if(mUserInfo == "empty")
                                 {
                                     // DO Nothing
                                 }
                                 else
                                 {
                                     String[] userArray = mUserInfo.split("/");

                                     if(userArray.length > 2)
                                     {
                                         // Append the username to the old name
                                         CheckInsCollection.put("UserName", userArray[1]);
                                     }
                                 }
                             }

                             docReference
                                     .collection("CheckInsCollection")
                                     .add(CheckInsCollection)
                                     .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                     {
                                         @Override
                                         public void onSuccess(DocumentReference documentReference)
                                         {
                                             Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                         }
                                     })
                                     .addOnFailureListener(new OnFailureListener()
                                     {
                                         @Override
                                         public void onFailure(@NonNull Exception e)
                                         {
                                             Log.w(TAG, "Error adding document", e);
                                         }
                                     });
                         }
                     }
                });
    }

    // Store the place info in the FireStore
    private void SavePlaceInfo()
    {
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
        if (mPlace.getComment() != null)
        {
            placeCollection.put("review", mPlace.getCheckinInfo().getReview());
        }

        placeCollection.put("LatLng", new GeoPoint(mPlace.getLatlng().latitude, mPlace.getLatlng().longitude));
        placeCollection.put("Rating", mPlace.getRating());

        final Map<String, Object> checkInsCollection = new HashMap<>();

        checkInsCollection.put("IsLocal", mPlace.getCheckinInfo().isLocal());
        checkInsCollection.put("IsHearted", mPlace.getCheckinInfo().isHearted());
        checkInsCollection.put("IsIdentifiedCheckin", mPlace.getCheckinInfo().isIdentifiedCheckIn());
        checkInsCollection.put("Review", mPlace.getCheckinInfo().getReview());
        checkInsCollection.put("CheckInTime", mPlace.getCheckinInfo().getCheckInTime());


        if (mPlace.getCheckinInfo().isIdentifiedCheckIn())
        {
            // Get the username from the SP
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String mUID = user.getUid();
            SharedPreferencesManagement spUserInfo = userInformation;

            String mUserInfo = spUserInfo.loadSPInfo();
            if(mUserInfo == "empty")
            {
                //checkInsCollection.put("UserName", "Anonymous");
            }
            else
            {
                String[] userArray = mUserInfo.split("/");

                if(userArray.length > 2)
                {
                    checkInsCollection.put("UserName", userArray[1]);
                }
                else
                {
                    //checkInsCollection.put("UserName", "Anonymous");
                }
            }
        }
        else
        {
            //checkInsCollection.put("UserName", "Anonymous");
        }

        // Add a new document with a generated ID
        db.collection("placesCollection")
            .add(placeCollection)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                    db.collection("placesCollection")
                            .document(documentReference.getId())
                            .collection("CheckInsCollection")
                            .add(checkInsCollection).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                    {
                        @Override
                        public void onSuccess(DocumentReference documentReference)
                        {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //mMap.setInfoWindowAdapter(new CustomInfoWindowGoogleMap(HomeScreenActivity.this));

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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_img_marker_man))
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_img_marker_man));
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

                textCoordinates = String.format("Coordinates : %s / %s ",lat,lng);

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
