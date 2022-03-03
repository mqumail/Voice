package app.com.muhammad.voice.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.GeoPoint;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.BoundingBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.com.muhammad.voice.DTO.PlaceInfo;

//ref: https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/location/currentlocation.html
public class LocationHelper {
    public static LocationManager locationManager;
    private static LocationProvider provider;
    private static LocationListener listener;

    private static Location currentLocation;

    public static Location getCurrentUserLocation(Context activity) {

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);

        // A new location update is received.  Do something useful with it.  In this case,
        // we're sending the update to a handler which then updates the UI with the new
        // location.
        listener = LocationHelper::setCurrentLocation;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Checking Permission LocationHandler", "No permission given, ask user for permissions.");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);

        return currentLocation != null ? currentLocation : locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public static BoundingBox getPOIsBoundingBox(ArrayList<POI> pois){
        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLong = Integer.MAX_VALUE;
        double maxLong = Integer.MIN_VALUE;

        for (POI poi : pois) {
            org.osmdroid.util.GeoPoint point = poi.mLocation;
            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }

        return new BoundingBox(maxLat, maxLong, minLat, minLong);
    }

    public static BoundingBox getCurrentLocationBoundingBox(Location location){
        return new BoundingBox(location.getLatitude() + 0.025, location.getLongitude() + 0.025,
                location.getLatitude() - 0.025, location.getLongitude() - 0.025);
    }

    private static void setCurrentLocation(Location location) {
        currentLocation = location;
    }

    public static void removeListener() {
        locationManager.removeUpdates(listener);
    }

    public static GeoPoint getLatLong(PlaceInfo place, Context context) throws IOException {
        double latitude;
        double longitude;
        GeoPoint geoPoint;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocationName(place.getAddress(), 1);
        if (addresses.size() >  0)
        {
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();

            geoPoint = new GeoPoint(latitude, longitude);
            return geoPoint;
        }
            return null;
    }

    public static List<Address> getAddresses(POI place, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        addresses = geocoder.getFromLocation(place.mLocation.getLatitude(), place.mLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        return addresses;
    }
}
