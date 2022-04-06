package app.com.muhammad.voice.ui.checkIn;

import static android.content.Context.MODE_PRIVATE;
import static app.com.muhammad.voice.Database.FirestoreFirebaseClient.getInstance;
import static app.com.muhammad.voice.util.Constants.MY_PREFERENCES;
import static app.com.muhammad.voice.util.Constants.MY_USER_AGENT;
import static app.com.muhammad.voice.util.Constants.PLACE_ADDRESS;
import static app.com.muhammad.voice.util.Constants.PLACE_TO_CHECK_IN;
import static app.com.muhammad.voice.util.Constants.USER_INFO_KEY;
import static app.com.muhammad.voice.util.Constants.USER_ZOOM;
import static app.com.muhammad.voice.util.LocationHelper.allowedToCheckIn;
import static app.com.muhammad.voice.util.LocationHelper.distanceToFromCurrentLocation;
import static app.com.muhammad.voice.util.LocationHelper.getAddresses;
import static app.com.muhammad.voice.util.LocationHelper.getLastKnownLocation;
import static app.com.muhammad.voice.util.LocationHelper.getLatLong;
import static app.com.muhammad.voice.util.LocationHelper.zoomToLocation;
import static app.com.muhammad.voice.util.UiHelperMethods.getBitmapFromVectorDrawable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.com.muhammad.voice.Adapters.PlacesRecyclerAdapter;
import app.com.muhammad.voice.Adapters.RecyclerViewReviewsAdapter;
import app.com.muhammad.voice.BaseActivity;
import app.com.muhammad.voice.DTO.CheckIn;
import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.Database.FirestoreFirebaseClient;
import app.com.muhammad.voice.Database.callbacks.FirebaseCheckInsCallback;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentCheckInBinding;
import app.com.muhammad.voice.ui.openStreetMap.OsmFragment;
import app.com.muhammad.voice.util.CustomInfoWindow;
import app.com.muhammad.voice.util.LocationHelper;

// Ref: OnItemClickListener - https://www.youtube.com/watch?v=69C1ljfDvl0
public class CheckInFragment extends Fragment implements PlacesRecyclerAdapter.OnPlaceListener
{
    private static final String TAG = "TK_CheckInFragment";

    public CheckInFragment()
    {
        // Required empty public constructor
    }

    //Variables
    private Context CONTEXT;
    private FragmentCheckInBinding binding;
    private RecyclerView mRecyclerView;
    private PlacesRecyclerAdapter mPlacesRecyclerAdapter;
    private FirestoreFirebaseClient client;
    private ArrayList<PlaceInfo> checkedInPlacesInfo;
    private ArrayList<POI> POIs;
    private Location location;
    private SearchView searchView;
    private ImageButton locationButton;
    private Button commentCloseButton;
    private Button closeListButton;
    private Button showListButton;


    //Views
    private LinearLayout poiListPopupWindow;
    private LinearLayout poiDetailPopupWindow;
    private ConstraintLayout poiCheckInWindow;
    private ConstraintLayout commentsView;
    private Button commentOpenButton;
    private MapView map;

    //Buttons
    private Button menuButtonMain;
    private FloatingActionButton checkInFAB;

