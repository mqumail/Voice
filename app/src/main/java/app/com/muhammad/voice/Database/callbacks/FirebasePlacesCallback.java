package app.com.muhammad.voice.Database.callbacks;

import java.util.ArrayList;

import app.com.muhammad.voice.DTO.PlaceInfo;

public interface FirebasePlacesCallback {
    void getPlacesCallBack(ArrayList<PlaceInfo> places);
}
