package app.com.muhammad.voice.ui.checkIn;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.com.muhammad.voice.Adapters.RecyclerViewAdapter;
import app.com.muhammad.voice.DTO.CheckInInfo;
import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentCheckInBinding;
import app.com.muhammad.voice.utils.CustomInfoWindow;
import app.com.muhammad.voice.utils.LocationHelper;

import static app.com.muhammad.voice.utils.ConstantsVariables.MY_USER_AGENT;
import static app.com.muhammad.voice.utils.LocationHelper.getAddresses;
import static app.com.muhammad.voice.utils.LocationHelper.getCurrentUserLocation;
import static app.com.muhammad.voice.utils.LocationHelper.getLatLong;
import static app.com.muhammad.voice.utils.LocationHelper.removeListener;
import static app.com.muhammad.voice.utils.UiHelperMethods.getBitmapFromVectorDrawable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// Ref: OnItemClickListener - https://www.youtube.com/watch?v=69C1ljfDvl0
public class CheckInFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener
{
    private static final String MY_PREFERENCES = "my_preferences";
    private static final String TAG = "CheckInFragment";

    private CollectionReference collectionReference;
 
    private FragmentActivity listener;
    private Context CONTEXT;

    private CheckInViewModel viewModel;
    private FragmentCheckInBinding binding;
    private MapView map;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private ArrayList<POI> POIs;

    private RecyclerView nearByPlacesRecyclerView;
    private RecyclerViewAdapter nearbyPOIsAdapter;

    private LocationHelper locationHandler;
    private Location location;

    private FirebaseFirestore database;

    SharedPreferences preferences;
    final String PREFERENCE_KEY = "my_preferences";

    public CheckInFragment()
    {
        // Required empty public constructor
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
            CONTEXT = context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Do Init here
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CheckInViewModel.class);
        binding = FragmentCheckInBinding.inflate(inflater, parent, false);
        View root = binding.getRoot();

        return root;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ActionBar
        Toolbar toolbar = binding.toolbarCheckInLayout.toolbarCheckIn;

        //Collection reference - Firestore
        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection("placesCollection");