    public static CheckInFragment newInstance() {
        return new CheckInFragment();
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            CONTEXT = context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentCheckInBinding.inflate(inflater, parent, false);
        View root = binding.getRoot();

        //Variables init
        map = binding.mapCheckIn;
        location = getLastKnownLocation(requireContext());
        checkInFAB = getActivity().findViewById(R.id.check_in_FAB);
        menuButtonMain = getActivity().findViewById(R.id.menu_button);
        poiListPopupWindow = binding.poiListPopupWindow.poiListPopupWindow;
        poiDetailPopupWindow = binding.poiDetailPopupWindow.placeDetailsPopupWindow;
        poiCheckInWindow = binding.poiCheckInWindow.placeCheckInWindow;
        searchView = binding.searchViewCheckIn;
        locationButton = getActivity().findViewById(R.id.currentLocationButton);
        commentsView = binding.poiDetailPopupWindow.placeCommentsListPopupCheckIn.placeCommentsPopup;
        commentOpenButton = binding.poiDetailPopupWindow.reviewsCheckInButton;
        commentCloseButton = binding.poiDetailPopupWindow.placeCommentsListPopupCheckIn.closeButton;
        closeListButton = binding.poiListPopupWindow.closeButtonPoiListPopup;
        showListButton = binding.poiListPopupWindow.showButtonPoiList;
        client = getInstance(CONTEXT);

        openStreetMapInit();

        //Arrange views visibilities
        menuButtonMain.setVisibility(View.GONE);
        checkInFAB.setVisibility(View.GONE);

        binding.menuButtonCheckIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity)getActivity()).handleDrawer();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.progressCircularCheckInPlaceList.setVisibility(View.VISIBLE);
                searchNearby(query);
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return root;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkInFAB.setVisibility(View.GONE);
        locationButton.setVisibility(View.GONE);

        Bundle arguments = getArguments();
        if (null != arguments)
        {
            checkedInPlacesInfo = (ArrayList<PlaceInfo>) arguments.get("Places");
            if (checkedInPlacesInfo == null) {
                String place = arguments.getString(PLACE_TO_CHECK_IN);
                String address = arguments.getString(PLACE_ADDRESS);

                TextView name = poiCheckInWindow.findViewById(R.id.check_in_title);
                name.setText(place);
                TextView placeAddress = poiCheckInWindow.findViewById(R.id.check_in_address);
                placeAddress.setText(address);
                poiCheckInWindow.setVisibility(View.VISIBLE);

                poiListPopupWindow.setVisibility(View.INVISIBLE);
                poiDetailPopupWindow.setVisibility(View.INVISIBLE);
                binding.searchViewCheckIn.setVisibility(View.INVISIBLE);
            } else {
                poiCheckInWindow.setVisibility(View.GONE);
                poiListPopupWindow.setVisibility(View.GONE);
                poiDetailPopupWindow.setVisibility(View.GONE);
            }
        }
        else
        {
            poiCheckInWindow.setVisibility(View.GONE);
            poiListPopupWindow.setVisibility(View.GONE);
            poiDetailPopupWindow.setVisibility(View.GONE);
        }


        commentOpenButton.setOnClickListener(view13 -> {
            TextView namePlaceDetails = poiDetailPopupWindow.findViewById(R.id.namePlaceDetail);
            TextView namePlace = commentsView.findViewById(R.id.place_name_comments_list);
            namePlace.setText(namePlaceDetails.getText());
            poiDetailPopupWindow.setVisibility(View.GONE);
            commentsView.setVisibility(View.VISIBLE);
        });

        commentCloseButton.setOnClickListener(view12 -> {
            poiDetailPopupWindow.setVisibility(View.VISIBLE);
            commentsView.setVisibility(View.GONE);
        });

        //When user clicks the close button of the list, close the list

        closeListButton.setOnClickListener(view1 -> {
            showListButton.setVisibility(View.VISIBLE);
            poiListPopupWindow.setVisibility(View.INVISIBLE);
        });
        //When user clicks the show list button, show the list again
        showListButton.setOnClickListener(view2 -> {
            poiListPopupWindow.setVisibility(View.VISIBLE);
            showListButton.setVisibility(View.INVISIBLE);
        });

        poiDetailPopupWindow.findViewById(R.id.close_button_place_detail).setOnClickListener(view1 -> poiDetailPopupWindow.setVisibility(View.GONE));

        poiDetailPopupWindow.findViewById(R.id.check_in_button).setOnClickListener(view1 ->
        {
            TextView placeName = poiCheckInWindow.findViewById(R.id.check_in_title);
            TextView placeAddress = poiCheckInWindow.findViewById(R.id.check_in_address);

            TextView name = poiDetailPopupWindow.findViewById(R.id.namePlaceDetail);
            TextView addressView = poiDetailPopupWindow.findViewById(R.id.addressPlaceDetail);
            try {
                if (allowedToCheckIn(addressView.getText().toString(), CONTEXT)) {
                    poiDetailPopupWindow.setVisibility(View.GONE);
                    placeName.setText(name.getText());
                    placeAddress.setText(addressView.getText());
                    poiCheckInWindow.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(CONTEXT, "Please move closer to: " + name.getText() + " to make a Check-In!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        poiCheckInWindow.findViewById(R.id.finish_check_in_button).setOnClickListener(view1 ->
        {
            String currentTime = getCurrentTime();
            location = getLastKnownLocation(CONTEXT);

            poiCheckInWindow.setVisibility(View.GONE);

            SwitchMaterial revealUserName = poiCheckInWindow.findViewById(R.id.userVisibilitySwitchButton);
            ToggleButton isHearted = poiCheckInWindow.findViewById(R.id.upVoteToggleButton);
            EditText comment = poiCheckInWindow.findViewById(R.id.commentEditText);

            //get ids of places
            client.getPlacesInfo(firestorePlaces -> {
                Log.d(TAG, "getPlacesCallBack: Got places. Size is " + firestorePlaces.size());

                checkedInPlacesInfo = firestorePlaces;

                PlaceInfo place = new PlaceInfo();
                CheckIn checkIn = new CheckIn();
                place.setName(binding.poiCheckInWindow.checkInTitle.getText().toString());
                place.setAddress(binding.poiCheckInWindow.checkInAddress.getText().toString());
                SharedPreferences preferences =  CONTEXT.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
                Set<String> userInfo = preferences.getStringSet(USER_INFO_KEY, null);
                String userName = "Anonymous";
                String email = "";
                if (userInfo != null) {
                    Iterator<String> iterator = userInfo.iterator();
                    email = iterator.next();
                    userName = iterator.next();
                }

                if (!revealUserName.isChecked()){
                    Log.d(TAG, "TK_saveCheckIn: User wishes to check in as anonymous check in.");
                    checkIn.setUserName("Anonymous");
                }
                else if (userName == null || userName.equals("")){
                    Log.d(TAG, "TK_SaveCheckIn: userName is not saved. Will save it as an anonymous check in");
                    checkIn.setUserName("Anonymous");
                }
                else{
                    checkIn.setUserName(userName);
                }
                checkIn.setIdentifiedCheckIn(revealUserName.isChecked());
                checkIn.setHearted(isHearted.isChecked());
                checkIn.setLocal(isLocalCheckIn(place));
                checkIn.setReview(comment.getText().toString());
                checkIn.setCheckInTime(currentTime);

                if (place.getCheckIns() == null) {
                    ArrayList<CheckIn> checkIns = new ArrayList<>();
                    checkIns.add(checkIn);
                    place.setCheckIns(checkIns);
                } else
                {
                    place.getCheckIns().add(checkIn);
                }

                //set id
                boolean firstCheckIn = true;
                for (PlaceInfo placeInfo : checkedInPlacesInfo) {
                    if (placeInfo.getName() != null) {
                        if (placeInfo.getName().equals(place.getName())) {
                            place.setId(placeInfo.getId() != null ? placeInfo.getId() : null);
                            updatePlaceCheckIns(place);
                            firstCheckIn = false;
                            break;
                        }
                        else {
                            Log.d(TAG, "TK_getPlacesCallBack: New Check In! Details not yet known, get it from overpassAPI!");
                        }
                    }
                }
                if (firstCheckIn) {
                    savePlaceInfo(place);
                    Toast.makeText(CONTEXT, "Checked-In successfully at: " + place.getName(), Toast.LENGTH_SHORT).show();}

                switchFragment(OsmFragment.newInstance());
            });
        });
    }

    private void updatePlaceCheckIns(PlaceInfo place)
    {
        final Map<String, Object> checkInsCollection = new HashMap<>();

        checkInsCollection.put("id", place.getCheckIns().get(0).getId());
        checkInsCollection.put("local", place.getCheckIns().get(0).isLocal());
        checkInsCollection.put("hearted", place.getCheckIns().get(0).isHearted());
        checkInsCollection.put("identifiedCheckIn", place.getCheckIns().get(0).isIdentifiedCheckIn());
        checkInsCollection.put("checkInTime", place.getCheckIns().get(0).getCheckInTime());
        checkInsCollection.put("userName", place.getCheckIns().get(0).getUserName());

        if (!place.getCheckIns().get(0).getReview().equals(""))
        {
            checkInsCollection.put("review", place.getCheckIns().get(0).getReview());
        }

        client.updateCheckInsDocument(checkInsCollection, place);
    }

    // Store the place info in the FireStore
    private void savePlaceInfo(PlaceInfo place)
    {
        Map<String, Object> placeCollection = new HashMap<>();
        if (place.getName() != null)
        {
            placeCollection.put("name", place.getName());
        }
        if (place.getAddress() != null)
        {
            placeCollection.put("address", place.getAddress());
        }
        if (place.getId() != null)
        {
            placeCollection.put("id", place.getId());
        }
        if (place.getPhoneNumber() != null)
        {
            placeCollection.put("phone", place.getPhoneNumber());
        }
        if (place.getWebsiteUri() != null)
        {
            placeCollection.put("website", place.getWebsiteUri().toString());
        }

        ///Place
        com.google.firebase.firestore.GeoPoint latLong = null;
        try {
            latLong = getLatLong(place, CONTEXT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        placeCollection.put("latLng", new com.google.firebase.firestore.GeoPoint(latLong.getLatitude(), latLong.getLongitude()));
        placeCollection.put("rating", place.getRating());

        final Map<String, Object> checkInsCollection = new HashMap<>();

        checkInsCollection.put("id", place.getCheckIns().get(0).getId());
        checkInsCollection.put("local", place.getCheckIns().get(0).isLocal());
        checkInsCollection.put("hearted", place.getCheckIns().get(0).isHearted());
        checkInsCollection.put("identifiedCheckIn", place.getCheckIns().get(0).isIdentifiedCheckIn());
        checkInsCollection.put("checkInTime", place.getCheckIns().get(0).getCheckInTime());
        checkInsCollection.put("userName", place.getCheckIns().get(0).getUserName());

        if (!place.getCheckIns().get(0).getReview().equals(""))
        {
            checkInsCollection.put("review", place.getCheckIns().get(0).getReview());
        }

        client.savePlace(placeCollection, checkInsCollection);
    }

    private boolean isLocalCheckIn(PlaceInfo place) {
        // TODO: Instead of just checking the names of the cities, also check the cities ID for example, there is Weimar in Germany and Also in Texas USA
        SharedPreferences preferences =  CONTEXT.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        String localCity = preferences.getString("localCity", null);
        if (localCity == null || localCity.equals("")) {
            Log.d(TAG, "TK_CheckIfLocalCheckIn: localCity is not saved. Skipping checking if check in is local and saving CheckIn as tourist check in");
            return false;
        }
        String address = place.getAddress();
        String[] localCitySplit = localCity.split(",");

        return address.toLowerCase(Locale.ROOT).contains(localCitySplit[0].toLowerCase(Locale.ROOT));
        //TODO: This is baddly designe method. fix it
    }

    private void searchNearby(String query) {

        //TODO: Maybe show a spinner or loading bar that search is happening because user is not sure what is happening
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(() -> {
            Configuration.getInstance().load(CONTEXT, PreferenceManager.getDefaultSharedPreferences(CONTEXT));
            Configuration.getInstance().setUserAgentValue(MY_USER_AGENT);

            BoundingBox currentLocationBB = LocationHelper.getCurrentLocationBoundingBox(location);
//            String urlNew = "http://api.geonames.org/findNearbyPOIsOSM?lat=37.451&lng=-122.18&username=demo";
//            GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("moh_themoh");
//            poiProvider.getPOIInside(currentLocationBB, 100);
            GeoNamesPOIProvider geoNamesPOIProvider = new GeoNamesPOIProvider("moh_themoh");
            ArrayList<POI> poisGeo = geoNamesPOIProvider.getPOIInside(currentLocationBB, 15);

            //Log.i(TAG, "searchNearby: returned " + poisGeo.size());

            OverpassAPIProvider overpassAPIProvider = new OverpassAPIProvider();
            //TODO: Hard coded, use and/or to use multiple tags.
            String url = overpassAPIProvider.urlForPOISearch("amenity="+query, currentLocationBB, 100, 10);

            // Get POIs
            POIs = overpassAPIProvider.getPOIsFromUrl(url);
                handler.post(() -> {

                    if (POIs == null || POIs.size() == 0){
                        binding.progressCircularCheckInPlaceList.setVisibility(View.GONE);
                        Toast.makeText(CONTEXT, "Could not find any places with: " + query +". Please refine your search.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        POIs.size();
                    }
                    // Get Bounding box around the pois
                    BoundingBox poIsBoundingBox = LocationHelper.getPOIsBoundingBox(POIs);

                    //TODO: Goto the DB, get the collection of Places
                    // use the collection to get checkIns collection
                    // attach that to the places list

                    ArrayList<PlaceInfo> overpassApiPlacesInfo = new ArrayList<>();
                    for (POI poi: POIs) {
                        PlaceInfo place = new PlaceInfo();
                        com.google.firebase.firestore.GeoPoint geoPoint =
                                new com.google.firebase.firestore.GeoPoint(poi.mLocation.getLatitude(), poi.mLocation.getLongitude());
                        place.setLatLng(geoPoint);
                        List<Address> addresses = new ArrayList<>();
                        try {
                             addresses = getAddresses(poi, CONTEXT);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                        String address = "Not Known";
                        String knownName = "Unknown";

                        if (addresses != null && addresses.size() > 0 && addresses.get(0).getAddressLine(0) != null) {
                            address = addresses.get(0).getAddressLine(0);
                            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                        } else {
                            Log.e(TAG, "onPlaceClick: Unable to get street address for the: " + poi.mId + " Location: " + poi.mLocation);
                        }
                        place.setName(poi.mType);
                        place.setAddress(address);
                        place.setRating(poi.mRank);
                        overpassApiPlacesInfo.add(place);
                    }
                    Collections.sort(overpassApiPlacesInfo, new Comparator<PlaceInfo>(){
                        @Override
                        public int compare(PlaceInfo lhs, PlaceInfo rhs) {
                            double distanceLhs = distanceToFromCurrentLocation(lhs.getLatLng(), CONTEXT);
                            double distanceRhs = distanceToFromCurrentLocation(rhs.getLatLng(), CONTEXT);

                            return Double.compare(distanceLhs, distanceRhs);
                        }
                    });
                    mRecyclerView = binding.poiListPopupWindow.nearbyPlacesRecyclerView;
                    mPlacesRecyclerAdapter = new PlacesRecyclerAdapter(getActivity(), overpassApiPlacesInfo, this);
                    mRecyclerView.setAdapter(mPlacesRecyclerAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(CONTEXT));

                    //Clusters
                    RadiusMarkerClusterer poiMarkerCluster = new RadiusMarkerClusterer(CONTEXT);
                    if (POIs.size() > 1) {
                        Bitmap clusterIcon = getBitmapFromVectorDrawable(CONTEXT, R.drawable.marker_cluster);
                        poiMarkerCluster.setIcon(clusterIcon);

                        // CLuster Design
                        poiMarkerCluster.getTextPaint().setColor(Color.DKGRAY);
                        poiMarkerCluster.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density); //taking into account the screen density
                        poiMarkerCluster.mAnchorU = Marker.ANCHOR_RIGHT;
                        poiMarkerCluster.mAnchorV = Marker.ANCHOR_BOTTOM;
                        poiMarkerCluster.mTextAnchorV = 0.40f;

                        map.getOverlays().add(poiMarkerCluster);
                    }

                    //Drop Pins
                    Drawable poiIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_place, null);
                    for (POI poi : POIs){
                        Marker poiMarker = new Marker(map);
                        poiMarker.setInfoWindow(null);
                        poiMarker.setIcon(poiIcon);
                        poiMarker.setPosition(poi.mLocation);
                        poiMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                zoomToLocation(map, marker.getPosition());
                                binding.poiListPopupWindow.poiListPopupWindow.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });


                        poiMarker.setTitle(poi.mType != null ? poi.mType : "No Name");
                        poiMarker.setSnippet(poi.mDescription != null ? poi.mDescription : "No Description");
                        poiMarker.setPosition(poi.mLocation);
                        poiMarker.setIcon(poiIcon);
                        if (poi.mThumbnail != null){
                            poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
                        }

                        //Here is where we set the custom Window
                        poiMarker.setInfoWindow(new CustomInfoWindow(map));
                        poiMarker.setRelatedObject(poi);
                        poiMarkerCluster.add(poiMarker);
                    }

                    IMapController mapController = map.getController();
                    map.zoomToBoundingBox(poIsBoundingBox, true);
                    map.invalidate();

                    binding.progressCircularCheckInPlaceList.setVisibility(View.GONE);
                    binding.poiListPopupWindow.poiListPopupWindow.setVisibility(View.VISIBLE);
                });
        });

    }

    private void openStreetMapInit() {
        // Needs extrenal storage to write tiles
        IConfigurationProvider osmConfig = org.osmdroid.config.Configuration.getInstance();
        File basePath = new File(CONTEXT.getCacheDir().getAbsolutePath(), "osmdroid");
        File tileCache = new File(osmConfig.getOsmdroidBasePath().getAbsolutePath(), "tile");
        osmConfig.setOsmdroidBasePath(basePath);
        osmConfig.setOsmdroidTileCache(tileCache);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            //Set user agent
            osmConfig.load(CONTEXT, PreferenceManager.getDefaultSharedPreferences(CONTEXT));
            osmConfig.setUserAgentValue(MY_USER_AGENT);

            //UI thread here, after osmInit
            handler.post(() -> {
                map.setTileSource(TileSourceFactory.MAPNIK);
                map.setMultiTouchControls(true);
                map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER); // removing default zoom controllers

                MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(CONTEXT), map);
                myLocationNewOverlay.enableMyLocation();

                Bitmap bitmapNotMoving = getBitmapFromVectorDrawable(CONTEXT, R.drawable.ic_marker_man);
                Bitmap bitmapMoving = getBitmapFromVectorDrawable(CONTEXT, R.drawable.ic_current_location_circle);
                myLocationNewOverlay.setDirectionArrow( bitmapNotMoving, bitmapMoving);
                myLocationNewOverlay.enableMyLocation();
                map.getOverlays().add(myLocationNewOverlay);
                Location location = getLastKnownLocation(CONTEXT);
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                map.getController().setZoom(USER_ZOOM);
                map.getController().animateTo(geoPoint);
                //zoomToLocation(map, geoPoint);
                map.invalidate();
            });
        });
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        //this.listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getActivity().findViewById(R.id.activity_base_tool_bar).setVisibility(View.VISIBLE);

        //removeListener();
        binding = null;
    }

    @Override
    public void onPlaceClick(View view, int position) {
        binding.searchViewCheckIn.setVisibility(View.GONE);
        // open details of the poi
        Log.d("TAG", "TK_onItemClick: showing place detail popup window");

        Collections.sort(POIs, new Comparator<POI>(){
            @Override
            public int compare(POI lhs, POI rhs) {
                com.google.firebase.firestore.GeoPoint lhsGeoPoint = new com.google.firebase.firestore.GeoPoint(lhs.mLocation.getLatitude(), lhs.mLocation.getLongitude());
                com.google.firebase.firestore.GeoPoint rhsGeoPoint = new com.google.firebase.firestore.GeoPoint(rhs.mLocation.getLatitude(), rhs.mLocation.getLongitude());
                double distanceLhs = distanceToFromCurrentLocation(lhsGeoPoint, CONTEXT);
                double distanceRhs = distanceToFromCurrentLocation(rhsGeoPoint, CONTEXT);

                return Double.compare(distanceLhs, distanceRhs);
            }
        });

        POI poi = POIs.get(position);

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = getAddresses(poi, CONTEXT);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        String address = "Not Known";
        String knownName = "Unknown";

        if (addresses != null && addresses.size() > 0 && addresses.get(0).getAddressLine(0) != null) {
            address = addresses.get(0).getAddressLine(0);
            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        }

        PlaceInfo placeInfoRequested = new PlaceInfo();

        //check if the user picked a place where we have check ins already:
        for (PlaceInfo placeInfo: checkedInPlacesInfo) {
            if (poi.mType.equals(placeInfo.getName())) {
                placeInfoRequested = placeInfo;
            }
        }

        String finalAddress = address;
        String finalKnownName = poi.mType;
        TextView placeName = poiDetailPopupWindow.findViewById(R.id.namePlaceDetail);
        TextView placeAddress = poiDetailPopupWindow.findViewById(R.id.addressPlaceDetail);

        if (null == placeInfoRequested.getName()) {
            Log.i(TAG, "onPlaceClick: Following place is not checked in: " + poi.mType + ". Skipping find firebase check-ins");
            placeName.setText(poi.mType);
            placeAddress.setText(address);
            poiDetailPopupWindow.setVisibility(View.VISIBLE);
        }
        else {
            Log.i(TAG, "onPlaceClick: id is: " + placeInfoRequested.getId());
            client.getCheckInForPlace(placeInfoRequested.getId(), new FirebaseCheckInsCallback() {
                @Override
                public void getCheckInsCallBack(ArrayList<CheckIn> checkIns) {

                    int localVisits = 0;
                    int touristVisits = 0;
                    int localLikes = 0;
                    int touristLikes = 0;
                    int reviews = 0;
                    for (CheckIn checkin : checkIns){
                        if (checkin.isLocal()) {
                            localVisits = localVisits + 1;
                            if (checkin.isHearted()) {
                                localLikes = localLikes + 1;
                            }
                        } else {
                            touristVisits = touristVisits + 1;
                            if (checkin.isHearted()) {
                                touristLikes = touristLikes + 1;
                            }
                        }
                        if (!checkin.getReview().equals("")) {
                            reviews = reviews + 1;
                        }
                    }

                    RecyclerView commentsRecyclerView = binding.poiDetailPopupWindow.placeCommentsListPopupCheckIn.usersCommentsList;
                    RecyclerViewReviewsAdapter recyclerViewReviewsAdapter = new RecyclerViewReviewsAdapter(CONTEXT, checkIns);
                    commentsRecyclerView.setAdapter(recyclerViewReviewsAdapter);
                    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(CONTEXT));

                    binding.poiDetailPopupWindow.namePlaceDetail.setText(finalKnownName);
                    binding.poiDetailPopupWindow.addressPlaceDetail.setText(finalAddress);
                    binding.poiDetailPopupWindow.localNumber.setText(String.valueOf(localVisits));
                    binding.poiDetailPopupWindow.localUpvotes.setText(String.valueOf(localLikes));
                    binding.poiDetailPopupWindow.touristNumber.setText(String.valueOf(touristVisits));
                    binding.poiDetailPopupWindow.touristUpvotes.setText(String.valueOf(touristLikes));
                    binding.poiDetailPopupWindow.reviewsCountCheckIn.setText(String.valueOf(reviews));

                    poiDetailPopupWindow.setVisibility(View.VISIBLE);
                }
            });
        }

        IMapController controller = map.getController();
        controller.setCenter(poi.mLocation);
        controller.setZoom(USER_ZOOM);
        controller.animateTo(poi.mLocation);
        map.invalidate();

        poiListPopupWindow.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTime() {

        //Want to return this: April 1st, 2022 11:44 A.M.

        String europeanDatePattern = "dd MMMM, yyyy hh:mm a";
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(europeanDatePattern);

        LocalDateTime currentDate = LocalDateTime.now();

        String formattedCurrentDate = europeanDateFormatter.format(currentDate);

        return formattedCurrentDate;
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(CONTEXT,
                PreferenceManager.getDefaultSharedPreferences(CONTEXT));
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(CONTEXT,
                PreferenceManager.getDefaultSharedPreferences(CONTEXT));
        if (map != null) {
            map.onPause();
        }
    }

    private void switchFragment(OsmFragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack("CheckIn2OSM");
        fragmentTransaction.commit();
    }

    // Fragment setup reference: https://guides.codepath.com/android/creating-and-using-fragments
}