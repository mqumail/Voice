package app.com.muhammad.voice.util;

import org.osmdroid.util.BoundingBox;

public class Constants {

    public static final String MAP_VIEW_OBJECT = "mapViewObject";
    public static final String OSM_FRAGMENT = "OsmFragment";
    public static final String PROFILE_FRAGMENT = "ProfileFragment";
    public static final String SETTINGS_FRAGMENT = "SettingsFragment";
    public static final String FRAGMENT_TO_LOAD = "FragToLoad";

    public static final int PERMISSION_REQUEST_PRECISE_LOCATION = 0;

    public static final String MY_PREFERENCES = "my_preferences";

    public static final String USER_INFO_KEY = "userInfoKey";
    public static final String LOCAL_CITY_KEY = "localCityKey";
    public static final String SHOW_ON_BOARD_KEY = "ShowOnBoardKey";

    public static final double STARTING_ZOOM_LEVEL = 5.0;
    public static final double MIDDLE_ZOOM_LEVEL = 17.0;
    public static final BoundingBox EUROPE_BB = new BoundingBox(59.534318,51.064453,31.203405,-11.162109);

    public static final String PLACE_TO_CHECK_IN = "PlaceToCheckIn";


    // Firestore collection's name
    public static final String PLACES_COLLECTION_PATH = "placesCollection";
    public static final String CHECK_INS_COLLECTION_PATH = "CheckInsCollection";

}