        //Views
        LinearLayout poiListPopupWindow = binding.poiListPopupWindow.poiListPopupWindow;
        LinearLayout poiDetailPopupWindow = binding.poiDetailPopupWindow.placeDetailsPopupWindow;
        ConstraintLayout poiCheckInWindow = binding.poiCheckInWindow.placeCheckInWindow;
        Button closeButton = binding.poiListPopupWindow.closeButtonPoiList;

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.check_in_menu);

        //Arranging views
        poiListPopupWindow.setVisibility(View.GONE);
        poiDetailPopupWindow.setVisibility(View.GONE);
        poiCheckInWindow.setVisibility(View.GONE);
        getActivity().findViewById(R.id.menu_button).setVisibility(View.GONE);
        getActivity().findViewById(R.id.activity_base_tool_bar).setVisibility(View.GONE);

        //When user clicks the close button of the list, close the list
        closeButton.setOnClickListener(view1 -> poiListPopupWindow.setVisibility(View.GONE));

        poiDetailPopupWindow.findViewById(R.id.close_button).setOnClickListener(view1 -> poiDetailPopupWindow.setVisibility(View.GONE));
        poiDetailPopupWindow.findViewById(R.id.check_in_button).setOnClickListener(view1 ->
        {
            poiDetailPopupWindow.setVisibility(View.GONE);
            TextView placeName = poiCheckInWindow.findViewById(R.id.check_in_title);
            TextView placeAddress = poiCheckInWindow.findViewById(R.id.check_in_address);

            TextView name = poiDetailPopupWindow.findViewById(R.id.namePlaceDetail);
            TextView addressView = poiDetailPopupWindow.findViewById(R.id.addressPlaceDetail);

            placeName.setText(name.getText());
            placeAddress.setText(addressView.getText());

            poiCheckInWindow.setVisibility(View.VISIBLE);
        });
        poiCheckInWindow.findViewById(R.id.finish_check_in_button).setOnClickListener(view1 ->
        {
            // TODO: when user finish check in, hide view and store the info in a checkin object and save it to Firestore
            poiCheckInWindow.setVisibility(View.GONE);

            SwitchMaterial revealUserName = poiCheckInWindow.findViewById(R.id.userVisibilitySwitchButton);
            ToggleButton isHearted = poiCheckInWindow.findViewById(R.id.upVoteToggleButton);

            PlaceInfo place = new PlaceInfo();
            CheckInInfo checkinInfo = new CheckInInfo();

            place.setName(binding.poiCheckInWindow.checkInTitle.getText().toString());
            place.setAddress(binding.poiCheckInWindow.checkInAddress.getText().toString());

            SharedPreferences preferences =  CONTEXT.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
            Set<String> userInfo = preferences.getStringSet("user-info", null);
            Iterator<String> iterator = userInfo.iterator();

            String email = iterator.next();
            String userName = iterator.next();
            if (!revealUserName.isChecked()){
                Log.d(TAG, "saveCheckIn: User wishes to check in as anonymous check in.");
                checkinInfo.setUserName("Anonymous");
            }
            else if (userName == null || userName.equals("")){
                Log.d(TAG, "SaveCheckIn: userName is not saved. Will save it as an anonymous check in");
                checkinInfo.setUserName("Anonymous");
            }
            else{
                checkinInfo.setUserName(userName);
            }
            checkinInfo.setIdentifiedCheckIn(revealUserName.isChecked());
            checkinInfo.setHearted(isHearted.isChecked());
            checkinInfo.setLocal(isLocalCheckIn(place));

            place.setCheckinInfo(checkinInfo);

            saveCheckIn(place);

//            // retrieve
//            database.collection("placesCollection")
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//
//                                //TODO: Drop pins on the map
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    });

        });

        location = getCurrentUserLocation(CONTEXT);

        openStreetMapInit();
    }

    // Store the place info in the FireStore
    private void saveCheckIn(PlaceInfo place)
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
        if (place.getComment() != null)
        {
            placeCollection.put("review", place.getCheckinInfo().getReview());
        }

        ///Place
        com.google.firebase.firestore.GeoPoint latLong = null;
        try { 
            latLong = getLatLong(place, CONTEXT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        placeCollection.put("LatLng", new com.google.firebase.firestore.GeoPoint(latLong.getLatitude(), latLong.getLongitude()));
        placeCollection.put("Rating", place.getRating());

        final Map<String, Object> checkInsCollection = new HashMap<>();

        checkInsCollection.put("IsLocal", place.getCheckinInfo().isLocal());
        checkInsCollection.put("IsHearted", place.getCheckinInfo().isHearted());
        checkInsCollection.put("IsIdentifiedCheckin", place.getCheckinInfo().isIdentifiedCheckIn());
        //checkInsCollection.put("CheckInTime", place.getCheckinInfo().getCheckInTime());

//        if (!place.getCheckinInfo().getReview().equals(""))
//        {
//            checkInsCollection.put("Review", place.getCheckinInfo().getReview());
//        }

        // Add a new document with a generated ID
        collectionReference
                .add(placeCollection)
                .addOnSuccessListener((OnSuccessListener<DocumentReference>) documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                    collectionReference
                            .document(documentReference.getId())
                            .collection("CheckInsCollection")
                            .add(checkInsCollection).addOnSuccessListener(documentReference1 -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference1.getId())).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    private boolean isLocalCheckIn(PlaceInfo place) {
        // TODO: Instead of just checking the names of the cities, also check the cities ID for example, there is Weimar in Germany and Also in Texas USA
        SharedPreferences preferences =  CONTEXT.getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        String localCity = preferences.getString("localCity", null);
        if (localCity == null || localCity.equals("")) {
            Log.d(TAG, "CheckIfLocalCheckIn: localCity is not saved. Skipping checking if check in is local and saving CheckIn as tourist check in");
            return false;
        }
        String address = place.getAddress();
        String[] localCitySplit = localCity.split(",");

        return address.toLowerCase(Locale.ROOT).contains(localCitySplit[0].toLowerCase(Locale.ROOT));
    }

    private void searchNearby(String query) {

        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(() -> {
            Configuration.getInstance().load(CONTEXT, PreferenceManager.getDefaultSharedPreferences(CONTEXT));
            Configuration.getInstance().setUserAgentValue(MY_USER_AGENT);

            OverpassAPIProvider overpassAPIProvider = new OverpassAPIProvider();
            //TODO: Hard coded, use and/or to use multiple tags.
            BoundingBox currentLocationBB = LocationHelper.getCurrentLocationBoundingBox(location);
            String url = overpassAPIProvider.urlForPOISearch("amenity=bar", currentLocationBB, 10, 15);

            // Get POIs
            POIs = overpassAPIProvider.getPOIsFromUrl(url);

            if (POIs != null && POIs.size() > 0){
                handler.post(() -> {

                    // Get Bounding box at current location
                    BoundingBox poIsBoundingBox = LocationHelper.getPOIsBoundingBox(POIs);

                    // TODO: Populate the list
                    ArrayList<PlaceInfo> places = new ArrayList<>();

                    for (POI poi: POIs) {
                        PlaceInfo place = new PlaceInfo();
                        place.setName(poi.mType);
                        place.setAddress(poi.mLocation.toString());
                        place.setRating(poi.mRank);

                        places.add(place);
                    }

                    nearByPlacesRecyclerView = binding.poiListPopupWindow.usersRevealedListRecyclerView;
                    nearbyPOIsAdapter = new RecyclerViewAdapter(getActivity(), places, this);
                    nearByPlacesRecyclerView.setAdapter(nearbyPOIsAdapter);
                    nearByPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(CONTEXT));

                    //TODO:
                    // 1) Remove clusters, show individual markers
                    // 2) Customize the markers drawable: use small with icon for restaurant, bar, church etc
                    // 3) When the user clicks search, zoom to a BB which fits all POIs and set the zoom to around 17.0 (ref: Google Maps)
                    // -DONE- 4) Optimize this fragment with its methods and calls to osm APIs
                    // -DONE- 5) Attach the list (recyclerView) and populate the list with the POIs from the API
                    // 6) Set onClickListener when the user clicks on an item in the list and then show details about the place in an info window
                    // 7) Find a better map design (icons, roads, colors, buildings, etc.)
                    // 8) Check to see if the new way will give us different terrains and night mode map

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
                    Drawable poiIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_man, null);
                    for (POI poi : POIs){
                        Marker poiMarker = new Marker(map);
                        poiMarker.setTitle(poi.mType != null ? poi.mType : "No Name");
                        poiMarker.setSnippet(poi.mDescription != null ? poi.mDescription : "No Description");
                        poiMarker.setPosition(poi.mLocation);
                        poiMarker.setIcon(poiIcon);
                        if (poi.mThumbnail != null){
                            poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
                        }
                        // TODO: Here is where we set the custom Window
                        poiMarker.setInfoWindow(new CustomInfoWindow(map));
                        poiMarker.setRelatedObject(poi);
                        poiMarkerCluster.add(poiMarker);
                    }

                    IMapController mapController = map.getController();
                    mapController.setZoom(17.0);
                    map.zoomToBoundingBox(poIsBoundingBox, true);
                    map.invalidate();

                    binding.poiListPopupWindow.poiListPopupWindow.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void openStreetMapInit(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            //Set user agent
            final Context context = getActivity();
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            Configuration.getInstance().setUserAgentValue(MY_USER_AGENT);

            handler.post(() -> {
                map = binding.mapCheckIn;
                map.setTileSource(TileSourceFactory.MAPNIK);
                map.setMultiTouchControls(true);

                MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), map);
                myLocationNewOverlay.enableMyLocation();
                map.getOverlays().add(myLocationNewOverlay);

                IMapController mapController = map.getController();
                mapController.setZoom(17.0);

                // The reason i am not using position from myLocationNewOverlay is because it returns 0,0.
                // Maybe it is because of the sensors have not fired and the onPositionChangeUpdate listener have not be called
                mapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                map.invalidate();
            });
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.check_in_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.nav_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange: %s", newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit: %s", query);
                    //clear all the markers from the map except myoverlay
                    // TODO: Search for whatever the user is requesting
                    searchNearby(query);
                    searchView.clearFocus();
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getActivity().findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.activity_base_tool_bar).setVisibility(View.VISIBLE);

        removeListener();
        binding = null;
    }

    @Override
    public void onItemClick(View view, int position) {
        // open details of the poi
        Log.d("TAG", "onItemClick: showing place detail popup window");

        // Also zoom to the marker after showing the popup window
        LinearLayout linearLayout = binding.poiDetailPopupWindow.placeDetailsPopupWindow;

        TextView name = linearLayout.findViewById(R.id.namePlaceDetail);
        TextView addressView = linearLayout.findViewById(R.id.addressPlaceDetail);
        TextView localVisitors = linearLayout.findViewById(R.id.localNumber);
        TextView localLikes = linearLayout.findViewById(R.id.localUpvotes);
        TextView touristVisitors = linearLayout.findViewById(R.id.touristNumber);
        TextView touristLikes = linearLayout.findViewById(R.id.touristUpvotes);

        POI place = POIs.get(position);

        List<Address> addresses = null;
        try {
            addresses = getAddresses(place, CONTEXT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        name.setText(place.mType != null ? place.mType : knownName);
        addressView.setText(address);

        binding.poiListPopupWindow.poiListPopupWindow.setVisibility(View.INVISIBLE);
        binding.poiDetailPopupWindow.placeDetailsPopupWindow.setVisibility(View.VISIBLE);
    }

    // Fragment setup reference: https://guides.codepath.com/android/creating-and-using-fragments
}