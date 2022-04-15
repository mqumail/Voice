package app.com.muhammad.voice.ui.openStreetMap;

import static app.com.muhammad.voice.Database.FirestoreFirebaseClient.getInstance;
import static app.com.muhammad.voice.util.Constants.CONTINENT_ZOOM;
import static app.com.muhammad.voice.util.Constants.EUROPE_BB;
import static app.com.muhammad.voice.util.Constants.MY_USER_AGENT;
import static app.com.muhammad.voice.util.Constants.PERMISSION_REQUEST_PRECISE_LOCATION;
import static app.com.muhammad.voice.util.Constants.PLACES_COLLECTION_PATH;
import static app.com.muhammad.voice.util.Constants.PLACE_ADDRESS;
import static app.com.muhammad.voice.util.Constants.PLACE_TO_CHECK_IN;
import static app.com.muhammad.voice.util.Constants.USER_ZOOM;
import static app.com.muhammad.voice.util.LocationHelper.allowedToCheckIn;
import static app.com.muhammad.voice.util.LocationHelper.getLastKnownLocation;
import static app.com.muhammad.voice.util.UiHelperMethods.getBitmapFromVectorDrawable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.com.muhammad.voice.Adapters.RecyclerViewReviewsAdapter;
import app.com.muhammad.voice.DTO.CheckIn;
import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.Database.FirestoreFirebaseClient;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentOsmBinding;
import app.com.muhammad.voice.ui.checkIn.CheckInFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OsmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OsmFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, RecyclerViewReviewsAdapter.OnCommentsListener {
    private static final String TAG = "TK_OsmFragment";
    private OsmViewModel viewModel;
    private FragmentOsmBinding binding;
    private MapView map;
    private ArrayList<PlaceInfo> places = new ArrayList<>();
    private ArrayList<CheckIn> currentPlaceCheckIns = new ArrayList<>();
    ArrayList<PlaceInfo> placesInfoWithCheckIns = new ArrayList<>();

    private HashMap<String, PlaceInfo> placesWithMarkerId = new HashMap<>();

    private FirestoreFirebaseClient client;
    private String placeToCheckInto;

    ProgressBar progressBar;
    ProgressBar progressBarPlaceDetail;
    FrameLayout mainContent;

    private View mLayout;

    private Context CONTEXT;

    public OsmFragment() {
        // Required empty public constructor
    }

    public static OsmFragment newInstance() {
        OsmFragment osmFragment = new OsmFragment();
        Bundle args = new Bundle();
        return osmFragment;
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "TK_onAttach: START 1.1");
        super.onAttach(context);
        if (context instanceof Activity){
            CONTEXT = context;
        }
        Log.d(TAG, "TK_onAttach: END - 1.2");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "TK_onCreate: START - 2.0");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "TK_onCreate: END - 2.1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "TK_onCreateView: START - 3.0");
        viewModel = new ViewModelProvider(this).get(OsmViewModel.class);
        binding = FragmentOsmBinding.inflate(inflater, container, false);
        map = binding.map;
        openStreetMapInit();
        progressBarPlaceDetail = binding.progressCircularOsmPlaceDetail;
        mainContent = binding.osmFragmentFrameLayoutContainer;
        mLayout = binding.getRoot();
        Log.d(TAG, "TK_onCreateView: END - 3.1");
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "TK_onViewCreated: START - 4.0");
        super.onViewCreated(view, savedInstanceState);
        binding.poiDetailPopupWindowOsm.placeCustomWindowOsm.setVisibility(View.INVISIBLE);

        binding.checkInFAB.setVisibility(View.VISIBLE);

        binding.checkInFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInFragment fragment = CheckInFragment.newInstance();
                Bundle arguments = new Bundle();
                arguments.putSerializable("Places", places);
                fragment.setArguments(arguments);
                switchFragment(fragment);
                binding.checkInFAB.setVisibility(View.GONE);
            }
        });

        Button closePlaceDetailInfoWindow = binding.poiDetailPopupWindowOsm.closeButtonOsm;
        Button checkIn = binding.poiDetailPopupWindowOsm.checkInButtonOsm;
        closePlaceDetailInfoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.check_in_FAB).setVisibility(View.VISIBLE);
                binding.poiDetailPopupWindowOsm.placeCustomWindowOsm.setVisibility(View.GONE);
            }
        });
        checkIn.setOnClickListener(view1 -> {
            String name = binding.poiDetailPopupWindowOsm.namePlaceDetailOsm.getText().toString();
            String address = binding.poiDetailPopupWindowOsm.addressPlaceDetailOsm.getText().toString();
            if (ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (allowedToCheckIn(address, CONTEXT)) {
                        getCurrentLocation();
                        CheckInFragment fragment = CheckInFragment.newInstance();
                        Bundle arguments = new Bundle();
                        binding.progressCircularOsmPlaceDetail.setVisibility(View.GONE);
                        arguments.putString(PLACE_TO_CHECK_IN, name);
                        arguments.putString(PLACE_ADDRESS, address);
                        fragment.setArguments(arguments);
                        switchFragment(fragment);
                        binding.poiDetailPopupWindowOsm.placeDetailsPopupWindowOsm.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(CONTEXT, "Please move closer to: " + name + " to make a Check-In!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                Toast.makeText(CONTEXT, "Please allow Voice to access your location!", Toast.LENGTH_LONG).show();
            }
        });

        binding.currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });


        ConstraintLayout commentsLayout = binding.poiDetailPopupWindowOsm.placeCommentsListPopup.placeCommentsPopup;
        LinearLayout detailsLayout = binding.poiDetailPopupWindowOsm.placeDetailsPopupWindowOsm;
        commentsLayout.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsLayout.setVisibility(View.GONE);
                detailsLayout.setVisibility(View.VISIBLE);
            }
        });
        binding.poiDetailPopupWindowOsm.reviewsOsmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsLayout.setVisibility(View.GONE);
                TextView placeNameComment = commentsLayout.findViewById(R.id.place_name_comments_list);
                TextView placeNameDetails = detailsLayout.findViewById(R.id.namePlaceDetail_osm);
                placeNameComment.setText(placeNameDetails.getText());
                commentsLayout.setVisibility(View.VISIBLE);
            }
        });

        client = getInstance(CONTEXT);
        CollectionReference collectionRef = client.getDb().collection(PLACES_COLLECTION_PATH);
        collectionRef.addSnapshotListener((value, error) -> {
            Long currentTimeInMilli = System.currentTimeMillis();
            Log.i(TAG, "TK_onEvent: START - 5.0 " + currentTimeInMilli);
            if (error != null) {
                Log.w(TAG, "Listen error", error);
            } else {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        PlaceInfo placeInfo = change.getDocument().toObject(PlaceInfo.class);
                        String placeInfoId = change.getDocument().getId();
                        placeInfo.setId(placeInfoId);
                        places.add(placeInfo);
                    }
                }
                addPlacesMarkers();
            }
            currentTimeInMilli = System.currentTimeMillis();
            Log.i(TAG, "TK_onEvent: END - 5.1 " + currentTimeInMilli);
        });
        Log.d(TAG, "TK_onViewCreated: END - 4.1");
    }

    private void openStreetMapInit() {
        // Needs extrenal storage to write tiles
        Log.d(TAG, "openStreetMapInit: START 6.0");
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

                if (ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Location location = getLastKnownLocation(CONTEXT);
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    map.getController().setCenter(geoPoint);
                    map.getController().setZoom(USER_ZOOM);
                    map.getController().animateTo(geoPoint);
                    map.invalidate();
                } else {
                    Log.i(TAG, "openStreetMapInit: No permission given for location. Setting location to EUROPE_BB.getCenterWithDateLine()");
                    IMapController controller = map.getController();
                    controller.setZoom(CONTINENT_ZOOM);
                    controller.animateTo(EUROPE_BB.getCenterWithDateLine());
                    controller.setCenter(EUROPE_BB.getCenterWithDateLine());
                }
                map.invalidate();
            });
        });

        Log.d(TAG, "openStreetMapInit: END 6.1");
    }

    private void switchFragment(CheckInFragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.osm_fragment_frame_layout_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack("OSM2CheckIn");
        fragmentTransaction.commit();
    }

    public void addPlacesMarkers() {
        //TODO: fix me; for loops
        if (places.size() > 0) {
            //FolderOverlay placesMarkers = new FolderOverlay(CONTEXT);
            ArrayList<GeoPoint> points = new ArrayList<>();
            Drawable markerIcon = ContextCompat.getDrawable(CONTEXT, R.drawable.ic_place);

            // get and clear old overlays
            final List<Overlay> overlays = map.getOverlays();
            overlays.clear();

            //Clusters
            RadiusMarkerClusterer poiMarkerCluster = new RadiusMarkerClusterer(CONTEXT);
            Bitmap clusterIcon = getBitmapFromVectorDrawable(CONTEXT, R.drawable.marker_cluster);
            poiMarkerCluster.setIcon(clusterIcon);
            // CLuster Design
            poiMarkerCluster.getTextPaint().setColor(Color.DKGRAY);
            poiMarkerCluster.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density); //taking into account the screen density
            poiMarkerCluster.mAnchorU = Marker.ANCHOR_RIGHT;
            poiMarkerCluster.mAnchorV = Marker.ANCHOR_BOTTOM;
            poiMarkerCluster.mTextAnchorV = 0.40f;
            poiMarkerCluster.setMaxClusteringZoomLevel(15);
            if (places.size() > 1) {
                map.getOverlays().add(poiMarkerCluster);
            }
            for (PlaceInfo place: places) {
                Marker marker = new Marker(map);
                marker.setInfoWindow(null);
                marker.setIcon(markerIcon);
                marker.setPosition(new GeoPoint(place.getLatLng().getLatitude(), place.getLatLng().getLongitude()));
                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    binding.checkInFAB.setVisibility(View.GONE);

                    // main container
                    binding.poiDetailPopupWindowOsm.namePlaceDetailOsm.setText(place.getName());
                    binding.poiDetailPopupWindowOsm.addressPlaceDetailOsm.setText(place.getAddress());
                    binding.poiDetailPopupWindowOsm.placeCustomWindowOsm.setVisibility(View.VISIBLE);

                    // progress bar - fetch result - continue to show view and hide spinner
                    binding.progressCircularOsmPlaceDetail.setVisibility(View.VISIBLE);
                    client.getCheckInForPlace(place.getId(), checkIns -> {
                        //zoom to marker
                        place.setCheckIns(checkIns);
                        int localVisits = 0;
                        int touristVisits = 0;
                        int localLikes = 0;
                        int touristLikes = 0;
                        int reviews = 0;
                        for (CheckIn checkin : place.getCheckIns()){
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
                            } if (null != checkin.getReview()) {
                                if (!checkin.getReview().equals("")) {
                                    reviews = reviews + 1;
                                }
                            }
                        }

                        Log.i(TAG, "getCheckInsCallBack: LocalVisits: " + localVisits);
                        Log.i(TAG, "getCheckInsCallBack: touristLikes: " + touristLikes);
                        Log.i(TAG, "getCheckInsCallBack: touristVisits: " + touristVisits);
                        Log.i(TAG, "getCheckInsCallBack: localLikes: " + localLikes);

                        RecyclerView commentsRecyclerView = binding.poiDetailPopupWindowOsm.placeCommentsListPopup.usersCommentsList;
                        RecyclerViewReviewsAdapter recyclerViewReviewsAdapter = new RecyclerViewReviewsAdapter(CONTEXT, checkIns);
                        commentsRecyclerView.setAdapter(recyclerViewReviewsAdapter);
                        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(CONTEXT));

                        binding.poiDetailPopupWindowOsm.localNumberOsm.setText(String.valueOf(localVisits));
                        binding.poiDetailPopupWindowOsm.localUpvotesOsm.setText(String.valueOf(localLikes));
                        binding.poiDetailPopupWindowOsm.touristNumberOsm.setText(String.valueOf(touristVisits));
                        binding.poiDetailPopupWindowOsm.touristLikesOsm.setText(String.valueOf(touristLikes));
                        binding.poiDetailPopupWindowOsm.reviewsCountOsm.setText(String.valueOf(reviews));
                        binding.progressCircularOsmPlaceDetail.setVisibility(View.GONE);

                        IMapController controller = map.getController();
                        controller.setZoom(USER_ZOOM);
                        controller.animateTo(new GeoPoint(place.getLatLng().getLatitude(), place.getLatLng().getLongitude()));
                        map.invalidate();
                    });
                    return false;
                });
                points.add(marker.getPosition());
                poiMarkerCluster.add(marker);
                overlays.add(poiMarkerCluster);

                MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(CONTEXT), map);
                myLocationNewOverlay.enableMyLocation();

                Bitmap bitmapNotMoving = getBitmapFromVectorDrawable(CONTEXT, R.drawable.ic_marker_man);
                Bitmap bitmapMoving = getBitmapFromVectorDrawable(CONTEXT, R.drawable.ic_current_location_circle);
                myLocationNewOverlay.setDirectionArrow( bitmapNotMoving, bitmapMoving);

                myLocationNewOverlay.enableMyLocation();
                overlays.add(myLocationNewOverlay);
                //placesMarkers.add(marker);
            }

            //map.getOverlays().add(placesMarkers);

            if (ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "addPlacesMarkers: Permission given, zooming to city zoom level with user location.");
                Location location = getLastKnownLocation(CONTEXT);
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                map.getController().setZoom(USER_ZOOM);
            } else {
                Log.i(TAG, "addPlacesMarkers: Permission not given yet, zooming to CONTINENT_ZOOM level with EUROPE_BB location.");
                BoundingBox box = BoundingBox.fromGeoPointsSafe(points);
                map.getController().setZoom(CONTINENT_ZOOM);
            }
            map.invalidate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_PRECISE_LOCATION)
        {
            // Request for location permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(CONTEXT, "Permission Granted.",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "TK_onRequestPermissionsResult: Permission Granted");
//                Location location = getLastKnownLocation(CONTEXT);
//                zoomToLocation(map, new GeoPoint(location.getLatitude(), location.getLongitude()));
            }
            else
            {
                // Permission request was denied.
                Toast.makeText(CONTEXT, "Permission Denied.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = getLastKnownLocation(CONTEXT);
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            map.getController().animateTo(geoPoint);
            map.invalidate();
        } else {
            // Permission is missing and must be requested.
            requestCurrentPosition();
        }
    }

    /**
     * Requests the {@link android.Manifest.permission#ACCESS_FINE_LOCATION} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestCurrentPosition(){
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            ConstraintLayout layout = getView().findViewById(R.id.osm_fragment_frame_layout_container);

            Snackbar.make(layout, R.string.location_permission_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, view -> {
                        // Request the permission
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_PRECISE_LOCATION);
                    }).show();
        } else {
            //Snackbar.make(mLayout, R.string.location_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_PRECISE_LOCATION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(CONTEXT,
                PreferenceManager.getDefaultSharedPreferences(CONTEXT));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().show();
        }
        if (map != null) {
            map.onResume();
            if (ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location location = getLastKnownLocation(CONTEXT);
                Log.i(TAG, "onResume: Location returned: " + location.getLongitude() + ", " + location.getLongitude());
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                IMapController controller = map.getController();
                controller.setZoom(USER_ZOOM);
                controller.animateTo(geoPoint);
            } else {
                IMapController mapController = map.getController();
                mapController.setCenter(EUROPE_BB.getCenterWithDateLine());
                mapController.setZoom(CONTINENT_ZOOM);
            }
            map.invalidate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(CONTEXT,
                PreferenceManager.getDefaultSharedPreferences(CONTEXT));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().hide();
        }
        if (map != null) {
            map.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static PlaceInfo findByMarkerIndex(Collection<PlaceInfo> listPlaces, int markerIndex) {
        return listPlaces.stream().filter(placeInfo -> markerIndex == placeInfo.getMarkerIndex()).findFirst().orElse(null);
        //Ref: https://stackoverflow.com/questions/17526608/how-to-find-an-object-in-an-arraylist-by-property
    }

    @Override
    public void onCommentClick(View view, int position) {
        TextView textView = view.findViewById(R.id.userNameTextView);
        Toast.makeText(CONTEXT, "Comment posted by: " + textView.getText(), Toast.LENGTH_SHORT).show();
    }
}