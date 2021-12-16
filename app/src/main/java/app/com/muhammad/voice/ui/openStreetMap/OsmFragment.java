package app.com.muhammad.voice.ui.openStreetMap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentOsmBinding;

import static app.com.muhammad.voice.utils.ConstantsVariables.COARSE_LOCATION_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.FINE_LOCATION_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.INTERNET_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.MY_USER_AGENT;
import static app.com.muhammad.voice.utils.ConstantsVariables.NETWORK_STATE_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.WIFI_STATE_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.WRITE_EXTERNAL_STORAGE_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.UiHelperMethods.replaceContentContainer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OsmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OsmFragment extends Fragment
{
    private OsmViewModel viewModel;
    private FragmentOsmBinding binding;
    private MapView map;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OsmFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OsmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OsmFragment newInstance(String param1, String param2)
    {
        OsmFragment fragment = new OsmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(OsmViewModel.class);
        binding = FragmentOsmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton checkInFAB = binding.checkInFAB;
        checkInFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start CheckIn Fragment
                replaceContentContainer(R.id.nav_check_in, getActivity().getSupportFragmentManager());
            }
        });

        openStreetMapInit();

        return root;
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
                //UI thread here, after osmInit
                // Check Permissions
                // TODO: just check permissions only when it is needed
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE);
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE);
                checkPermission(Manifest.permission.INTERNET, INTERNET_PERMISSION_CODE);
                checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, NETWORK_STATE_PERMISSION_CODE);
                checkPermission(Manifest.permission.ACCESS_WIFI_STATE, WIFI_STATE_PERMISSION_CODE);
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);

                map = binding.map;
                map.setTileSource(TileSourceFactory.MAPNIK);
                map.setMultiTouchControls(true);

                // My location
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

    //TODO: Take care of these deprecated methods
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case COARSE_LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(getActivity(), "Coarse Location Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case FINE_LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(getActivity(), "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case WIFI_STATE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(getActivity(), "WIFI Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case NETWORK_STATE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(getActivity(), "Network Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case INTERNET_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(getActivity(), "Internet Permission Granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}