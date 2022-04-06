package app.com.muhammad.voice.util;

import static android.content.Context.LOCATION_SERVICE;
import static app.com.muhammad.voice.util.Constants.CITY_ZOOM;

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

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.com.muhammad.voice.DTO.PlaceInfo;

public class LocationHelper
{
    public static LocationManager locationManager;
    private static LocationProvider provider;
    private static List<String> providers;
    private static LocationListener listener;
    Location bestLocation = null;

    public static Location getLastKnownLocation(Context activity) {
        locationManager = (LocationManager) activity.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Checking Permission LocationHandler", "No permission given, ask user for permissions.");
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) { continue; }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l; // Found best last known location: %s", l);
            }
        }
        return bestLocation;
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

    public static void zoomToLocation(MapView map, org.osmdroid.util.GeoPoint geoPoint) {
        IMapController mapController = map.getController();
        mapController.setCenter(geoPoint);
        mapController.setZoom(CITY_ZOOM);
        mapController.animateTo(geoPoint);
        map.invalidate();
    }

    public static boolean allowedToCheckIn(String address, Context context) throws IOException {

        PlaceInfo place = new PlaceInfo();
        place.setAddress(address);
        GeoPoint geoPointOfPlace = getLatLong(place, context);

        Location placeLocation = new Location("Place");
        placeLocation.setLatitude(geoPointOfPlace.getLatitude());
        placeLocation.setLongitude(geoPointOfPlace.getLongitude());

        Location userLocation = getLastKnownLocation(context);
        double distance = userLocation.distanceTo(placeLocation);

        return !(distance > 10);
    }

    public static double distanceToFromCurrentLocation(GeoPoint geoPoint, Context context) {
        Location userLocation = getLastKnownLocation(context);
        Location placeLocation = new Location("Place");
        placeLocation.setLatitude(geoPoint.getLatitude());
        placeLocation.setLongitude(geoPoint.getLongitude());
        double distance = userLocation.distanceTo(placeLocation);
        return distance;
    }
}
