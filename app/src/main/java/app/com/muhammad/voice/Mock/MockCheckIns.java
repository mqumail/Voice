package app.com.muhammad.voice.Mock;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.muhammad.voice.DTO.CheckinInfo;
import app.com.muhammad.voice.DTO.PlaceInfo;

public class MockCheckIns {

    private static MockCheckIns mockCheckInsInstance;
    private List<PlaceInfo> placesInformation = new ArrayList<PlaceInfo>();

    private MockCheckIns() {
    }

    public static MockCheckIns getInstance(){
        if (mockCheckInsInstance == null){
            mockCheckInsInstance = new MockCheckIns();
        }
        return mockCheckInsInstance;
    }

    public List<PlaceInfo> getPlacesInformation()
    {
        if (placesInformation.isEmpty()){
            placesInformation.add(addMockPlaceToPlaceInformationList());
        }
        return placesInformation;
    }

    public void setPlaceInformation(List<PlaceInfo> placeInformation)
    {
        this.placesInformation = placeInformation;
    }

    public void addToPlaceInformationList(PlaceInfo placeInfo) {
        if (placesInformation.contains(placeInfo)) {
            PlaceInfo updatePlace = placesInformation.get(placesInformation.indexOf(placeInfo));
            updatePlace.addCheckInInfo(placeInfo.getCheckinInfos().get(0));
        }
        else {
            placesInformation.add(placeInfo);
        }
    }

    public PlaceInfo addMockPlaceToPlaceInformationList(){
        List<CheckinInfo> checkinInfos = new ArrayList<>();
        checkinInfos.add(checkinInfoOne);
        checkinInfos.add(checkinInfoTwo);
        checkinInfos.add(checkinInfoThree);
        placeInfoAnnaAmalia.setCheckinInfos(checkinInfos);

        return placeInfoAnnaAmalia;
    }

    static PlaceInfo placeInfoAnnaAmalia = new PlaceInfo("Duchess Anna Amalia Library", "Platz der Demokratie 1, 99423 Weimar, Germany", "+49 3643 545400", "ChIJvUagEdsapEcRE1CWMkbSNtI",
            Uri.parse("http://www.klassik-stiftung.de/einrichtungen/herzogin-anna-amalia-bibliothek/"), new LatLng(50.9785158, 11.3322211), 4.400000095367432f,
            "", "", true, true, true);
    static CheckinInfo checkinInfoOne = new CheckinInfo(false, true, false, "Anonymous", "Amazing Library", new Timestamp(new Date("23/09/2020")));
    static CheckinInfo checkinInfoTwo = new CheckinInfo(true, false, true, "Moh", "Historic Place in Weimar", new Timestamp(new Date("23/01/2021")));
    static CheckinInfo checkinInfoThree = new CheckinInfo(true, true, true, "Luis", "Amazing Library", new Timestamp(new Date("23/04/2019")));
}
