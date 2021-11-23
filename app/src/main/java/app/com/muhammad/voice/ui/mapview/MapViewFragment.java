package app.com.muhammad.voice.ui.mapview;

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
import app.com.muhammad.voice.databinding.FragmentMapViewBinding;

import static app.com.muhammad.voice.utils.ConstantsVariables.COARSE_LOCATION_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.FINE_LOCATION_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.INTERNET_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.MY_USER_AGENT;
import static app.com.muhammad.voice.utils.ConstantsVariables.NETWORK_STATE_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.WIFI_STATE_PERMISSION_CODE;
import static app.com.muhammad.voice.utils.ConstantsVariables.WRITE_EXTERNAL_STORAGE_PERMISSION_CODE;

public class MapViewFragment extends Fragment
{

    private MapViewViewModel mapViewViewModel;
    private FragmentMapViewBinding binding;
    private MapView map;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        mapViewViewModel =
                new ViewModelProvider(this).get(MapViewViewModel.class);

        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        osmInit();

        return root;
    }

    private void osmInit(){

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

                map = getView().findViewById(R.id.map);
                map.setTileSource(TileSourceFactory.MAPNIK);
                map.setMultiTouchControls(true);

                // My location
                MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), map);
                myLocationNewOverlay.enableMyLocation();
                map.getOverlays().add(myLocationNewOverlay);

                IMapController mapController = map.getController();
                mapController.setZoom(9);
                mapController.setCenter(new GeoPoint(50.9846195739, 11.3378999626));
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
                    Toast.makeText(getActivity(), "Coarse Location Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case FINE_LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(), "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case WIFI_STATE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(), "WIFI Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case NETWORK_STATE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(), "Network Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case INTERNET_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(), "Internet Permission Granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}