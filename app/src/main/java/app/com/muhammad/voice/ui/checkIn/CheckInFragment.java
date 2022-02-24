package app.com.muhammad.voice.ui.checkIn;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentCheckInBinding;
import app.com.muhammad.voice.utils.CustomInfoWindow;

import static app.com.muhammad.voice.utils.ConstantsVariables.MY_USER_AGENT;
import static app.com.muhammad.voice.utils.UiHelperMethods.getBitmapFromVectorDrawable;

public class CheckInFragment extends Fragment
{
    private CheckInViewModel viewModel;
    private FragmentCheckInBinding binding;
    private MapView map;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private ArrayList<POI> POIs;

    ListView nearByPlacesListView;

    public CheckInFragment()
    {
        // Required empty public constructor
    }

    public static CheckInFragment newInstance()
    {
        return new CheckInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(CheckInViewModel.class);
        binding = FragmentCheckInBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nearByPlacesListView = binding.nearByPlaces;
        binding.menuButtonCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = getActivity().findViewById(R.id.base_drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        openStreetMapInit();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.menu_button).setVisibility(View.GONE);
        getActivity().findViewById(R.id.activity_base_tool_bar).setVisibility(View.GONE);

        binding.toolbarCheckInLayout.toolbarCheckIn.inflateMenu(R.menu.check_in_menu);
    }

    private void searchOnOsm(String query) {

        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(() -> {
            //Set user agent
            final Context context = getActivity();
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            Configuration.getInstance().setUserAgentValue(MY_USER_AGENT);

            // Weimar BoundingBox
            BoundingBox weimarBoundingBox = new BoundingBox();
            weimarBoundingBox.set(50.9847962773, 11.3484415648, 50.9716374502, 11.3235935805);

//            IMapController mapController = map.getController();
//            mapController.setZoom(17.0);
//            mapController.setCenter(weimarBoundingBox.getCenterWithDateLine());

            OverpassAPIProvider overpassAPIProvider = new OverpassAPIProvider();
            String url = overpassAPIProvider.urlForPOISearch("amenity="+ query, weimarBoundingBox, 50, 15);

            // Get POIs
            POIs = overpassAPIProvider.getPOIsFromUrl(url);

            if (POIs != null && POIs.size() > 0){
                handler.post(() -> {

                    // TODO: Populate the list
                    ArrayList<String> descriptionPOIs = new ArrayList<String>();

                    for (POI poi: POIs) {
                        descriptionPOIs.add(poi.mType);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, descriptionPOIs);
                    nearByPlacesListView.setAdapter(arrayAdapter);

                    RadiusMarkerClusterer poiMarkerCluster = new RadiusMarkerClusterer(getActivity());
                    if (POIs.size() > 1) {
                        Bitmap clusterIcon = getBitmapFromVectorDrawable(getActivity(), R.drawable.marker_cluster);
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
                    Drawable poiIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_poi_default, null);
                    for (POI poi : POIs){
                        Marker poiMarker = new Marker(map);
                        poiMarker.setTitle(poi.mType);
                        poiMarker.setSnippet(poi.mDescription);
                        poiMarker.setPosition(poi.mLocation);
                        poiMarker.setIcon(poiIcon);
                        if (poi.mThumbnail != null){
                            poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
                        }
                        poiMarker.setInfoWindow(new CustomInfoWindow(map));
                        poiMarker.setRelatedObject(poi);
                        poiMarkerCluster.add(poiMarker);
                    }
                    // TODO: figure out the which are the outer most POIs and then creating a bounding box there, move camera to there
                    map.invalidate();
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

                //My Location Overlay
                MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), map);
                myLocationNewOverlay.enableMyLocation();
                map.getOverlays().add(myLocationNewOverlay);

                IMapController mapController = map.getController();
                mapController.setZoom(17.0);

                //TODO: This is hard coded location, use users actual location
                mapController.setCenter(new GeoPoint(50.978284, 11.340627));
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
                    searchOnOsm(query);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getActivity().findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.activity_base_tool_bar).setVisibility(View.VISIBLE);

        binding = null;
    }
}